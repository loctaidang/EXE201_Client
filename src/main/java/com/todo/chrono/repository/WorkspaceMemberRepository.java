package com.todo.chrono.repository;

import com.todo.chrono.entity.WorkspaceMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, Integer> {
    boolean existsByWorkspaceIdAndUserId(Integer workspaceId, Integer userId);
    Optional<WorkspaceMember> findByWorkspaceIdAndUserId(Integer workspaceId, Integer userId);
    List<WorkspaceMember> findByWorkspaceId(Integer workspaceId);
}
