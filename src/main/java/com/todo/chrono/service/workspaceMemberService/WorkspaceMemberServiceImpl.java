package com.todo.chrono.service.workspaceMemberService;

import com.todo.chrono.dto.request.WorkspaceMemberDTO;
import com.todo.chrono.entity.User;
import com.todo.chrono.entity.Workspace;
import com.todo.chrono.entity.WorkspaceMember;
import com.todo.chrono.mapper.WorkspaceMemberMapper;
import com.todo.chrono.repository.TaskRepository;
import com.todo.chrono.repository.UserRepository;
import com.todo.chrono.repository.WorkspaceMemberRepository;
import com.todo.chrono.repository.WorkspaceRepository;
import com.todo.chrono.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.todo.chrono.enums.RoleWorkspaceMember;
import com.todo.chrono.enums.TaskStatus;
import com.todo.chrono.util.AccountUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkspaceMemberServiceImpl implements WorkspaceMemberService {

    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final AccountUtil accountUtil;
    private final TaskRepository taskRepository;

    @Override
    public WorkspaceMemberDTO addMemberToWorkspace(Integer workspaceId, Integer userId)
            throws IdInvalidException {

        // Lấy user hiện tại (người muốn mời)
        User currentUser = accountUtil.getCurrentUser();

        // Kiểm tra premium còn hiệu lực
        if (currentUser.getPremiumExpiry() == null
                || currentUser.getPremiumExpiry().isBefore(java.time.LocalDateTime.now())) {
            throw new IdInvalidException("Chỉ người dùng Premium mới có thể thêm thành viên vào workspace.");
        }

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new IdInvalidException("Workspace ID không hợp lệ"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdInvalidException("User ID không hợp lệ"));

        boolean exists = workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, userId);
        if (exists) {
            throw new IdInvalidException("User đã là thành viên của workspace");
        }

        WorkspaceMember member = new WorkspaceMember();
        member.setWorkspace(workspace);
        member.setUser(user);
        member.setRole(RoleWorkspaceMember.MEMBER);
        member.setJoinedAt(java.time.LocalDateTime.now());

        WorkspaceMember saved = workspaceMemberRepository.save(member);
        return WorkspaceMemberMapper.mapToWorkspaceMemberDTO(saved);
    }

    @Override
    public List<WorkspaceMemberDTO> getMembersByWorkspaceId(Integer workspaceId) {
        List<WorkspaceMember> members = workspaceMemberRepository.findByWorkspaceId(workspaceId);
        return members.stream()
                .map(WorkspaceMemberMapper::mapToWorkspaceMemberDTO)
                .collect(Collectors.toList());
    }

    @Override
    public WorkspaceMemberDTO updateMemberRole(Integer workspaceId, Integer targetUserId, RoleWorkspaceMember newRole)
            throws IdInvalidException {

        User currentUser = accountUtil.getCurrentUser();
        Integer currentUserId = currentUser.getId();

        WorkspaceMember currentUserMember = workspaceMemberRepository
                .findByWorkspaceIdAndUserId(workspaceId, currentUserId)
                .orElseThrow(() -> new IdInvalidException("Người dùng không phải thành viên workspace"));

        if (currentUserMember.getRole() != RoleWorkspaceMember.OWNER) {
            throw new IdInvalidException("Chỉ OWNER mới có quyền cập nhật thông tin workspace.");
        }

        if (newRole == RoleWorkspaceMember.OWNER) {
            throw new IdInvalidException("Không thể gán quyền OWNER cho thành viên khác.");
        }

        WorkspaceMember targetMember = workspaceMemberRepository
                .findByWorkspaceIdAndUserId(workspaceId, targetUserId)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy thành viên cần cập nhật"));

        targetMember.setRole(newRole);
        WorkspaceMember updated = workspaceMemberRepository.save(targetMember);

        return WorkspaceMemberMapper.mapToWorkspaceMemberDTO(updated);
    }

    @Override
    public void removeMemberFromWorkspace(Integer workspaceId, Integer targetUserId)
            throws IdInvalidException {

        User currentUser = accountUtil.getCurrentUser();
        Integer currentUserId = currentUser.getId();

        WorkspaceMember currentUserMember = workspaceMemberRepository
                .findByWorkspaceIdAndUserId(workspaceId, currentUserId)
                .orElseThrow(
                        () -> new IdInvalidException("Người dùng hiện tại không phải là thành viên của workspace."));

        if (currentUserMember.getRole() != RoleWorkspaceMember.OWNER) {
            throw new IdInvalidException("Chỉ OWNER mới có quyền xóa thành viên.");
        }

        if (currentUserId.equals(targetUserId)) {
            throw new IdInvalidException("OWNER không thể tự xóa chính mình khỏi workspace.");
        }

        WorkspaceMember targetMember = workspaceMemberRepository
                .findByWorkspaceIdAndUserId(workspaceId, targetUserId)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy thành viên cần xóa."));

        workspaceMemberRepository.delete(targetMember);
    }

    @Override
    public void leaveWorkspace(Integer workspaceId, Integer userId) throws IdInvalidException {
        WorkspaceMember member = workspaceMemberRepository
                .findByWorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy thành viên trong workspace"));

        if (member.getRole() == RoleWorkspaceMember.OWNER) {
            throw new IdInvalidException(
                    "OWNER không thể tự rời khỏi workspace. Vui lòng xoá workspace trước.");
        }

        workspaceMemberRepository.delete(member);
    }

}
