package com.todo.chrono.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.todo.chrono.entity.TaskAssignee;
import com.todo.chrono.entity.User;

import java.util.List;
import java.util.Optional;

public interface TaskAssigneeRepository extends JpaRepository<TaskAssignee, Integer> {
    List<TaskAssignee> findByTaskId(Integer taskId);
    List<TaskAssignee> findByUserId(Integer userId);
    Optional<TaskAssignee> findByTaskIdAndUserId(Integer taskId, Integer userId);

    void deleteByTaskId(Integer taskId);
    boolean existsByTaskIdAndUserId(Integer taskId, Integer userId);

    void deleteByTaskIdAndUserId(Integer taskId, Integer userId);

    @Query("SELECT ta.user FROM TaskAssignee ta WHERE ta.task.id = :taskId")
    List<User> findUsersByTaskId(@Param("taskId") Integer taskId);

}
