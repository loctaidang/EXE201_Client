package com.todo.chrono.mapper;

import com.todo.chrono.dto.request.WorkspaceMemberDTO;
import com.todo.chrono.entity.WorkspaceMember;

public class WorkspaceMemberMapper {

    public static WorkspaceMemberDTO mapToWorkspaceMemberDTO(WorkspaceMember workspaceMember){
        WorkspaceMemberDTO workspaceMemberDTO = new WorkspaceMemberDTO();
        workspaceMemberDTO.setId(workspaceMember.getId());
        workspaceMemberDTO.setWorkspaceId(workspaceMember.getWorkspace().getId());
        workspaceMemberDTO.setUserId(workspaceMember.getUser().getId());
        workspaceMemberDTO.setRole(workspaceMember.getRole());
        workspaceMemberDTO.setJoinedAt(workspaceMember.getJoinedAt());
        return workspaceMemberDTO;

    }
    public static WorkspaceMember mapToWorkspaceMember(WorkspaceMemberDTO workspaceMemberDTO){
        WorkspaceMember workspaceMember = new WorkspaceMember();
        workspaceMember.setId(workspaceMemberDTO.getId());
        // workspaceMember.setWorkspace(workspace);
        // workspaceMember.setUser(user);
        workspaceMember.setRole(workspaceMemberDTO.getRole());
        workspaceMember.setJoinedAt(workspaceMemberDTO.getJoinedAt());
        return workspaceMember;

    }
}
