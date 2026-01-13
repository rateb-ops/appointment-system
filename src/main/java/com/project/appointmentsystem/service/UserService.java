package com.project.appointmentsystem.service;
import com.project.appointmentsystem.Dto.AuthResponse;
import com.project.appointmentsystem.Dto.Login;
import com.project.appointmentsystem.Dto.Register;
import com.project.appointmentsystem.Security.JwtUtil;
import com.project.appointmentsystem.entity.userEntity;
import com.project.appointmentsystem.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordencoder;
    public UserService(UserRepository userRepository,JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordencoder = passwordEncoder;
    }

    @Transactional
    public userEntity register(Register request) {

            userEntity user = new userEntity();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordencoder.encode(request.getPassword()));
            user.setRole(request.getRole());
            return userRepository.save(user);
    }


    public AuthResponse login(Login request) {
        userEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if(!passwordencoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getRole().name());
    }







}
