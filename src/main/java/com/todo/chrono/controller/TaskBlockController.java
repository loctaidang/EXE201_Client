package com.todo.chrono.controller;

import com.todo.chrono.dto.request.TaskBlockDTO;
import com.todo.chrono.dto.request.TaskBlockCreateDTO;
import com.todo.chrono.service.taskBlockService.TaskBlockService;
import com.todo.chrono.util.error.IdInvalidException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@CrossOrigin("*")
@RestController
@AllArgsConstructor
@RequestMapping("/task-blocks")
@SecurityRequirement(name = "api")
public class TaskBlockController {
    private final TaskBlockService taskBlockService;

    @PostMapping("/task/{taskId}")
    public ResponseEntity<TaskBlockDTO> createTaskBlock(@PathVariable int taskId,
            @RequestBody TaskBlockCreateDTO dto) {
        try {
            return ResponseEntity.ok(taskBlockService.createTaskBlock(dto, taskId));
        } catch (IdInvalidException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<TaskBlockDTO>> getBlocksByTaskId(@PathVariable int taskId) {
        return ResponseEntity.ok(taskBlockService.getTaskBlocksByTaskId(taskId));
    }

    @GetMapping("/user/{userId}/datetime-range")
    public ResponseEntity<List<TaskBlockDTO>> getBlocksByDateTimeRange(
            @PathVariable int userId,
            @RequestParam("start") String start,
            @RequestParam("end") String end) {
        LocalDateTime startTime = LocalDateTime.parse(start);
        LocalDateTime endTime = LocalDateTime.parse(end);
        return ResponseEntity.ok(taskBlockService.getTaskBlocksByDateTimeRange(userId, startTime, endTime));
    }

    @GetMapping("/workspace/{workspaceId}/datetime-range")
    public ResponseEntity<List<TaskBlockDTO>> getBlocksByWorkspaceAndDateTimeRange(
            @PathVariable int workspaceId,
            @RequestParam("start") String start,
            @RequestParam("end") String end) {
        LocalDateTime startTime = LocalDateTime.parse(start);
        LocalDateTime endTime = LocalDateTime.parse(end);
        return ResponseEntity
                .ok(taskBlockService.getBlocksByWorkspaceAndDateTimeRange(workspaceId, startTime, endTime));
    }

    @PutMapping("/block/{blockId}")
    public ResponseEntity<TaskBlockDTO> updateBlock(@PathVariable int blockId,
            @RequestBody TaskBlockCreateDTO dto) {
        try {
            return ResponseEntity.ok(taskBlockService.updateTaskBlock(blockId, dto));
        } catch (IdInvalidException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.unprocessableEntity().body(null);
        }
    }

    @DeleteMapping("/{blockId}")
    public ResponseEntity<Void> deleteBlock(@PathVariable int blockId) {
        try {
            taskBlockService.deleteTaskBlock(blockId);
            return ResponseEntity.noContent().build();
        } catch (IdInvalidException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
