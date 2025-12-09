package com.riwi.io.coopcredit_credit_application_service.domain.services;

import com.riwi.io.coopcredit_credit_application_service.domain.repositories.UserRepositoryPort;
import com.riwi.io.coopcredit_credit_application_service.domain.ports.in.LoginUserUseCase;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.configuration.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUserService implements LoginUserUseCase {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService; // To load UserDetails after authentication
    private final JwtService jwtService;

    @Override
    public String loginUser(String username, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        password
                )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return jwtService.generateToken(userDetails);
    }
}
