package com.todo.chrono.service.authService;


import com.todo.chrono.util.error.IdInvalidException;
import com.todo.chrono.util.error.NotFoundException;
import com.todo.chrono.util.SecurityUtil;
import com.todo.chrono.dto.request.LoginDTO;
import com.todo.chrono.dto.request.RegisterRequestDTO;
import com.todo.chrono.dto.response.ResLoginDTO;
import com.todo.chrono.entity.User;
import com.todo.chrono.enums.Role;
import com.todo.chrono.repository.UserRepository;
import com.todo.chrono.service.userService.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
//import static jdk.internal.org.jline.reader.impl.LineReaderImpl.CompletionType.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    SecurityUtil securityUtil;
    @Autowired
    UserService userService;

    //    @Autowired
//    StudentRepository studentRepository ;
    public ResLoginDTO login(LoginDTO loginDTO) {
        var user = userRepository.findByUsername(loginDTO.getUsername()).orElseThrow(() -> new NotFoundException("User not found"));
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword()))
            throw new NotFoundException("Wrong password");

        String token = securityUtil.createToken(user);
        ResLoginDTO resLoginDTO = new ResLoginDTO();
        resLoginDTO.setId(user.getId());
        resLoginDTO.setToken(token);
        resLoginDTO.setUsername(user.getUsername());
        resLoginDTO.setRole(user.getRole());
        resLoginDTO.setPassword(user.getPassword());
        return resLoginDTO;
    }

    public User register(RegisterRequestDTO dto) throws IdInvalidException {
        if (userService.isUsernameExist(dto.getUsername())) {
            throw new IdInvalidException("Username " + dto.getUsername() + " đã tồn tại, vui lòng chọn username khác.");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.FREE); // hoặc Role.USER tùy logic
        user.setDeleted(false);
        user.setPremiumExpiry(LocalDateTime.now().plusDays(30));

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Tên đăng nhập đã tồn tại");
        }
    }


    
}
//    public ResLoginDTO loginStudent(LoginDTO loginDTO) {
//        var student = studentRepository.findByUsername(loginDTO.getUsername()).orElseThrow(() -> new NotFoundException("User not found"));
//        if (!passwordEncoder.matches(loginDTO.getPassword(), student.getPassword())) throw new NotFoundException("Wrong password");
//        String token = securityUtil.createTokenStudent(student);
//        ResLoginDTO resLoginDTO = new ResLoginDTO();
//        resLoginDTO.setToken(token);
//        resLoginDTO.setRole(Role.STUDENT);
//        resLoginDTO.setUsername(student.getUsername());
//        resLoginDTO.setEmail(student.getEmail());
//        resLoginDTO.setAddress(student.getAddress());
//        resLoginDTO.setPhone(student.getPhone());
//        resLoginDTO.setFirst_name(student.getFirst_name());
//        resLoginDTO.setLast_name(student.getLast_name());
//        resLoginDTO.setImage(student.getImage());
//        resLoginDTO.setIs_deleted(student.getIs_deleted());
//        resLoginDTO.setUser_id(student.getStudent_id());
//        return resLoginDTO;
//    }
