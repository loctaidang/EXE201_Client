package com.todo.chrono.service.workspaceService;


import com.todo.chrono.util.error.IdInvalidException;
import com.todo.chrono.dto.request.WorkspaceDTO;
import com.todo.chrono.dto.request.UserDTO;

import java.util.List;

public interface WorkspaceService {
    WorkspaceDTO createWorkspace(WorkspaceDTO workspaceDTO, Integer user_id) throws IdInvalidException;

    WorkspaceDTO getWorkspaceById ( Integer workspace_id) throws IdInvalidException;

    WorkspaceDTO updateWorkspace (WorkspaceDTO workspaceDTO, Integer workspace_id) throws IdInvalidException;

    void deleteWorkspace (Integer workspace_id) throws IdInvalidException;

    List<WorkspaceDTO> getWorkspacesByUserId (int user_id) throws IdInvalidException;

    List<WorkspaceDTO> getWorkspaceAll();
    UserDTO getUserIdByWorkspaceId(int workspace_id);
}