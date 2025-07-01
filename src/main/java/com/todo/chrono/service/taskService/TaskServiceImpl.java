package com.todo.chrono.service.taskService;

import com.todo.chrono.util.error.IdInvalidException;
import com.todo.chrono.dto.request.UserDTO;
import com.todo.chrono.dto.request.TaskDTO;
import com.todo.chrono.dto.request.WorkspaceDTO;
import com.todo.chrono.repository.WorkspaceRepository;
import com.todo.chrono.dto.request.TaskBriefDTO;
import com.todo.chrono.dto.request.TaskCreateDTO;
import lombok.AllArgsConstructor;
import com.todo.chrono.entity.Task;
import com.todo.chrono.repository.TaskRepository;
import com.todo.chrono.entity.Workspace;
import com.todo.chrono.enums.Role;
import com.todo.chrono.mapper.TaskMapper;
import com.todo.chrono.mapper.WorkspaceMapper;
import com.todo.chrono.repository.UserRepository;
import com.todo.chrono.mapper.UserMapper;
import java.time.LocalDateTime;
import com.todo.chrono.enums.TaskStatus;
import org.springframework.stereotype.Service;
import com.todo.chrono.service.workspaceService.WorkspaceService;
import com.todo.chrono.entity.User;
import com.todo.chrono.repository.TaskAssigneeRepository;
import java.util.Collections;
import com.todo.chrono.util.AccountUtil;
import com.todo.chrono.repository.WorkspaceMemberRepository;
import com.todo.chrono.entity.WorkspaceMember;
import com.todo.chrono.enums.RoleWorkspaceMember;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private WorkspaceRepository workspaceRepository;
    private TaskRepository taskRepository;
    private WorkspaceService workspaceService;
    private UserRepository userRepository;
    private TaskAssigneeRepository taskAssigneeRepository;
    private AccountUtil accountUtil;
    private WorkspaceMemberRepository workspaceMemberRepository;

    @Override
    public TaskDTO createTask(TaskCreateDTO taskCreateDTO, Integer workspace_id) throws IdInvalidException {
        // 1. Lấy workspace
        Workspace workspace = workspaceRepository.findById(workspace_id)
                .orElseThrow(() -> new IdInvalidException("Workspace: " + workspace_id + " không tồn tại"));

        // 2. Lấy người dùng hiện tại
        User currentUser = accountUtil.getCurrentUser();
        Integer currentUserId = currentUser.getId();

        // 3. Kiểm tra xem user có phải là thành viên workspace không
        WorkspaceMember member = workspaceMemberRepository
                .findByWorkspaceIdAndUserId(workspace_id, currentUserId)
                .orElseThrow(() -> new IdInvalidException("Bạn không phải là thành viên của workspace này"));

        // 4. Kiểm tra quyền (chỉ OWNER và MEMBER mới được tạo task)
        if (member.getRole() != RoleWorkspaceMember.OWNER && member.getRole() != RoleWorkspaceMember.MEMBER) {
            throw new IdInvalidException("Bạn không có quyền tạo task trong workspace này");
        }

        // 5. Nếu chủ sở hữu workspace là FREE thì giới hạn số task
        if (workspace.getUser().getRole().equals(Role.FREE)) {
            int currentTaskCount = taskRepository.countByWorkspaceId(workspace_id);
            if (currentTaskCount >= 5) {
                throw new IdInvalidException("Người dùng FREE chỉ được tạo tối đa 5 task trong 1 workspace");
            }
        }

        // 6. Kiểm tra task trùng tên
        if (taskRepository.existsByWorkspaceIdAndTitle(workspace_id, taskCreateDTO.getTitle())) {
            throw new IdInvalidException("Task với tên này đã tồn tại trong workspace");
        }

        // 7. Tạo và lưu task
        Task task = TaskMapper.mapToTask(taskCreateDTO);
        task.setWorkspace(workspace);
        task.setCreatedAt(LocalDateTime.now());

        Task savedTask = taskRepository.save(task);
        workspaceService.updateWorkspaceStatusIfNeeded(workspace_id);

        return TaskMapper.mapToTaskDTO(savedTask);
    }

    @Override
    public TaskDTO getTaskById(Integer task_id) throws IdInvalidException {
        Task task = taskRepository.findById(task_id)
                .orElseThrow(() -> new IdInvalidException("Task với id = " + task_id + " không tồn tại"));

        boolean isPremium = task.getWorkspace().getUser().getRole() == Role.PREMIUM;
        List<User> assignees = isPremium
                ? taskAssigneeRepository.findUsersByTaskId(task_id)
                : Collections.emptyList();

        return TaskMapper.mapToTaskDTO(task, isPremium, assignees);

    }

    @Override
    public TaskDTO updateTask(TaskCreateDTO updateTask, Integer task_id) throws IdInvalidException {
        Task task = taskRepository.findById(task_id)
                .orElseThrow(() -> new IdInvalidException("Task " + task_id + " không tồn tại"));

        Integer workspaceId = task.getWorkspace().getId();
        User currentUser = accountUtil.getCurrentUser();

        WorkspaceMember member = workspaceMemberRepository
                .findByWorkspaceIdAndUserId(workspaceId, currentUser.getId())
                .orElseThrow(() -> new IdInvalidException("Bạn không phải thành viên của workspace này"));

        boolean isOwner = member.getRole().equals(RoleWorkspaceMember.OWNER);

        String newName = updateTask.getTitle();
        String currentName = task.getTitle();

        if (!isOwner) {
            // Chỉ cho phép đổi status nếu không phải OWNER
            if (updateTask.getStatus() != null) {
                task.setStatus(updateTask.getStatus());
            } else {
                throw new IdInvalidException("Chỉ OWNER mới có quyền chỉnh sửa thông tin task ngoài STATUS");
            }
        } else {
            // OWNER được phép chỉnh toàn bộ
            if (newName != null && !newName.equals(currentName)
                    && taskRepository.existsByWorkspaceIdAndTitle(workspaceId, newName)) {
                throw new IdInvalidException("Task với tên = " + newName + " đã tồn tại trong workspace");
            }

            if (updateTask.getTitle() != null)
                task.setTitle(updateTask.getTitle());
            if (updateTask.getStatus() != null)
                task.setStatus(updateTask.getStatus());
            if (updateTask.getDueDate() != null)
                task.setDueDate(updateTask.getDueDate());
            if (updateTask.getDescription() != null)
                task.setDescription(updateTask.getDescription());
            if (updateTask.getPriority() != null)
                task.setPriority(updateTask.getPriority());
        }

        Task updateTaskObj = taskRepository.save(task);
        workspaceService.updateWorkspaceStatusIfNeeded(workspaceId);

        return TaskMapper.mapToTaskDTO(updateTaskObj);
    }

    @Override
    public void deleteTask(Integer task_id) throws IdInvalidException {
        Task task = taskRepository.findById(task_id)
                .orElseThrow(() -> new IdInvalidException("Task với id = " + task_id + " không tồn tại"));
        taskRepository.deleteById(task_id);
        workspaceService.updateWorkspaceStatusIfNeeded(task.getWorkspace().getId());
    }

    @Override
    public List<TaskDTO> getTasksByWorkspaceId(int workspace_id) throws IdInvalidException {
        List<Task> tasks = taskRepository.findAllByWorkspaceId(workspace_id);
        if (tasks == null || tasks.isEmpty()) {
            throw new IdInvalidException("Workspace id = " + workspace_id + " hiện không có task");
        }

        boolean isPremium = tasks.get(0).getWorkspace().getUser().getRole() == Role.PREMIUM;

        return tasks.stream().map(task -> {
            List<User> assignees = isPremium
                    ? taskAssigneeRepository.findUsersByTaskId(task.getId())
                    : Collections.emptyList();

            TaskDTO taskDTO = TaskMapper.mapToTaskDTO(task, isPremium, assignees);

            // ✅ Gán progress thủ công cho workspace
            int total = taskRepository.countByWorkspaceId(task.getWorkspace().getId());
            int completed = taskRepository.countByWorkspaceIdAndStatus(task.getWorkspace().getId(),
                    TaskStatus.COMPLETED);
            int progress = total == 0 ? 0 : (int) ((double) completed / total * 100);
            taskDTO.getWorkspace().setProgress(progress);

            return taskDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public WorkspaceDTO getWorkspaceIdByTaskId(int task_id) throws IdInvalidException {
        Task task = taskRepository.findById(task_id)
                .orElseThrow(() -> new IdInvalidException("Task với id = " + task_id + " không tồn tại"));

        Workspace workspace = task.getWorkspace();
        WorkspaceDTO dto = WorkspaceMapper.mapToWorkspaceDTO(workspace);

        // 👉 Tính progress
        int total = taskRepository.countByWorkspaceId(workspace.getId());
        int completed = taskRepository.countByWorkspaceIdAndStatus(workspace.getId(), TaskStatus.COMPLETED);
        int progress = total == 0 ? 0 : (int) ((double) completed / total * 100);
        dto.setProgress(progress);
        return dto;
    }

    @Override
    public List<TaskDTO> getTaskAll() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream().map(
                (task) -> TaskMapper.mapToTaskDTO(task)).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> getTasksByWorkspaceIdAndStatus(int workspace_id, TaskStatus status) throws IdInvalidException {
        List<Task> tasks = taskRepository.findAllByWorkspaceIdAndStatus(workspace_id, status);
        if (tasks.isEmpty()) {
            throw new IdInvalidException(
                    "Không có task nào với trạng thái: " + status + " trong Workspace id = " + workspace_id);
        }
        return tasks.stream()
                .map(TaskMapper::mapToTaskDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskBriefDTO> getTop5TasksTodoByUserId(int userId) throws IdInvalidException {
        List<Workspace> workspaces = workspaceRepository.findWorkspacesByUserId(userId);
        if (workspaces.isEmpty()) {
            throw new IdInvalidException("User ID " + userId + " không có workspace nào.");
        }

        return workspaces.stream()
                .flatMap(ws -> taskRepository.findAllByWorkspaceId(ws.getId()).stream()
                        .filter(task -> (task.getStatus() == TaskStatus.PENDING
                                || task.getStatus() == TaskStatus.IN_PROGRESS)
                                && task.getDueDate() != null)
                        .map(task -> new TaskBriefDTO(task.getTitle(), ws.getName(), task.getDueDate())))
                .sorted(Comparator.comparing(TaskBriefDTO::getDueDate)) // sắp xếp tăng dần
                .limit(5)
                .collect(Collectors.toList());
    }

    @Override
    public int countCompletedTasksByUserId(int userId) throws IdInvalidException {
        if (!userRepository.existsById(userId)) {
            throw new IdInvalidException("User với id = " + userId + " không tồn tại");
        }
        return taskRepository.countCompletedTasksByUserId(userId);
    }

    @Override
    public List<TaskDTO> getAllTasksByUserId(int userId) throws IdInvalidException {
        List<Task> tasks = taskRepository.findAllByUserId(userId);
        if (tasks.isEmpty()) {
            throw new IdInvalidException("User id = " + userId + " không có task nào.");
        }

        return tasks.stream().map(task -> {
            boolean isPremium = task.getWorkspace().getUser().getRole() == Role.PREMIUM;
            List<User> assignees = isPremium
                    ? taskAssigneeRepository.findUsersByTaskId(task.getId())
                    : Collections.emptyList();
            return TaskMapper.mapToTaskDTO(task, isPremium, assignees);
        }).collect(Collectors.toList());
    }
}