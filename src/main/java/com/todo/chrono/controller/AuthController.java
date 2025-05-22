package com.todo.chrono.controller;


import com.todo.chrono.util.error.IdInvalidException;
import com.todo.chrono.dto.request.LoginDTO;
import com.todo.chrono.dto.request.RegisterRequestDTO;
import com.todo.chrono.dto.response.ResLoginDTO;
import com.todo.chrono.entity.User;
import com.todo.chrono.service.authService.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@AllArgsConstructor
@RequestMapping("api")
@SecurityRequirement(name = "api")  
public class AuthController {

    @Autowired
    AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        ResLoginDTO res = authService.login(loginDTO);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }


    @PostMapping("/register")
    public ResponseEntity<User> createUser(@Valid @RequestBody RegisterRequestDTO dto) throws IdInvalidException {
//        boolean isOtpValid = otpService.validateOtp(userDTO.getPhone(), otp);
//        if (!isOtpValid) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
//        }
        User user = authService.register(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }


    // @PostMapping("/login/student")
    // public ResponseEntity<ResLoginDTO> loginStudent(@Valid @RequestBody LoginDTO loginDTO) {
    //     ResLoginDTO res = authService.login(loginDTO);
    //     return ResponseEntity.status(HttpStatus.OK).body(res);
    // }

}
