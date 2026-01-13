package com.project.appointmentsystem.controller;
import com.project.appointmentsystem.Dto.AuthResponse;
import com.project.appointmentsystem.Dto.Login;
import com.project.appointmentsystem.Dto.Register;
import com.project.appointmentsystem.entity.userEntity;
import com.project.appointmentsystem.repository.UserRepository;
import com.project.appointmentsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final UserService userService;


    public UserController( UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public userEntity register( @Valid @RequestBody Register request) {

        return userService.register(request);
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody Login loginRequest) {
        AuthResponse authResponse = userService.login(loginRequest);
        return ResponseEntity.ok(authResponse);
    }



}


