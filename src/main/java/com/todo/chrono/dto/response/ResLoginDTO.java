package com.todo.chrono.dto.response;

import com.todo.chrono.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResLoginDTO extends User {
    private String token;
}

