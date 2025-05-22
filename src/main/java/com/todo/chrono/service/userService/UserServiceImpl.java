package com.todo.chrono.service.userService;


import com.todo.chrono.util.error.IdInvalidException;
import com.todo.chrono.dto.request.UserDTO;
import com.todo.chrono.dto.response.ResCreateUserDTO;
import com.todo.chrono.dto.response.ResUpdateUserDTO;
import com.todo.chrono.dto.response.ResUserDTO;
import com.todo.chrono.entity.User;
import com.todo.chrono.mapper.UserMapper;
import com.todo.chrono.repository.UserRepository;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO createUser(UserDTO userDTO) throws IdInvalidException {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new IdInvalidException("Username " + userDTO.getUsername() + " đã tồn tại, vui lòng sử dụng email khác.");
        }
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        User user = UserMapper.mapToUser(userDTO);
        user.setRole(userDTO.getRole());
        user.setImageUrl(userDTO.getImageUrl());
        User savedUser= userRepository.save(user);
        return UserMapper.mapToUserDTO(savedUser);
    }

    @Override
    public UserDTO getUserById(Integer user_id) throws IdInvalidException {
        Optional<User> user = userRepository.findById(user_id);
        if (user.isPresent()) {
            return UserMapper.mapToUserDTO(user.get());
        }else{
            throw new IdInvalidException("User với id = " + user_id + " không tồn tại");
        }
    }

    @Override
    public List<ResUserDTO> getUserAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserMapper::mapToUserDTO)
                .map(this::convertToResUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO updateUser(UserDTO updateUser, Integer user_id) {
        User user = userRepository.findById(user_id)
                .orElseThrow(()-> new RuntimeException("User "+user_id+" not found"));
        user.setUsername(updateUser.getUsername());
        user.setRole(updateUser.getRole());
        user.setImageUrl(updateUser.getImageUrl());
        User updateUserObj = userRepository.save(user);
        return UserMapper.mapToUserDTO(updateUserObj);
    }

    @Override
    public void deleteUser(Integer user_id) throws IdInvalidException {
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new IdInvalidException("User với id = " + user_id + " không tồn tại hoặc đã bị xóa"));
        user.setDeleted(true); // Xóa mềm
        userRepository.save(user);
    }

    @Override
    public UserDTO handleGetUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(()->new RuntimeException("Not exits"+username));

        return UserMapper.mapToUserDTO(user);
    }

    public boolean isUsernameExist(String username) {
        return this.userRepository.existsByUsername(username);
    }

    @Override
    public ResCreateUserDTO convertToResCreateUserDTO(UserDTO user) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        res.setId(user.getId());
        res.setUsername(user.getUsername());
        res.setRole(user.getRole());
        res.setDeleted(user.isDeleted());
        res.setPremiumExpiry(user.getPremiumExpiry());
        res.setImageUrl(user.getImageUrl());
        return res;
    }
    @Override
    public ResUpdateUserDTO convertToResUpdateUserDTO(UserDTO user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        res.setId(user.getId());
        res.setUsername(user.getUsername());
        res.setRole(user.getRole());
        res.setDeleted(user.isDeleted());
        res.setPremiumExpiry(user.getPremiumExpiry());
        res.setImageUrl(user.getImageUrl());
        return res;
    }
    @Override
    public ResUserDTO convertToResUserDTO(UserDTO user) {
        ResUserDTO res = new ResUserDTO();
        res.setId(user.getId());
        res.setUsername(user.getUsername());
        res.setRole(user.getRole());
        res.setDeleted(user.isDeleted());
        res.setPremiumExpiry(user.getPremiumExpiry());
        res.setImageUrl(user.getImageUrl());
        return res;
    }

    @Override
    public List<UserDTO> getDeletedUsers() {
        List<User> deletedUsers = userRepository.findAllByIsDeletedTrue();
        return deletedUsers.stream()
                .map(UserMapper::mapToUserDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void restoreUser(Integer userId) throws IdInvalidException {
        User user = userRepository.findByIdAndIsDeletedTrue(userId)
                .orElseThrow(() -> new IdInvalidException("User với id = " + userId + " không tồn tại hoặc chưa bị xóa mềm"));
        user.setDeleted(false); // Phục hồi
        userRepository.save(user);
    }
}
