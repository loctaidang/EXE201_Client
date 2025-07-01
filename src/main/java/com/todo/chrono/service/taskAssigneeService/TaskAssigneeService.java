package com.todo.chrono.service.taskAssigneeService;

import java.util.List;

import com.todo.chrono.dto.request.TaskAssigneeDTO;
import com.todo.chrono.util.error.IdInvalidException;


public interface TaskAssigneeService {
    TaskAssigneeDTO assignUserToTask(Integer taskId, Integer userId) throws IdInvalidException;
    void removeUserFromTask(Integer taskId, Integer userId) throws IdInvalidException;
    List<TaskAssigneeDTO> getAssigneesByTaskId(Integer taskId);
    List<TaskAssigneeDTO> getTasksByAssigneeUserId(Integer userId);
}
