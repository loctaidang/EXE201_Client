package com.todo.chrono.controller;

import com.todo.chrono.util.error.IdInvalidException;
import com.todo.chrono.dto.request.UserDTO;
import com.todo.chrono.dto.response.ResCreateUserDTO;
import com.todo.chrono.dto.response.ResUpdateUserDTO;
import com.todo.chrono.dto.response.ResUserDTO;
import com.todo.chrono.service.userService.UserService;
import com.todo.chrono.dto.request.UserCreateDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@AllArgsConstructor

@SecurityRequirement(name = "brearerAuth")
@RequestMapping("/user")
@SecurityRequirement(name = "api")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private UserService userService;
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'FREE', 'PREMIUM') ")
    public ResponseEntity<ResCreateUserDTO> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) throws IdInvalidException {
        UserDTO savedUser = userService.createUser(userCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(savedUser));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'FREE', 'PREMIUM') ")
    @GetMapping("/get/{user_id}")
    public ResponseEntity<ResUserDTO> getUserById (@PathVariable("user_id") Integer user_id) throws IdInvalidException{
        UserDTO userDTO = userService.getUserById(user_id);
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUserDTO(userDTO));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'FREE', 'PREMIUM') ")
    @GetMapping("/get/all")
    public ResponseEntity<List<ResUserDTO>> getUserAll(){
        List<ResUserDTO> user = userService.getUserAll();
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username : {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'FREE', 'PREMIUM') ")
    @PutMapping("/{user_id}")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody UserCreateDTO updatedUser, @PathVariable("user_id") Integer user_id) throws IdInvalidException{
        UserDTO userDTO = userService.updateUser(updatedUser,user_id);
        return ResponseEntity.ok(this.userService.convertToResUpdateUserDTO(userDTO));
    }

//    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') ")
//    @DeleteMapping("/delete/{user_id}")
//    public ResponseEntity<Void> deleteUser(@PathVariable("user_id") Integer user_id) throws IdInvalidException{
//        UserDTO currentUser = this.userService.getUserById(user_id);
//        this.userService.deleteUser(user_id);
//        return ResponseEntity.ok(null);
//    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/delete/{user_id}")
    public ResponseEntity<String> deleteUser(@PathVariable("user_id") Integer user_id) throws IdInvalidException {
        userService.deleteUser(user_id);
        return ResponseEntity.ok("User với id = " + user_id + " đã được xóa mềm thành công");
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/deleted")
    public ResponseEntity<List<UserDTO>> getDeletedUsers() {
        List<UserDTO> deletedUsers = userService.getDeletedUsers();
        return ResponseEntity.ok(deletedUsers);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/restore/{user_id}")
    public ResponseEntity<String> restoreUser(@PathVariable("user_id") Integer user_id) throws IdInvalidException {
        userService.restoreUser(user_id);
        return ResponseEntity.ok("User với id = " + user_id + " đã được phục hồi thành công");
    }

}
