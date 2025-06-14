package com.todo.chrono.mapper;


import com.todo.chrono.dto.request.UserDTO;
import com.todo.chrono.entity.User;
import com.todo.chrono.dto.request.UserCreateDTO;

public class UserMapper {

    public static UserDTO mapToUserDTO(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(user.getPassword());
        userDTO.setRole(user.getRole());
        userDTO.setPremiumExpiry(user.getPremiumExpiry());
        userDTO.setImageUrl(user.getImageUrl());
        userDTO.setDeleted(user.isDeleted());
        userDTO.setName(user.getName());
        return userDTO;

    }
    public static User mapToUser(UserDTO userDTO){
        User user = new User();
        user.setId(userDTO.getId());
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setRole(userDTO.getRole());
        user.setPremiumExpiry(userDTO.getPremiumExpiry());
        user.setDeleted(userDTO.isDeleted());
        user.setImageUrl(userDTO.getImageUrl());
        user.setName(userDTO.getName());
        return user;
    }
    public static User mapToUser(UserCreateDTO userCreateDTO){
        User user = new User();
        user.setUsername(userCreateDTO.getUsername());
        user.setPassword(userCreateDTO.getPassword());
        user.setName(userCreateDTO.getName());
        return user;
    }
}
