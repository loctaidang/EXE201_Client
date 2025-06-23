package com.todo.chrono.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

import com.todo.chrono.entity.Task;
import com.todo.chrono.enums.TaskStatus;
import com.todo.chrono.enums.TaskPriority;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
    private int id;

    private WorkspaceDTO workspace;

    private String title;

    private TaskStatus status;

    private TaskPriority priority;

    private String description;

    private LocalDateTime dueDate;

    private LocalDateTime createdAt;

}