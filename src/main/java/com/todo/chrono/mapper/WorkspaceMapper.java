package com.todo.chrono.mapper;

import com.todo.chrono.dto.request.WorkspaceDTO;
import com.todo.chrono.entity.Workspace;
import com.todo.chrono.dto.request.WorkspaceCreateDTO;

public class WorkspaceMapper {

    public static WorkspaceDTO mapToWorkspaceDTO(Workspace workspace){
        WorkspaceDTO workspaceDTO = new WorkspaceDTO();
        workspaceDTO.setId(workspace.getId());
        workspaceDTO.setUser(UserMapper.mapToUserDTO(workspace.getUser()));
        workspaceDTO.setName(workspace.getName());
        workspaceDTO.setDescription(workspace.getDescription());
        workspaceDTO.setStatus(workspace.getStatus());
        workspaceDTO.setCreatedAt(workspace.getCreatedAt());
        workspaceDTO.setUpdatedAt(workspace.getUpdatedAt());
        return workspaceDTO;

    }
    public static Workspace mapToWorkspace(WorkspaceDTO workspaceDTO){
        Workspace workspace = new Workspace();
        workspace.setId(workspaceDTO.getId());
        workspace.setName(workspaceDTO.getName());
        workspace.setDescription(workspaceDTO.getDescription());
        workspace.setStatus(workspaceDTO.getStatus());
        workspace.setCreatedAt(workspaceDTO.getCreatedAt());
        workspace.setUpdatedAt(workspaceDTO.getUpdatedAt());
        return workspace;

    }
    public static Workspace mapToWorkspace(WorkspaceCreateDTO workspaceCreateDTO){
        Workspace workspace = new Workspace();
        workspace.setName(workspaceCreateDTO.getName());
        workspace.setDescription(workspaceCreateDTO.getDescription());
        workspace.setStatus(workspaceCreateDTO.getStatus());
        return workspace;
    }
}
