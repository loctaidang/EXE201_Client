package com.todo.chrono.mapper;

import com.todo.chrono.dto.request.TaskDTO;
import com.todo.chrono.entity.Task;
import com.todo.chrono.entity.Workspace;
import com.todo.chrono.dto.request.TaskCreateDTO;

public class TaskMapper {

    public static TaskDTO mapToTaskDTO(Task task){
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(task.getId());
        taskDTO.setWorkspace(WorkspaceMapper.mapToWorkspaceDTO(task.getWorkspace()));
        taskDTO.setTitle(task.getTitle());
        taskDTO.setStatus(task.getStatus());
        taskDTO.setPriority(task.getPriority());
        taskDTO.setDescription(task.getDescription());
        taskDTO.setDueDate(task.getDueDate());
        taskDTO.setCreatedAt(task.getCreatedAt());
        return taskDTO;

    }
    public static Task mapToTask(TaskDTO taskDTO){
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
    public static Task mapToTask(TaskCreateDTO taskCreateDTO){
        Task task = new Task();
        task.setTitle(taskCreateDTO.getTitle());
        task.setStatus(taskCreateDTO.getStatus());
        task.setDescription(taskCreateDTO.getDescription());
        task.setPriority(taskCreateDTO.getPriority());
        task.setDueDate(taskCreateDTO.getDueDate());
        return task;
    }
    
}
