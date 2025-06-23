package com.todo.chrono.controller;

import com.todo.chrono.util.error.IdInvalidException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.todo.chrono.dto.request.WorkspaceDTO;
import com.todo.chrono.service.workspaceService.WorkspaceService;
import com.todo.chrono.dto.request.UserDTO;
import com.todo.chrono.dto.request.WorkspaceCreateDTO;

import java.util.List;

@CrossOrigin("*")
@RestController
@AllArgsConstructor
@RequestMapping("/workspace")
@SecurityRequirement(name = "api")
public class WorkspaceController {

    private WorkspaceService workspaceService;

    @PostMapping("/user/{user_id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FREE', 'PREMIUM') ")
    public ResponseEntity<WorkspaceDTO> createWorkspace(@PathVariable("user_id") Integer user_id,
            @RequestBody WorkspaceCreateDTO workspaceCreateDTO) throws IdInvalidException {
        WorkspaceDTO savedWorkspace = workspaceService.createWorkspace(workspaceCreateDTO, user_id);
        return new ResponseEntity<>(savedWorkspace, HttpStatus.CREATED);
    }

    @GetMapping("/user/{user_id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FREE', 'PREMIUM') ")
    public ResponseEntity<List<WorkspaceDTO>> getWorkspaceByUserId(@PathVariable("user_id") int user_id)
            throws IdInvalidException {
        List<WorkspaceDTO> workspaceDTOs = workspaceService.getWorkspacesByUserId(user_id);
        return ResponseEntity.ok(workspaceDTOs);
    }

    @GetMapping("/{workspace_id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FREE', 'PREMIUM') ")
    public ResponseEntity<WorkspaceDTO> getWorkspaceById(@PathVariable("workspace_id") Integer workspace_id)
            throws IdInvalidException {
        WorkspaceDTO workspaceDTO = workspaceService.getWorkspaceById(workspace_id);
        return ResponseEntity.ok(workspaceDTO);
    }

    @PutMapping("/{workspace_id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FREE', 'PREMIUM') ")
    public ResponseEntity<WorkspaceDTO> updateWorkspace(@RequestBody WorkspaceCreateDTO updatedWorkspace,
            @PathVariable("workspace_id") Integer workspaceId) throws IdInvalidException {
        WorkspaceDTO workspaceDTO = workspaceService.updateWorkspace(updatedWorkspace, workspaceId);
        return ResponseEntity.ok(workspaceDTO);
    }

    @DeleteMapping("/{workspace_id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FREE', 'PREMIUM') ")
    public ResponseEntity<Void> deleteWorkspace(@PathVariable("workspace_id") Integer workspace_id)
            throws IdInvalidException {
        WorkspaceDTO currentWorkspace = this.workspaceService.getWorkspaceById(workspace_id);
        this.workspaceService.deleteWorkspace(currentWorkspace.getId());
        return ResponseEntity.ok(null);
    }

    @GetMapping("/{workspace_id}/user")
    @PreAuthorize("hasAnyRole('ADMIN', 'FREE', 'PREMIUM') ")
    public ResponseEntity<UserDTO> getUserIdByWorkspaceId(@PathVariable("workspace_id") int workspace_id) {
        UserDTO userDTO = workspaceService.getUserIdByWorkspaceId(workspace_id);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FREE', 'PREMIUM') ")
    public ResponseEntity<List<WorkspaceDTO>> getWorkspaceAll() {
        List<WorkspaceDTO> workspace = workspaceService.getWorkspaceAll();
        return ResponseEntity.ok(workspace);
    }

    @GetMapping("/progress/workspace/{workspaceId}")
    public ResponseEntity<Integer> getWorkspaceProgress(@PathVariable int workspaceId) {
        try {
            int progress = workspaceService.getWorkspaceProgress(workspaceId);
            return ResponseEntity.ok(progress);
        } catch (IdInvalidException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/progress/user/{userId}")
    public ResponseEntity<List<WorkspaceDTO>> getInProgressWorkspaces(@PathVariable int userId) {
        try {
            List<WorkspaceDTO> list = workspaceService.getWorkspacesInProgressByUser(userId);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}/completed-count")
    @PreAuthorize("hasAnyRole('ADMIN', 'FREE', 'PREMIUM')")
    public ResponseEntity<Integer> countCompletedWorkspacesByUser(@PathVariable int userId) throws IdInvalidException {
        int count = workspaceService.countCompletedWorkspacesByUser(userId);
        return ResponseEntity.ok(count);
    }
    @GetMapping("/users/{userId}/workspaces/uncompleted-count")
    @PreAuthorize("hasAnyRole('FREE', 'PREMIUM','ADMIN')")
    public ResponseEntity<Integer> countUncompletedWorkspaces(@PathVariable int userId) {
        int count = workspaceService.countUncompletedWorkspacesByUser(userId);
        return ResponseEntity.ok(count);
    }
}