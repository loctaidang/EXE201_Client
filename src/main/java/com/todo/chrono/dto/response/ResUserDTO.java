package com.todo.chrono.dto.response;


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
public class ResUserDTO {
    private int id;

    private String username;

    private LocalDateTime premiumExpiry;

    private boolean deleted;

    private String imageUrl;

    private Role role;

}
