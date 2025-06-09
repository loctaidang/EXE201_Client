package com.todo.chrono.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import com.todo.chrono.enums.RoleWorkspaceMember;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceMemberDTO {
    private Integer id;
    private Integer workspaceId;
    private Integer userId;
    private RoleWorkspaceMember role;
    private LocalDateTime joinedAt;
}