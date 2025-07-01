package com.todo.chrono.service.workspaceService;

import com.todo.chrono.util.error.IdInvalidException;
import com.todo.chrono.dto.request.TaskBriefDTO;
import com.todo.chrono.dto.request.UserDTO;
import com.todo.chrono.dto.request.WorkspaceDTO;
import com.todo.chrono.repository.WorkspaceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.todo.chrono.entity.User;
import com.todo.chrono.entity.Workspace;
import com.todo.chrono.enums.Role;
import com.todo.chrono.mapper.WorkspaceMapper;
import com.todo.chrono.repository.UserRepository;
import com.todo.chrono.mapper.UserMapper;
import java.time.LocalDateTime;
import com.todo.chrono.dto.request.WorkspaceCreateDTO;
import com.todo.chrono.repository.WorkspaceMemberRepository;
import com.todo.chrono.entity.WorkspaceMember;
import com.todo.chrono.enums.RoleWorkspaceMember;
import com.todo.chrono.repository.TaskRepository;
import com.todo.chrono.enums.TaskStatus;
import com.todo.chrono.enums.WorkspaceStatus;
import com.todo.chrono.util.AccountUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {

    private WorkspaceRepository workspaceRepository;
    private UserRepository userRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final TaskRepository taskRepository;
    private final AccountUtil accountUtil;

    @Override
    public WorkspaceDTO createWorkspace(WorkspaceCreateDTO workspaceCreateDTO, Integer user_id)
            throws IdInvalidException {
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new IdInvalidException("User: " + user_id + " not found"));

        // Nếu role là FREE thì giới hạn số workspace
        if (user.getRole().equals(Role.FREE)) {
            int currentWorkspaceCount = workspaceRepository.countByUserId(user_id);
            if (currentWorkspaceCount >= 2) {
                throw new IdInvalidException("User FREE với id = " + user_id + " chỉ được tạo tối đa 2 workspace.");
            }
        }

        // Kiểm tra tên workspace đã tồn tại với user này
        if (workspaceRepository.existsByUserIdAndName(user_id, workspaceCreateDTO.getName())) {
            throw new IdInvalidException(
                    "Workspace với tên = " + workspaceCreateDTO.getName() + " đã tồn tại trong User id = " + user_id);
        }

        Workspace workspace = WorkspaceMapper.mapToWorkspace(workspaceCreateDTO);
        workspace.setUser(user);
        workspace.setCreatedAt(LocalDateTime.now());
        workspace.setUpdatedAt(LocalDateTime.now());

        Workspace savedWorkspace = workspaceRepository.save(workspace);
        WorkspaceMember member = new WorkspaceMember();
        member.setWorkspace(savedWorkspace);
        member.setUser(user);
        member.setRole(RoleWorkspaceMember.OWNER);
        member.setJoinedAt(LocalDateTime.now());
        workspaceMemberRepository.save(member);
        return WorkspaceMapper.mapToWorkspaceDTO(savedWorkspace);
    }

    @Override
    public WorkspaceDTO getWorkspaceById(Integer workspace_id) throws IdInvalidException {
        Optional<Workspace> workspace = workspaceRepository.findById(workspace_id);
        if (workspace.isPresent()) {
            WorkspaceDTO dto = WorkspaceMapper.mapToWorkspaceDTO(workspace.get());
            int total = taskRepository.countByWorkspaceId(workspace_id);
            int completed = taskRepository.countByWorkspaceIdAndStatus(workspace_id, TaskStatus.COMPLETED);
            int progress = total == 0 ? 0 : (int) ((double) completed / total * 100);
            dto.setProgress(progress);
            return dto;
        } else {
            throw new IdInvalidException("Workspace với id = " + workspace_id + " không tồn tại");
        }

    }

    @Override
    public WorkspaceDTO updateWorkspace(WorkspaceCreateDTO updateWorkspace, Integer workspace_id)
            throws IdInvalidException {
        // 1. Tìm workspace
        Workspace workspace = workspaceRepository.findById(workspace_id)
                .orElseThrow(() -> new IdInvalidException("Workspace với id = " + workspace_id + " không tồn tại"));

        // 2. Lấy user hiện tại
        User currentUser = accountUtil.getCurrentUser();

        // 3. Kiểm tra quyền OWNER
        WorkspaceMember member = workspaceMemberRepository
                .findByWorkspaceIdAndUserId(workspace_id, currentUser.getId())
                .orElseThrow(() -> new IdInvalidException("Bạn không phải thành viên của workspace này"));

        if (member.getRole() != RoleWorkspaceMember.OWNER) {
            throw new IdInvalidException("Chỉ OWNER mới có quyền chỉnh sửa workspace.");
        }

        // 4. Xử lý cập nhật
        String newName = updateWorkspace.getName();
        String currentName = workspace.getName();

        if (newName != null) {
            if (!newName.equals(currentName)
                    && workspaceRepository.existsByUserIdAndName(workspace.getUser().getId(), newName)) {
                throw new IdInvalidException("Workspace với tên = " + newName + " đã tồn tại trong User id = "
                        + workspace.getUser().getId());
            }
            workspace.setName(newName);
        }

        if (updateWorkspace.getDescription() != null) {
            workspace.setDescription(updateWorkspace.getDescription());
        }

        if (updateWorkspace.getStatus() != null) {
            if (updateWorkspace.getStatus() == WorkspaceStatus.COMPLETED) {
                int totalTasks = taskRepository.countByWorkspaceId(workspace_id);
                int completedTasks = taskRepository.countByWorkspaceIdAndStatus(workspace_id, TaskStatus.COMPLETED);
                if (totalTasks == 0 || completedTasks < totalTasks) {
                    throw new IdInvalidException("Không thể chuyển sang COMPLETED khi còn task chưa hoàn thành.");
                }
            }
            workspace.setStatus(updateWorkspace.getStatus());
        }

        workspace.setUpdatedAt(LocalDateTime.now());
        Workspace updated = workspaceRepository.save(workspace);

        return WorkspaceMapper.mapToWorkspaceDTO(updated);
    }

    @Override
    public void deleteWorkspace(Integer workspace_id) throws IdInvalidException {
        // 1. Tìm workspace
        Workspace workspace = workspaceRepository.findById(workspace_id)
                .orElseThrow(() -> new IdInvalidException("Workspace với id = " + workspace_id + " không tồn tại"));

        // 2. Lấy user hiện tại
        User currentUser = accountUtil.getCurrentUser();

        // 3. Kiểm tra xem user có phải thành viên và là OWNER hay không
        WorkspaceMember member = workspaceMemberRepository
                .findByWorkspaceIdAndUserId(workspace_id, currentUser.getId())
                .orElseThrow(() -> new IdInvalidException("Bạn không phải thành viên của workspace này"));

        if (member.getRole() != RoleWorkspaceMember.OWNER) {
            throw new IdInvalidException("Chỉ OWNER mới có quyền xóa workspace.");
        }

        // 4. Xóa workspace
        workspaceRepository.deleteById(workspace_id);
    }

    @Override
    public List<WorkspaceDTO> getWorkspacesByUserId(int userId) throws IdInvalidException {
        List<WorkspaceMember> memberEntries = workspaceMemberRepository.findByUserId(userId);

        if (memberEntries.isEmpty()) {
            throw new IdInvalidException("User id = " + userId + " hiện không là thành viên của bất kỳ workspace nào");
        }

        return memberEntries.stream().map(member -> {
            Workspace ws = member.getWorkspace();
            WorkspaceDTO dto = WorkspaceMapper.mapToWorkspaceDTO(ws);

            int total = taskRepository.countByWorkspaceId(ws.getId());
            int completed = taskRepository.countByWorkspaceIdAndStatus(ws.getId(), TaskStatus.COMPLETED);
            int progress = total == 0 ? 0 : (int) ((double) completed / total * 100);
            dto.setProgress(progress);

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserIdByWorkspaceId(int workspace_id) {
        Workspace workspace = workspaceRepository.findWorkspaceById(workspace_id);
        return UserMapper.mapToUserDTO(workspace.getUser());
    }

    @Override
    public List<WorkspaceDTO> getWorkspaceAll() {
        List<Workspace> workspaces = workspaceRepository.findAll();
        return workspaces.stream().map(ws -> {
            WorkspaceDTO dto = WorkspaceMapper.mapToWorkspaceDTO(ws);
            int total = taskRepository.countByWorkspaceId(ws.getId());
            int completed = taskRepository.countByWorkspaceIdAndStatus(ws.getId(), TaskStatus.COMPLETED);
            int progress = total == 0 ? 0 : (int) ((double) completed / total * 100);
            dto.setProgress(progress);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public int getWorkspaceProgress(int workspaceId) throws IdInvalidException {
        if (!workspaceRepository.existsById(workspaceId)) {
            throw new IdInvalidException("Workspace id = " + workspaceId + " không tồn tại");
        }

        int totalTasks = taskRepository.countByWorkspaceId(workspaceId);
        if (totalTasks == 0)
            return 0;

        int completedTasks = taskRepository.countByWorkspaceIdAndStatus(workspaceId, TaskStatus.COMPLETED);
        return (int) ((double) completedTasks / totalTasks * 100);
    }

    @Override
    public List<WorkspaceDTO> getWorkspacesInProgressByUser(int userId) {
        List<Workspace> userWorkspaces = workspaceRepository.findAllByUserId(userId);
        return userWorkspaces.stream()
                .filter(ws -> {
                    int total = taskRepository.countByWorkspaceId(ws.getId());
                    int done = taskRepository.countByWorkspaceIdAndStatus(ws.getId(), TaskStatus.COMPLETED);
                    return total > 0 && done < total;
                })
                .map(ws -> {
                    WorkspaceDTO dto = WorkspaceMapper.mapToWorkspaceDTO(ws);
                    int total = taskRepository.countByWorkspaceId(ws.getId());
                    int completed = taskRepository.countByWorkspaceIdAndStatus(ws.getId(), TaskStatus.COMPLETED);
                    int progress = total == 0 ? 0 : (int) ((double) completed / total * 100);
                    dto.setProgress(progress);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public int countCompletedWorkspacesByUser(int userId) throws IdInvalidException {
        List<Workspace> workspaces = workspaceRepository.findWorkspacesByUserId(userId);
        if (workspaces.isEmpty()) {
            return 0;
        }

        int count = 0;

        for (Workspace ws : workspaces) {
            int total = taskRepository.countByWorkspaceId(ws.getId());
            int completed = taskRepository.countByWorkspaceIdAndStatus(ws.getId(), TaskStatus.COMPLETED);

            if (total > 0 && total == completed) {
                count++;
            }
        }

        return count;
    }

    @Override
    public void updateWorkspaceStatusIfNeeded(Integer workspaceId) {
        int totalTasks = taskRepository.countByWorkspaceId(workspaceId);

        if (totalTasks == 0)
            return; // Không cập nhật nếu chưa có task

        int completedTasks = taskRepository.countByWorkspaceIdAndStatus(workspaceId, TaskStatus.COMPLETED);
        Workspace workspace = workspaceRepository.findById(workspaceId).orElse(null);

        if (workspace != null) {
            if (completedTasks == totalTasks) {
                workspace.setStatus(WorkspaceStatus.COMPLETED);
            } else {
                workspace.setStatus(WorkspaceStatus.PENDING);
            }
            workspace.setUpdatedAt(LocalDateTime.now());
            workspaceRepository.save(workspace);
        }
    }

    @Override
    public int countUncompletedWorkspacesByUser(int userId) {
        return workspaceRepository.countByUserIdAndStatusNot(userId, WorkspaceStatus.COMPLETED);
    }
}