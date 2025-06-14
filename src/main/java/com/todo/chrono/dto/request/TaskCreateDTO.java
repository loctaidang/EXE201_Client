package com.todo.chrono.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import com.todo.chrono.enums.TaskStatus;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskCreateDTO {

    private String title;
    private TaskStatus status;
    private LocalDateTime dueDate;
    
}