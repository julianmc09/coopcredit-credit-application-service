package com.riwi.io.coopcredit_credit_application_service.domain.services;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.User;
import com.riwi.io.coopcredit_credit_application_service.domain.ports.in.LoginUserUseCase;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.UserRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LoginUserService implements LoginUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String loginUser(String username, String password) {
        User user = userRepositoryPort.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        // In a real application, the JWT token generation would happen here or in a security adapter.
        // For now, we return a placeholder as the domain service's responsibility is authentication.
        return "AUTHENTICATED_SUCCESSFULLY_PLACEHOLDER_TOKEN";
    }
}
