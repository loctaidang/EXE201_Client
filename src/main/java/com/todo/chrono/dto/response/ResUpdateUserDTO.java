package com.todo.chrono.dto.response;

import com.todo.chrono.enums.Role;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ResUpdateUserDTO {
    private int id;

    private String username;

    private LocalDateTime premiumExpiry;

    private Role role;

    private boolean deleted;

    private String imageUrl;
}
