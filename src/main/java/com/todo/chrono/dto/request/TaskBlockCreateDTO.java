package com.todo.chrono.dto.request;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskBlockCreateDTO {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}