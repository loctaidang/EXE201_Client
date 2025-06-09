package com.todo.chrono.service.workspaceMemberService;

import com.todo.chrono.dto.request.WorkspaceMemberDTO;
import com.todo.chrono.util.error.IdInvalidException;
import com.todo.chrono.enums.RoleWorkspaceMember;

import java.util.List;

public interface WorkspaceMemberService {
        WorkspaceMemberDTO addMemberToWorkspace(Integer workspaceId, Integer userId)
                        throws IdInvalidException;

        List<WorkspaceMemberDTO> getMembersByWorkspaceId(Integer workspaceId);

        WorkspaceMemberDTO updateMemberRole(
                        Integer workspaceId,
                        Integer targetUserId,
                        Integer currentUserId,
                        RoleWorkspaceMember newRole) throws IdInvalidException;

        public void removeMemberFromWorkspace(Integer workspaceId, Integer targetUserId, Integer currentUserId)
                        throws IdInvalidException;

        public void leaveWorkspace(Integer workspaceId, Integer userId) throws IdInvalidException;

}
