package com.todo.chrono.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import com.todo.chrono.service.taskAssigneeService.TaskAssigneeService;
import com.todo.chrono.dto.request.TaskAssigneeDTO;
import com.todo.chrono.util.error.IdInvalidException;
import java.util.List;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;

@CrossOrigin("*")
@RestController
@AllArgsConstructor
@RequestMapping("/task-assignees")
@SecurityRequirement(name = "api")
public class TaskAssigneeController {
    private final TaskAssigneeService taskAssigneeService;

    @PostMapping("/{taskId}/assign/{userId}")
    public TaskAssigneeDTO assignUserToTask(
            @PathVariable Integer taskId,
            @PathVariable Integer userId) throws IdInvalidException {
        return taskAssigneeService.assignUserToTask(taskId, userId);
    }

    // Gỡ user khỏi task
    @DeleteMapping("/{taskId}/remove/{userId}")
    public void removeUserFromTask(
            @PathVariable Integer taskId,
            @PathVariable Integer userId) throws IdInvalidException {
        taskAssigneeService.removeUserFromTask(taskId, userId);
    }

    // Lấy danh sách người được gán theo task
    @GetMapping("/task/{taskId}")
    public List<TaskAssigneeDTO> getAssigneesByTaskId(@PathVariable Integer taskId) {
        return taskAssigneeService.getAssigneesByTaskId(taskId);
    }

    // Lấy danh sách task theo user được gán
    @GetMapping("/user/{userId}")
    public List<TaskAssigneeDTO> getTasksByUserId(@PathVariable Integer userId) {
        return taskAssigneeService.getTasksByAssigneeUserId(userId);
    }
}
