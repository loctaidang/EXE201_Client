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

    @Override
    public TaskDTO createTask(TaskCreateDTO taskCreateDTO, Integer workspace_id) throws IdInvalidException {
        Workspace workspace = workspaceRepository.findById(workspace_id)
                .orElseThrow(() -> new IdInvalidException("Workspace: " + workspace_id + " not found"));

        // Nếu role là FREE thì giới hạn số task trong mỗi workspace là 5
        if (workspace.getUser().getRole().equals(Role.FREE)) {
            int currentTaskCount = taskRepository.countByWorkspaceId(workspace_id);
            if (currentTaskCount >= 5) {
                throw new IdInvalidException(
                        "Người dùng FREE chỉ được tạo tối đa 5 task trong 1 workspace (Workspace ID = " + workspace_id
                                + ").");
            }
        }

        // Kiểm tra tên task đã tồn tại trong workspace
        if (taskRepository.existsByWorkspaceIdAndTitle(workspace_id, taskCreateDTO.getTitle())) {
            throw new IdInvalidException(
                    "Task với tên = " + taskCreateDTO.getTitle() + " đã tồn tại trong Workspace id = " + workspace_id);
        }

        Task task = TaskMapper.mapToTask(taskCreateDTO);
        task.setWorkspace(workspace);
        task.setCreatedAt(LocalDateTime.now());

        Task savedTask = taskRepository.save(task);
        workspaceService.updateWorkspaceStatusIfNeeded(workspace_id);
        return TaskMapper.mapToTaskDTO(savedTask);
    }

    @Override
    public TaskDTO getTaskById(Integer task_id) throws IdInvalidException {
        Optional<Task> task = taskRepository.findById(task_id);
        if (task.isPresent()) {
            return TaskMapper.mapToTaskDTO(task.get());
        } else {
            throw new IdInvalidException("Task với id = " + task_id + " không tồn tại");
        }

    }

    @Override
    public TaskDTO updateTask(TaskCreateDTO updateTask, Integer task_id) throws IdInvalidException {
        Task task = taskRepository.findById(task_id)
                .orElseThrow(() -> new RuntimeException("Task " + task_id + " not found"));
        String newName = updateTask.getTitle();
        String currentName = task.getTitle();

        if (newName != null && !newName.equals(currentName)
                && taskRepository.existsByWorkspaceIdAndTitle(task.getWorkspace().getId(), newName)) {
            throw new IdInvalidException("Task với tên = " + newName + " đã tồn tại trong Workspace id = "
                    + task.getWorkspace().getId());
        }
        if (updateTask.getStatus() != null) {
            task.setStatus(updateTask.getStatus());
        }
        if (updateTask.getDueDate() != null) {
            task.setDueDate(updateTask.getDueDate());
        }
        if (updateTask.getDescription() != null) {
            task.setDescription(updateTask.getDescription());
        }
        if (updateTask.getPriority() != null) {
            task.setPriority(updateTask.getPriority());
        }
        Task updateTaskObj = taskRepository.save(task);
        workspaceService.updateWorkspaceStatusIfNeeded(task.getWorkspace().getId());
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
        if (tasks == null) {
            throw new IdInvalidException("Trong Workspace id = " + workspace_id + " hiện không có task");
        }
        return tasks.stream().map(
                (task) -> TaskMapper.mapToTaskDTO(task)).collect(Collectors.toList());
    }

    @Override
    public WorkspaceDTO getWorkspaceIdByTaskId(int task_id) throws IdInvalidException {
        Optional<Task> task = taskRepository.findById(task_id);
        if (task.isPresent()) {
            return WorkspaceMapper.mapToWorkspaceDTO(task.get().getWorkspace());
        } else {
            throw new IdInvalidException("Task với id = " + task_id + " không tồn tại");
        }
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

}