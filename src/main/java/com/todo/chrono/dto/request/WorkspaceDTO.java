package com.todo.chrono.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import com.todo.chrono.enums.WorkspaceStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceDTO {
    private int id;

    private UserDTO user;

    private String name;

    private String description;

    private WorkspaceStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private int progress;

}