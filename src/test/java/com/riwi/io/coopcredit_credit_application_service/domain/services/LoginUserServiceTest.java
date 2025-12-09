package com.riwi.io.coopcredit_credit_application_service.domain.services;

import com.riwi.io.coopcredit_credit_application_service.infrastructure.configuration.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUserServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private LoginUserService loginUserService;

    private UserDetails testUserDetails;

    @BeforeEach
    void setUp() {
        testUserDetails = User.withUsername("testuser")
                .password("encodedPassword")
                .roles("AFILIADO")
                .build();
    }

    @Test
    @DisplayName("Should return a JWT token on successful login")
    void shouldReturnJwtTokenOnSuccessfulLogin() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null); // AuthenticationManager doesn't return anything useful for this test
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(testUserDetails);
        when(jwtService.generateToken(testUserDetails)).thenReturn("mocked-jwt-token");

        String token = loginUserService.loginUser("testuser", "rawPassword");

        assertNotNull(token);
        assertEquals("mocked-jwt-token", token);
    }

    @Test
    @DisplayName("Should throw BadCredentialsException on invalid username or password")
    void shouldThrowBadCredentialsExceptionOnInvalidCredentials() {
        doThrow(new BadCredentialsException("Invalid username or password")).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            loginUserService.loginUser("wronguser", "wrongpassword");
        });

        assertEquals("Invalid username or password", exception.getMessage());
    }
}
