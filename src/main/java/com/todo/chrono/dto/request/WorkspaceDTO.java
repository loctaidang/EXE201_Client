package com.todo.chrono.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceDTO {
    private int id;

    private int userId;

    private String name;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}