package com.todo.chrono.service.taskAssigneeService;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import com.todo.chrono.repository.TaskRepository;
import com.todo.chrono.repository.UserRepository;
import com.todo.chrono.repository.TaskAssigneeRepository;
import com.todo.chrono.util.error.IdInvalidException;
import com.todo.chrono.entity.Task;
import com.todo.chrono.entity.User;
import com.todo.chrono.entity.TaskAssignee;
import com.todo.chrono.mapper.TaskAssigneeMapper;
import com.todo.chrono.dto.request.TaskAssigneeDTO;
import java.util.List;
import java.util.stream.Collectors;
import com.todo.chrono.repository.WorkspaceMemberRepository;
import com.todo.chrono.util.AccountUtil;

@Service
@AllArgsConstructor
public class TaskAssigneeServiceImpl implements TaskAssigneeService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskAssigneeRepository taskAssigneeRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final AccountUtil accountUtil;

    @Override
    public TaskAssigneeDTO assignUserToTask(Integer taskId, Integer userId) throws IdInvalidException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IdInvalidException("Task ID " + taskId + " không tồn tại"));
        User currentUser = accountUtil.getCurrentUser();

        // Kiểm tra current user có phải là thành viên workspace không
        boolean isCurrentUserMember = workspaceMemberRepository
                .existsByWorkspaceIdAndUserId(task.getWorkspace().getId(), currentUser.getId());

        if (!isCurrentUserMember) {
            throw new IdInvalidException("Bạn không có quyền gán task trong workspace này");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdInvalidException("User ID " + userId + " không tồn tại"));

        // ✅ Check user đó có nằm trong workspace chứa task không
        Integer workspaceId = task.getWorkspace().getId();
        boolean isMember = workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, userId);

        if (!isMember) {
            throw new IdInvalidException(
                    "User ID " + userId + " không phải là thành viên của workspace ID " + workspaceId);
        }

        // ✅ Kiểm tra user đã được gán chưa
        boolean exists = taskAssigneeRepository.existsByTaskIdAndUserId(taskId, userId);
        if (exists) {
            throw new IdInvalidException("User ID " + userId + " đã được gán cho Task ID " + taskId);
        }

        TaskAssignee assignee = TaskAssigneeMapper.mapToEntity(task, user);
        taskAssigneeRepository.save(assignee);

        return TaskAssigneeMapper.mapToDTO(assignee);
    }

    @Override
    public void removeUserFromTask(Integer taskId, Integer userId) throws IdInvalidException {
        TaskAssignee assignee = taskAssigneeRepository.findByTaskIdAndUserId(taskId, userId)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy người dùng trong task"));

        taskAssigneeRepository.delete(assignee);
    }

    @Override
    public List<TaskAssigneeDTO> getAssigneesByTaskId(Integer taskId) {
        return taskAssigneeRepository.findByTaskId(taskId)
                .stream()
                .map(TaskAssigneeMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskAssigneeDTO> getTasksByAssigneeUserId(Integer userId) {
        return taskAssigneeRepository.findByUserId(userId)
                .stream()
                .map(TaskAssigneeMapper::mapToDTO)
                .collect(Collectors.toList());
    }

}
