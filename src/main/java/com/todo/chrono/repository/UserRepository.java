package com.todo.chrono.repository;


import com.todo.chrono.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    // Lấy danh sách user đã xóa mềm bằng native query
    @Query(value = "SELECT * FROM users WHERE deleted = true", nativeQuery = true)
    List<User> findAllByIsDeletedTrue();

    // Tìm user đã xóa mềm theo ID bằng native query
    @Query(value = "SELECT * FROM users WHERE id = :userId AND deleted = true", nativeQuery = true)
    Optional<User> findByIdAndIsDeletedTrue(@Param("userId") Integer userId);

}