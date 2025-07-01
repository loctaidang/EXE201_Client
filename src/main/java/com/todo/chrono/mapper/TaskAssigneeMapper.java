package com.todo.chrono.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

import com.todo.chrono.dto.request.TaskAssigneeDTO;
import com.todo.chrono.entity.Task;
import com.todo.chrono.entity.TaskAssignee;
import com.todo.chrono.entity.User;

public class TaskAssigneeMapper {
    public static TaskAssigneeDTO mapToDTO(TaskAssignee entity) {
        TaskAssigneeDTO dto = new TaskAssigneeDTO();
        dto.setId(entity.getId());
        dto.setUser(UserMapper.mapToUserDTO(entity.getUser()));
        dto.setAssignedAt(entity.getAssignedAt());

        Task task = entity.getTask();
        boolean isPremium = task.getWorkspace().getUser().getRole().name().equals("PREMIUM");

        // Lấy danh sách người được gán nếu là PREMIUM
        List<User> assignees = isPremium && task.getAssignees() != null
                ? task.getAssignees().stream()
                    .map(TaskAssignee::getUser)
                    .collect(Collectors.toList())
                : Collections.emptyList();

        dto.setTask(TaskMapper.mapToTaskDTO(task, isPremium, assignees));
        return dto;
    }

    public static TaskAssignee mapToEntity(Task task, User user) {
        TaskAssignee entity = new TaskAssignee();
        entity.setTask(task);
        entity.setUser(user);
        entity.setAssignedAt(LocalDateTime.now());
        return entity;
    }
}
