package com.todo.chrono.controller;

import com.todo.chrono.util.error.IdInvalidException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.todo.chrono.dto.request.WorkspaceDTO;
import com.todo.chrono.service.taskService.TaskService;
import com.todo.chrono.dto.request.TaskDTO;
import com.todo.chrono.dto.request.TaskBriefDTO;
import com.todo.chrono.dto.request.TaskCreateDTO;
import com.todo.chrono.enums.TaskStatus;
import java.util.List;

@CrossOrigin("*")
@RestController
@AllArgsConstructor
@RequestMapping("/task")
@SecurityRequirement(name = "api")
public class TaskController {

    private TaskService taskService;

    @PostMapping("/workspace/{workspace_id}")
    @PreAuthorize("hasAnyRole('FREE', 'PREMIUM','ADMIN')")
    public ResponseEntity<TaskDTO> createTask(@PathVariable("workspace_id") Integer workspace_id,
            @RequestBody TaskCreateDTO taskCreateDTO) throws IdInvalidException {
        TaskDTO savedTask = taskService.createTask(taskCreateDTO, workspace_id);
        return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
    }

    @GetMapping("/workspace/{workspace_id}")
    @PreAuthorize("hasAnyRole('FREE', 'PREMIUM','ADMIN')")
    public ResponseEntity<List<TaskDTO>> getTaskByWorkspaceId(@PathVariable("workspace_id") int workspace_id)
            throws IdInvalidException {
        List<TaskDTO> taskDTOs = taskService.getTasksByWorkspaceId(workspace_id);
        return ResponseEntity.ok(taskDTOs);
    }

    @GetMapping("/{task_id}")
    @PreAuthorize("hasAnyRole('FREE', 'PREMIUM','ADMIN')")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable("task_id") Integer task_id) throws IdInvalidException {
        TaskDTO taskDTO = taskService.getTaskById(task_id);
        return ResponseEntity.ok(taskDTO);
    }

    @PutMapping("/{task_id}")
    @PreAuthorize("hasAnyRole('FREE', 'PREMIUM','ADMIN')")
    public ResponseEntity<TaskDTO> updateTask(@RequestBody TaskCreateDTO updatedTask,
            @PathVariable("task_id") Integer taskId) throws IdInvalidException {
        TaskDTO taskDTO = taskService.updateTask(updatedTask, taskId);
        return ResponseEntity.ok(taskDTO);
    }

    @DeleteMapping("/{task_id}")
    @PreAuthorize("hasAnyRole('FREE', 'PREMIUM','ADMIN')")
    public ResponseEntity<Void> deleteTask(@PathVariable("task_id") Integer task_id) throws IdInvalidException {
        TaskDTO currentTask = this.taskService.getTaskById(task_id);
        this.taskService.deleteTask(currentTask.getId());
        return ResponseEntity.ok(null);
    }

    @GetMapping("/{task_id}/workspace")
    @PreAuthorize("hasAnyRole('FREE', 'PREMIUM','ADMIN')")
    public ResponseEntity<WorkspaceDTO> getWorkspaceIdByTaskId(@PathVariable("task_id") int task_id)
            throws IdInvalidException {
        WorkspaceDTO workspaceDTO = taskService.getWorkspaceIdByTaskId(task_id);
        return ResponseEntity.ok(workspaceDTO);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('FREE', 'PREMIUM','ADMIN')")
    public ResponseEntity<List<TaskDTO>> getTaskAll() {
        List<TaskDTO> task = taskService.getTaskAll();
        return ResponseEntity.ok(task);
    }

    @GetMapping("/workspace/{workspace_id}/status/{status}")
    @PreAuthorize("hasAnyRole('FREE', 'PREMIUM','ADMIN')")
    public ResponseEntity<List<TaskDTO>> getTasksByWorkspaceIdAndStatus(@PathVariable("workspace_id") int workspace_id,
            @PathVariable("status") TaskStatus status) throws IdInvalidException {
        List<TaskDTO> task = taskService.getTasksByWorkspaceIdAndStatus(workspace_id, status);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/users/{userId}/tasks/top5")
    public ResponseEntity<List<TaskBriefDTO>> getTop5TasksToDo(@PathVariable int userId) throws IdInvalidException {
        List<TaskBriefDTO> result = taskService.getTop5TasksTodoByUserId(userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/users/{userId}/completed-count")
    @PreAuthorize("hasAnyRole('FREE', 'PREMIUM','ADMIN')")
    public ResponseEntity<Integer> countCompletedTasksByUser(@PathVariable int userId) throws IdInvalidException {
        int count = taskService.countCompletedTasksByUserId(userId);
        return ResponseEntity.ok(count);
    }

}