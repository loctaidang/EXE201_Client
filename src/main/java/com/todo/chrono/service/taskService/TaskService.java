package com.todo.chrono.service.taskService;


import com.todo.chrono.util.error.IdInvalidException;
import com.todo.chrono.dto.request.TaskDTO;
import com.todo.chrono.dto.request.WorkspaceDTO;
import com.todo.chrono.dto.request.TaskCreateDTO;
import com.todo.chrono.enums.TaskStatus;
import com.todo.chrono.dto.request.TaskBriefDTO;

import java.util.List;

public interface TaskService {
    TaskDTO createTask(TaskCreateDTO taskCreateDTO, Integer workspace_id) throws IdInvalidException;


    TaskDTO getTaskById ( Integer task_id) throws IdInvalidException;

    TaskDTO updateTask (TaskCreateDTO taskCreateDTO, Integer task_id) throws IdInvalidException;

    void deleteTask (Integer task_id) throws IdInvalidException;

    List<TaskDTO> getTasksByWorkspaceId (int workspace_id) throws IdInvalidException;

    List<TaskDTO> getTaskAll();
    WorkspaceDTO getWorkspaceIdByTaskId(int task_id) throws IdInvalidException;
    List<TaskDTO> getTasksByWorkspaceIdAndStatus(int workspace_id, TaskStatus status) throws IdInvalidException;
    List<TaskBriefDTO> getTop5TasksTodoByUserId(int userId) throws IdInvalidException;

    int countCompletedTasksByUserId(int userId) throws IdInvalidException;
    List<TaskDTO> getAllTasksByUserId(int userId) throws IdInvalidException;


}
