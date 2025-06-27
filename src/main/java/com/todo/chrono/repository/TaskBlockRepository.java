package com.todo.chrono.repository;

import com.todo.chrono.entity.TaskBlock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskBlockRepository extends JpaRepository<TaskBlock, Integer> {
    List<TaskBlock> findByTaskId(int taskId);
    List<TaskBlock> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    List<TaskBlock> findByTask_Workspace_IdAndStartTimeBetween(int workspaceId, LocalDateTime start, LocalDateTime end);
}