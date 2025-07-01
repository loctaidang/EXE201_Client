package com.todo.chrono.mapper;

import com.todo.chrono.dto.request.TaskBlockDTO;
import com.todo.chrono.entity.TaskBlock;
import com.todo.chrono.entity.Task;
import com.todo.chrono.mapper.TaskMapper;

import java.time.LocalDateTime;

import com.todo.chrono.dto.request.TaskBlockCreateDTO;

 public class TaskBlockMapper {

    public static TaskBlockDTO toDTO(TaskBlock block) {
        return TaskBlockDTO.builder()
                .id(block.getId())
                .task(TaskMapper.mapToTaskDTO(block.getTask())) // dùng DTO, không entity
                .startTime(block.getStartTime())
                .endTime(block.getEndTime())
                .createdAt(block.getCreatedAt())
                .build();
    }

    public static TaskBlock toEntity(TaskBlockDTO dto, Task task)    {
        return TaskBlock.builder()
                .id(dto.getId())
                .task(task)
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .createdAt(dto.getCreatedAt())
                .build();
    }
    public static TaskBlock toEntity(TaskBlockCreateDTO dto, Task task) {
        return TaskBlock.builder()
                .task(task)
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .createdAt(LocalDateTime.now())  // luôn set thời điểm tạo mới
                .build();
    }

    
}
