package com.todo.chrono.service.workspaceService;


import com.todo.chrono.util.error.IdInvalidException;
import com.todo.chrono.dto.request.WorkspaceDTO;
import com.todo.chrono.dto.request.UserDTO;
import com.todo.chrono.dto.request.WorkspaceCreateDTO;
import com.todo.chrono.dto.request.TaskBriefDTO;

import java.util.List;

public interface WorkspaceService {
    WorkspaceDTO createWorkspace(WorkspaceCreateDTO workspaceCreateDTO, Integer user_id) throws IdInvalidException;

    WorkspaceDTO getWorkspaceById ( Integer workspace_id) throws IdInvalidException;

    WorkspaceDTO updateWorkspace (WorkspaceCreateDTO workspaceCreateDTO, Integer workspace_id) throws IdInvalidException;

    void deleteWorkspace (Integer workspace_id) throws IdInvalidException;

    List<WorkspaceDTO> getWorkspacesByUserId (int user_id) throws IdInvalidException;

    List<WorkspaceDTO> getWorkspaceAll();
    UserDTO getUserIdByWorkspaceId(int workspace_id);
    public int getWorkspaceProgress(int workspaceId) throws IdInvalidException;
    List<WorkspaceDTO> getWorkspacesInProgressByUser(int userId);
    int countCompletedWorkspacesByUser(int userId) throws IdInvalidException;
    void updateWorkspaceStatusIfNeeded(Integer workspaceId);
    int countUncompletedWorkspacesByUser(int userId);

    

}