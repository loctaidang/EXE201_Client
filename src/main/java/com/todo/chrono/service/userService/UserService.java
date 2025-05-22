package com.todo.chrono.service.userService;


import com.todo.chrono.util.error.IdInvalidException;
import com.todo.chrono.dto.request.UserDTO;
import com.todo.chrono.dto.response.ResCreateUserDTO;
import com.todo.chrono.dto.response.ResUpdateUserDTO;
import com.todo.chrono.dto.response.ResUserDTO;

import java.util.List;

public interface UserService {
    UserDTO createUser(UserDTO userDTO) throws IdInvalidException;

    UserDTO getUserById ( Integer user_id) throws IdInvalidException;

    List<ResUserDTO> getUserAll();

    UserDTO updateUser(UserDTO updateUser, Integer topic_id);

    void deleteUser (Integer user_id) throws IdInvalidException;

    UserDTO handleGetUserByUsername(String username);

    boolean isUsernameExist (String username);

    ResCreateUserDTO convertToResCreateUserDTO(UserDTO user);

    ResUpdateUserDTO convertToResUpdateUserDTO(UserDTO user);

    ResUserDTO convertToResUserDTO(UserDTO user);

    // Lấy danh sách user đã xóa mềm
    List<UserDTO> getDeletedUsers();

    // Phục hồi user đã xóa mềm
    void restoreUser(Integer userId) throws IdInvalidException;
}