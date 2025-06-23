package com.todo.chrono.repository;

import com.todo.chrono.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.todo.chrono.enums.WorkspaceStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Integer> {
    Optional<Workspace> findById(Integer id);

    boolean existsById(Integer id);

    boolean existsByUserIdAndName(Integer userId, String name);

    int countByUserId(Integer userId);

    List<Workspace> findAllByUserId(int userId);

    int countByUserIdAndStatusNot(int userId, WorkspaceStatus status);

    @Query(value = "SELECT * FROM workspaces WHERE id = :workspaceId", nativeQuery = true)
    Workspace findWorkspaceById(@Param("workspaceId") Integer workspaceId);

    @Query(value = "SELECT * FROM workspaces WHERE user_id = :userId", nativeQuery = true)
    List<Workspace> findWorkspacesByUserId(@Param("userId") Integer userId);

    // // Lấy danh sách user đã xóa mềm bằng native query
    // @Query(value = "SELECT * FROM users WHERE deleted = true", nativeQuery =
    // true)
    // List<Workspace> findAllByIsDeletedTrue();

    // // Tìm user đã xóa mềm theo ID bằng native query
    // @Query(value = "SELECT * FROM users WHERE id = :userId AND deleted = true",
    // nativeQuery = true)
    // Optional<Workspace> findByIdAndIsDeletedTrue(@Param("userId") Integer
    // userId);

}