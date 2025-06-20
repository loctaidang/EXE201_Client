package com.todo.chrono.controller;

import com.todo.chrono.dto.request.AddMemberRequest;
import com.todo.chrono.dto.request.RoleRequest;
import com.todo.chrono.dto.request.WorkspaceMemberDTO;
import com.todo.chrono.service.workspaceMemberService.WorkspaceMemberService;
import com.todo.chrono.util.error.IdInvalidException;
import com.todo.chrono.enums.RoleWorkspaceMember;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/workspace")
@AllArgsConstructor
@SecurityRequirement(name = "api")
public class WorkspaceMemberController {

    private final WorkspaceMemberService workspaceMemberService;

    @PostMapping("/{workspaceId}/members")
    @PreAuthorize("hasAnyRole('ADMIN', 'FREE', 'PREMIUM')")
    public ResponseEntity<WorkspaceMemberDTO> addMemberToWorkspace(
            @PathVariable Integer workspaceId,
            @RequestBody AddMemberRequest request ) throws IdInvalidException {
        WorkspaceMemberDTO addedMember = workspaceMemberService.addMemberToWorkspace(
                workspaceId, request.getUserId());
        return ResponseEntity.ok(addedMember);
    }

    @GetMapping("/{workspaceId}/members")
    @PreAuthorize("hasAnyRole('ADMIN', 'FREE', 'PREMIUM') ")
    public ResponseEntity<List<WorkspaceMemberDTO>> getMembersByWorkspaceId(
            @PathVariable Integer workspaceId) {
        List<WorkspaceMemberDTO> members = workspaceMemberService.getMembersByWorkspaceId(workspaceId);
        return ResponseEntity.ok(members);
    }

    @PutMapping("/{workspaceId}/members/{userId}/role")
    @PreAuthorize("hasAnyRole('ADMIN', 'FREE', 'PREMIUM')")
    public ResponseEntity<WorkspaceMemberDTO> updateMemberRole(
            @PathVariable Integer workspaceId,
            @PathVariable Integer userId,
            @RequestParam RoleWorkspaceMember newRole) throws IdInvalidException {

        WorkspaceMemberDTO updatedMember = workspaceMemberService
                .updateMemberRole(workspaceId, userId, newRole);

        return ResponseEntity.ok(updatedMember);
    }

    @DeleteMapping("/{workspaceId}/members/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FREE', 'PREMIUM')")
    public ResponseEntity<?> removeMemberFromWorkspace(
            @PathVariable Integer workspaceId,
            @PathVariable Integer userId) throws IdInvalidException {

        workspaceMemberService.removeMemberFromWorkspace(workspaceId, userId);
        return ResponseEntity.ok("Xóa thành viên khỏi workspace thành công.");
    }

    @DeleteMapping("/{workspaceId}/leave")
    @PreAuthorize("hasAnyRole('ADMIN', 'FREE', 'PREMIUM')")
    public ResponseEntity<?> leaveWorkspace(
            @PathVariable Integer workspaceId,
            @RequestParam Integer userId) throws IdInvalidException {
        workspaceMemberService.leaveWorkspace(workspaceId, userId);
        return ResponseEntity.ok("Rời khỏi workspace thành công.");
    }
    
}
