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
    @PreAuthorize("hasRole('FREE')")
    public ResponseEntity<WorkspaceDTO> createWorkspace(@PathVariable("user_id") Integer user_id,
                                                @RequestBody WorkspaceCreateDTO workspaceCreateDTO) throws IdInvalidException{
        WorkspaceDTO savedWorkspace = workspaceService.createWorkspace(workspaceCreateDTO, user_id);
        return new ResponseEntity<>(savedWorkspace, HttpStatus.CREATED);
    }

    @GetMapping("/user/{user_id}")
    @PreAuthorize("hasRole('FREE')")
    public ResponseEntity<List<WorkspaceDTO>> getWorkspaceByUserId (@PathVariable("user_id") int user_id) throws IdInvalidException {
        List<WorkspaceDTO> workspaceDTOs = workspaceService.getWorkspacesByUserId(user_id);
        return ResponseEntity.ok(workspaceDTOs);
    }

    @GetMapping("/{workspace_id}")
    @PreAuthorize("hasAnyRole('FREE', 'STUDENT','MANAGER')")
    public ResponseEntity<WorkspaceDTO> getWorkspaceById (@PathVariable("workspace_id") Integer workspace_id) throws IdInvalidException {
        WorkspaceDTO workspaceDTO = workspaceService.getWorkspaceById(workspace_id);
        return ResponseEntity.ok(workspaceDTO);
    }

    @PutMapping("/{workspace_id}")
    @PreAuthorize("hasRole('FREE')")
    public ResponseEntity<WorkspaceDTO> updateWorkspace(@RequestBody WorkspaceCreateDTO updatedWorkspace, @PathVariable("workspace_id") Integer workspaceId) throws IdInvalidException{
        WorkspaceDTO workspaceDTO = workspaceService.updateWorkspace(updatedWorkspace, workspaceId );
        return ResponseEntity.ok(workspaceDTO);
    }

    @DeleteMapping("/{workspace_id}")
    @PreAuthorize("hasRole('FREE')")
    public ResponseEntity<Void> deleteWorkspace(@PathVariable("workspace_id") Integer workspace_id) throws IdInvalidException {
        WorkspaceDTO currentWorkspace = this.workspaceService.getWorkspaceById(workspace_id);
        this.workspaceService.deleteWorkspace(currentWorkspace.getId());
        return ResponseEntity.ok(null);
    }
    @GetMapping("/{workspace_id}/user")
    @PreAuthorize("hasAnyRole('FREE', 'STUDENT','MANAGER')")
    public ResponseEntity<UserDTO> getUserIdByWorkspaceId(@PathVariable("workspace_id") int workspace_id) {
        UserDTO userDTO = workspaceService.getUserIdByWorkspaceId(workspace_id);
        return ResponseEntity.ok(userDTO);
    }

   @GetMapping
   public ResponseEntity<List<WorkspaceDTO>> getWorkspaceAll(){
       List<WorkspaceDTO> workspace = workspaceService.getWorkspaceAll();
       return ResponseEntity.ok(workspace);
   }

}