package com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.controller;

import com.riwi.io.coopcredit_credit_application_service.domain.ports.in.LoginUserUseCase;
import com.riwi.io.coopcredit_credit_application_service.domain.ports.in.RegisterUserUseCase;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.dto.request.LoginRequest;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.dto.request.RegisterUserRequest;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.dto.response.LoginResponse;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.dto.response.UserResponse; // Assuming you'll create this
import com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.mapper.UserRestMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final UserRestMapper userRestMapper;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        var user = registerUserUseCase.registerUser(
                request.getUsername(),
                request.getPassword(),
                request.getRole()
        );
        // Assuming UserResponse is a simple DTO for user details without password
        return ResponseEntity.ok(UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = loginUserUseCase.loginUser(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(LoginResponse.builder().token(token).build());
    }
}
