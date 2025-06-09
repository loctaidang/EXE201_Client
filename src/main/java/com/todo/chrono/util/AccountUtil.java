package com.todo.chrono.util;

import com.todo.chrono.entity.User;
import com.todo.chrono.repository.UserRepository;
import com.todo.chrono.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountUtil {

    private final UserRepository userRepository;

    public User getCurrentUser() throws IdInvalidException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Giả sử email là username

        return userRepository.findByUsername(email)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy người dùng hiện tại"));
    }
}
