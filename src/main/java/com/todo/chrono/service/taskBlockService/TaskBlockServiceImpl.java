package com.todo.chrono.service.taskBlockService;

import com.todo.chrono.dto.request.TaskBlockDTO;
import com.todo.chrono.dto.request.TaskBlockCreateDTO;
import com.todo.chrono.entity.Task;
import com.todo.chrono.entity.TaskBlock;
import com.todo.chrono.mapper.TaskBlockMapper;
import com.todo.chrono.mapper.TaskMapper;
import com.todo.chrono.repository.TaskBlockRepository;
import com.todo.chrono.repository.TaskRepository;
import com.todo.chrono.util.error.IdInvalidException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.todo.chrono.enums.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TaskBlockServiceImpl implements TaskBlockService {

    private final TaskBlockRepository taskBlockRepository;
    private final TaskRepository taskRepository;

    @Override
    public TaskBlockDTO createTaskBlock(TaskBlockCreateDTO dto, int taskId) throws IdInvalidException {
        if (dto.getStartTime() != null && dto.getEndTime() != null) {
            if (dto.getStartTime().isAfter(dto.getEndTime())) {
                throw new IllegalArgumentException("startTime phải trước endTime");
            }
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IdInvalidException("Task ID " + taskId + " không tồn tại"));

        if (task.getStatus() == TaskStatus.COMPLETED) {
            throw new IllegalStateException("Không thể tạo block cho task đã hoàn thành");
        }

        TaskBlock block = TaskBlockMapper.toEntity(dto, task);
        TaskBlock saved = taskBlockRepository.save(block);
        return TaskBlockMapper.toDTO(saved);
    }

    @Override
    public List<TaskBlockDTO> getTaskBlocksByTaskId(int taskId) {
        return taskBlockRepository.findByTaskId(taskId)
                .stream()
                .map(TaskBlockMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskBlockDTO> getTaskBlocksByDateTimeRange(int userId, LocalDateTime start, LocalDateTime end) {
        return taskBlockRepository.findByStartTimeBetween(start, end)
                .stream()
                .filter(tb -> tb.getTask().getWorkspace().getUser().getId() == userId)
                .map(TaskBlockMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskBlockDTO> getBlocksByWorkspaceAndDateTimeRange(int workspaceId, LocalDateTime start,
            LocalDateTime end) {
        return taskBlockRepository.findByTask_Workspace_IdAndStartTimeBetween(workspaceId, start, end)
                .stream()
                .map(TaskBlockMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
public TaskBlockDTO updateTaskBlock(int blockId, TaskBlockCreateDTO dto) throws IdInvalidException {
    TaskBlock existing = taskBlockRepository.findById(blockId)
        .orElseThrow(() -> new IdInvalidException("Block ID " + blockId + " không tồn tại"));

    
    if (existing.getTask().getStatus() == TaskStatus.COMPLETED) {
        throw new IllegalStateException("Không thể cập nhật block của task đã hoàn thành");
    }

    if (dto.getStartTime() != null) {
        existing.setStartTime(dto.getStartTime());
    }

    if (dto.getEndTime() != null) {
        existing.setEndTime(dto.getEndTime());
    }

    // Check thời gian
    if (existing.getStartTime() != null && existing.getEndTime() != null &&
        existing.getStartTime().isAfter(existing.getEndTime())) {
        throw new IllegalArgumentException("startTime phải trước endTime");
    }

    TaskBlock updated = taskBlockRepository.save(existing);
    return TaskBlockMapper.toDTO(updated);
}


    @Override
    public void deleteTaskBlock(int blockId) throws IdInvalidException {
        TaskBlock existing = taskBlockRepository.findById(blockId)
                .orElseThrow(() -> new IdInvalidException("Block ID " + blockId + " không tồn tại"));
        taskBlockRepository.delete(existing);
    }

}
