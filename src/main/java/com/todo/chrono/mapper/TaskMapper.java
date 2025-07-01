package com.todo.chrono.mapper;

import com.todo.chrono.dto.request.TaskDTO;
import com.todo.chrono.entity.Task;
import com.todo.chrono.entity.Workspace;
import com.todo.chrono.dto.request.TaskCreateDTO;
import com.todo.chrono.entity.User;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

public class TaskMapper {

    public static TaskDTO mapToTaskDTO(Task task) {
        TaskDTO taskDTO = new TaskDTO();
        Workspace workspace = task.getWorkspace();

        // Map workspace
        var workspaceDTO = WorkspaceMapper.mapToWorkspaceDTO(workspace);

        // 👉 Tính progress
        int total = workspace.getTasks() != null ? workspace.getTasks().size() : 0;
        int completed = workspace.getTasks() != null
                ? (int) workspace.getTasks().stream().filter(t -> t.getStatus().toString().equals("COMPLETED")).count()
                : 0;
        int progress = total == 0 ? 0 : (int) ((double) completed / total * 100);
        workspaceDTO.setProgress(progress);
        taskDTO.setId(task.getId());
        taskDTO.setWorkspace(workspaceDTO);
        taskDTO.setTitle(task.getTitle());
        taskDTO.setStatus(task.getStatus());
        taskDTO.setPriority(task.getPriority());
        taskDTO.setDescription(task.getDescription());
        taskDTO.setDueDate(task.getDueDate());
        taskDTO.setCreatedAt(task.getCreatedAt());
        return taskDTO;

    }

    public static TaskDTO mapToTaskDTO(Task task, boolean isPremium, List<User> assignees) {
        TaskDTO taskDTO = new TaskDTO();
        Workspace workspace = task.getWorkspace();
        var workspaceDTO = WorkspaceMapper.mapToWorkspaceDTO(workspace);

        // 👉 Tính progress từ danh sách task của workspace
        int total = workspace.getTasks() != null ? workspace.getTasks().size() : 0;
        int completed = workspace.getTasks() != null
                ? (int) workspace.getTasks().stream().filter(t -> t.getStatus().toString().equals("COMPLETED")).count()
                : 0;
        int progress = total == 0 ? 0 : (int) ((double) completed / total * 100);
        workspaceDTO.setProgress(progress);

        taskDTO.setId(task.getId());
        taskDTO.setWorkspace(workspaceDTO);
        taskDTO.setTitle(task.getTitle());
        taskDTO.setStatus(task.getStatus());
        taskDTO.setPriority(task.getPriority());
        taskDTO.setDescription(task.getDescription());
        taskDTO.setDueDate(task.getDueDate());
        taskDTO.setCreatedAt(task.getCreatedAt());

        if (isPremium && assignees != null) {
            taskDTO.setAssignees(
                    assignees.stream()
                            .map(UserMapper::mapToUserDTO)
                            .collect(Collectors.toList()));
        } else {
            taskDTO.setAssignees(Collections.emptyList());
        }

        return taskDTO;
    }

    public static Task mapToTask(TaskDTO taskDTO) {
        Task task = new Task();
        task.setId(taskDTO.getId());
        task.setTitle(taskDTO.getTitle());
        task.setStatus(taskDTO.getStatus());
        task.setPriority(taskDTO.getPriority());
        task.setDescription(taskDTO.getDescription());
        task.setDueDate(taskDTO.getDueDate());
        task.setCreatedAt(taskDTO.getCreatedAt());
        return task;
    }

    public static Task mapToTask(TaskCreateDTO taskCreateDTO) {
        Task task = new Task();
        task.setTitle(taskCreateDTO.getTitle());
        task.setStatus(taskCreateDTO.getStatus());
        task.setDescription(taskCreateDTO.getDescription());
        task.setPriority(taskCreateDTO.getPriority());
        task.setDueDate(taskCreateDTO.getDueDate());
        return task;
    }

}
