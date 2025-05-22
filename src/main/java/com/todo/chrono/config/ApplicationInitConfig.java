package com.todo.chrono.config;


import com.todo.chrono.entity.User; 
import com.todo.chrono.enums.Role;
import com.todo.chrono.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationInitConfig {
    private final PasswordEncoder passwordEncoder;

    public ApplicationInitConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository){
        return args -> {

            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
            }



        };
    }
}
