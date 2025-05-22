package com.todo.chrono.dto.request;


import com.todo.chrono.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private int id;

    private String username;

    private String password;

    private boolean deleted;

    private LocalDateTime premiumExpiry;     

    private Role role;

}