package com.todo.chrono.service.taskService;

import com.todo.chrono.util.error.IdInvalidException;
import com.todo.chrono.dto.request.UserDTO;
import com.todo.chrono.dto.request.TaskDTO;
import com.todo.chrono.dto.request.WorkspaceDTO;
import com.todo.chrono.repository.WorkspaceRepository;
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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private WorkspaceRepository workspaceRepository;
    private TaskRepository taskRepository;

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
        task.setTitle(updateTask.getTitle());
        if (taskRepository.existsByWorkspaceIdAndTitle(task.getWorkspace().getId(), updateTask.getTitle())) {
            throw new IdInvalidException("Task với tên = " + updateTask.getTitle() + " đã tồn tại trong Workspace id = "
                    + task.getWorkspace().getId());
        }
        task.setStatus(updateTask.getStatus());
        task.setDueDate(updateTask.getDueDate());
        Task updateTaskObj = taskRepository.save(task);
        return TaskMapper.mapToTaskDTO(updateTaskObj);
    }

    @Override
    public void deleteTask(Integer task_id) throws IdInvalidException {
        Task task = taskRepository.findById(task_id)
                .orElseThrow(() -> new IdInvalidException("Task với id = " + task_id + " không tồn tại"));
        taskRepository.deleteById(task_id);
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
}