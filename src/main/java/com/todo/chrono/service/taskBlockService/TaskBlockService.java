package com.todo.chrono.service.taskBlockService;

import com.todo.chrono.dto.request.TaskBlockDTO;
import com.todo.chrono.util.error.IdInvalidException;
import com.todo.chrono.dto.request.TaskBlockCreateDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskBlockService {
    TaskBlockDTO createTaskBlock(TaskBlockCreateDTO dto, int taskId) throws IdInvalidException;

    List<TaskBlockDTO> getTaskBlocksByTaskId(int taskId);

    List<TaskBlockDTO> getTaskBlocksByDateTimeRange(int userId, LocalDateTime start, LocalDateTime end);

    List<TaskBlockDTO> getBlocksByWorkspaceAndDateTimeRange(int workspaceId, LocalDateTime start, LocalDateTime end);

    TaskBlockDTO updateTaskBlock(int blockId, TaskBlockCreateDTO dto) throws IdInvalidException;

    void deleteTaskBlock(int blockId) throws IdInvalidException;

}
