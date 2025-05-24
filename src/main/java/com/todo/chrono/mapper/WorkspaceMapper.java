package com.todo.chrono.mapper;

import com.todo.chrono.dto.request.WorkspaceDTO;
import com.todo.chrono.entity.Workspace;
import com.todo.chrono.dto.request.WorkspaceCreateDTO;

public class WorkspaceMapper {

    public static WorkspaceDTO mapToWorkspaceDTO(Workspace workspace){
        WorkspaceDTO workspaceDTO = new WorkspaceDTO();
        workspaceDTO.setId(workspace.getId());
        workspaceDTO.setUserId(workspace.getUser().getId());
        workspaceDTO.setName(workspace.getName());
        workspaceDTO.setCreatedAt(workspace.getCreatedAt());
        workspaceDTO.setUpdatedAt(workspace.getUpdatedAt());
        return workspaceDTO;

    }
    public static Workspace mapToWorkspace(WorkspaceDTO workspaceDTO){
        Workspace workspace = new Workspace();
        workspace.setId(workspaceDTO.getId());
        workspace.setName(workspaceDTO.getName());
        workspace.setCreatedAt(workspaceDTO.getCreatedAt());
        workspace.setUpdatedAt(workspaceDTO.getUpdatedAt());
        return workspace;

    }
    public static Workspace mapToWorkspace(WorkspaceCreateDTO workspaceCreateDTO){
        Workspace workspace = new Workspace();
        workspace.setName(workspaceCreateDTO.getName());
        return workspace;
    }
}
