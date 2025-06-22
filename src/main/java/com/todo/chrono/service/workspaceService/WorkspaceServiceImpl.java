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
            return WorkspaceMapper.mapToWorkspaceDTO(workspace.get());
        } else {
            throw new IdInvalidException("Workspace với id = " + workspace_id + " không tồn tại");
        }

    }

    @Override
    public WorkspaceDTO updateWorkspace(WorkspaceCreateDTO updateWorkspace, Integer workspace_id)
            throws IdInvalidException {
        Workspace workspace = workspaceRepository.findById(workspace_id)
                .orElseThrow(() -> new IdInvalidException("Workspace với id = " + workspace_id + " không tồn tại"));

        String newName = updateWorkspace.getName();
        String currentName = workspace.getName();

        if (newName != null) {
            // Nếu tên mới khác tên hiện tại và đã tồn tại tên này trong workspace của user
            if (!newName.equals(currentName)
                    && workspaceRepository.existsByUserIdAndName(workspace.getUser().getId(), newName)) {
                throw new IdInvalidException("Workspace với tên = " + newName + " đã tồn tại trong User id = "
                        + workspace.getUser().getId());
            }

            // Cập nhật tên nếu hợp lệ
            workspace.setName(newName);
        }
        workspace.setUpdatedAt(LocalDateTime.now());
        Workspace updateWorkspaceObj = workspaceRepository.save(workspace);
        return WorkspaceMapper.mapToWorkspaceDTO(updateWorkspaceObj);
    }

    @Override
    public void deleteWorkspace(Integer workspace_id) throws IdInvalidException {
        Workspace workspace = workspaceRepository.findById(workspace_id)
                .orElseThrow(() -> new IdInvalidException("Workspace với id = " + workspace_id + " không tồn tại"));
        workspaceRepository.deleteById(workspace_id);
    }

    @Override
    public List<WorkspaceDTO> getWorkspacesByUserId(int user_id) throws IdInvalidException {
        List<Workspace> workspaces = workspaceRepository.findWorkspacesByUserId(user_id);
        if (workspaces == null) {
            throw new IdInvalidException("Trong User id = " + user_id + " hiện không có workspace");
        }
        return workspaces.stream().map(
                (workspace) -> WorkspaceMapper.mapToWorkspaceDTO(workspace)).collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserIdByWorkspaceId(int workspace_id) {
        Workspace workspace = workspaceRepository.findWorkspaceById(workspace_id);
        return UserMapper.mapToUserDTO(workspace.getUser());
    }

    @Override
    public List<WorkspaceDTO> getWorkspaceAll() {
        List<Workspace> workspaces = workspaceRepository.findAll();
        return workspaces.stream().map(
                (workspace) -> WorkspaceMapper.mapToWorkspaceDTO(workspace)).collect(Collectors.toList());
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
                .map(WorkspaceMapper::mapToWorkspaceDTO)
                .collect(Collectors.toList());
    }
    

}