package com.todo.chrono.repository;

import com.todo.chrono.entity.Task;
import com.todo.chrono.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    Optional<Task> findById(Integer id);

    boolean existsById(Integer id);

    int countByWorkspaceId(Integer workspaceId);

    boolean existsByWorkspaceIdAndTitle(Integer workspaceId, String title);

    List<Task> findAllByWorkspaceId(Integer workspaceId);

    List<Task> findAllByWorkspaceIdAndStatus(Integer workspaceId, TaskStatus status);

    int countByWorkspaceIdAndStatus(int workspaceId, TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.workspace.user.id = :userId")
    List<Task> findAllByUserId(@Param("userId") int userId);

    @Query("SELECT COUNT(t) FROM Task t JOIN t.workspace w JOIN w.workspaceMembers wm " +
            "WHERE wm.user.id = :userId AND t.status = com.todo.chrono.enums.TaskStatus.COMPLETED")
    int countCompletedTasksByUserId(@Param("userId") int userId);

    // // Lấy danh sách user đã xóa mềm bằng native query
    // @Query(value = "SELECT * FROM tasks WHERE deleted = true", nativeQuery =
    // true)
    // List<Task> findAllByIsDeletedTrue();

    // // Tìm user đã xóa mềm theo ID bằng native query
    // @Query(value = "SELECT * FROM tasks WHERE id = :taskId AND deleted = true",
    // nativeQuery = true)
    // Optional<Task> findByIdAndIsDeletedTrue(@Param("taskId") Integer taskId);

}