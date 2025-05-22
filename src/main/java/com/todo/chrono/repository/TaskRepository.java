package com.todo.chrono.repository;

import com.todo.chrono.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task,Integer> {
    Optional<Task> findById(Integer id);
    boolean existsById(Integer id);

    // // Lấy danh sách user đã xóa mềm bằng native query
    // @Query(value = "SELECT * FROM tasks WHERE deleted = true", nativeQuery = true)
    // List<Task> findAllByIsDeletedTrue();

    // // Tìm user đã xóa mềm theo ID bằng native query
    // @Query(value = "SELECT * FROM tasks WHERE id = :taskId AND deleted = true", nativeQuery = true)
    // Optional<Task> findByIdAndIsDeletedTrue(@Param("taskId") Integer taskId);

}