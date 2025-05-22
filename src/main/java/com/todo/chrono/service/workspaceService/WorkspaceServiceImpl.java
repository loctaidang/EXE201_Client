package com.todo.chrono.service.workspaceService;

import com.todo.chrono.util.error.IdInvalidException;
import com.todo.chrono.dto.request.UserDTO;
import com.todo.chrono.dto.request.WorkspaceDTO;
import com.todo.chrono.repository.WorkspaceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.todo.chrono.entity.User;
import com.todo.chrono.entity.Workspace;
import com.todo.chrono.mapper.WorkspaceMapper;
import com.todo.chrono.repository.UserRepository;
import com.todo.chrono.mapper.UserMapper;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {

    private WorkspaceRepository workspaceRepository;
    private UserRepository userRepository;


    @Override
    public WorkspaceDTO createWorkspace(WorkspaceDTO workspaceDTO, Integer user_id) throws IdInvalidException {
        Workspace workspace = WorkspaceMapper.mapToWorkspace(workspaceDTO);
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new IdInvalidException("User: "+user_id+" not found"));
        workspace.setUser(user);
        if (workspaceRepository.existsByUserIdAndName(user_id, workspace.getName())) {
            throw new IdInvalidException("Workspace với tên = " + workspace.getName() + " đã tồn tại trong User id = " + user_id);
        }
        workspace.setCreatedAt(LocalDateTime.now());
        workspace.setUpdatedAt(LocalDateTime.now());
        Workspace savedWorkspace = workspaceRepository.save(workspace);
        return WorkspaceMapper.mapToWorkspaceDTO(savedWorkspace);
    }

    @Override
    public WorkspaceDTO getWorkspaceById(Integer workspace_id) throws IdInvalidException {
        Optional<Workspace> workspace = workspaceRepository.findById(workspace_id);
        if (workspace.isPresent()) {
            return WorkspaceMapper.mapToWorkspaceDTO(workspace.get());
        }else{
                throw new IdInvalidException("Workspace với id = " + workspace_id + " không tồn tại");
        }

    }

    @Override
    public WorkspaceDTO updateWorkspace(WorkspaceDTO updateWorkspace, Integer workspace_id) throws IdInvalidException {
        Workspace workspace = workspaceRepository.findById(workspace_id)
                .orElseThrow(()-> new RuntimeException("Workspace "+workspace_id+" not found"));
        workspace.setName(updateWorkspace.getName());
        if (workspaceRepository.existsByUserIdAndName(workspace.getUser().getId(), workspace.getName())) {
            throw new IdInvalidException("Workspace với tên = " + workspace.getName() + " đã tồn tại trong User id = " + workspace.getUser().getId());
        }
        workspace.setUpdatedAt(LocalDateTime.now());
        Workspace updateWorkspaceObj = workspaceRepository.save(workspace);
        return WorkspaceMapper.mapToWorkspaceDTO(updateWorkspaceObj);
    }

    @Override
    public void deleteWorkspace(Integer workspace_id) throws IdInvalidException {
        Workspace workspace = workspaceRepository.findById(workspace_id)
                .orElseThrow(() -> new IdInvalidException("Workspace với id = " + workspace_id + " không tồn tại"));
        workspaceRepository.deleteById(workspace_id);
    }

    @Override
    public List<WorkspaceDTO> getWorkspacesByUserId(int user_id) throws IdInvalidException {
        List<Workspace> workspaces = workspaceRepository.findWorkspacesByUserId(user_id);
        if (workspaces == null) {
            throw new IdInvalidException("Trong User id = " + user_id + " hiện không có workspace");
        }
        return workspaces.stream().map(
                (workspace) -> WorkspaceMapper.mapToWorkspaceDTO(workspace)).collect(Collectors.toList()
        );
    }

    @Override
    public UserDTO getUserIdByWorkspaceId(int workspace_id) {
        Workspace workspace = workspaceRepository.findWorkspaceById(workspace_id);
        return UserMapper.mapToUserDTO(workspace.getUser());
    }

   @Override
   public List<WorkspaceDTO> getWorkspaceAll() {
       List<Workspace> workspaces = workspaceRepository.findAll();
       return workspaces.stream().map(
               (workspace) -> WorkspaceMapper.mapToWorkspaceDTO(workspace)).collect(Collectors.toList()
       );
   }
}