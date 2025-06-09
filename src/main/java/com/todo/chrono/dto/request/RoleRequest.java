package com.todo.chrono.dto.request;

import com.todo.chrono.enums.RoleWorkspaceMember;

import lombok.Data;

@Data
public class RoleRequest {
    private RoleWorkspaceMember role;
}
