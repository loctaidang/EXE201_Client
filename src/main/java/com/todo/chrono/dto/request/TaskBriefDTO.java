package com.todo.chrono.dto.request;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskBriefDTO {
    private String taskTitle;
    private String workspaceName;
    private LocalDateTime dueDate;
}