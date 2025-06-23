package com.todo.chrono.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.todo.chrono.enums.WorkspaceStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceCreateDTO {
    private String name;
    private String description;
    private WorkspaceStatus status;


}