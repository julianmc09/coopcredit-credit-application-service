package com.riwi.io.coopcredit_credit_application_service.domain.services;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.Role;
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
import static org.mockito.Mockito.*;

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

    private UserDetails userDetails;
    private String username;
    private String password;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        username = "testuser";
        password = "password123";
        jwtToken = "mocked.jwt.token";
        userDetails = User.builder()
                .username(username)
                .password(password)
                .roles(Role.ROLE_AFILIADO.name().replace("ROLE_", "")) // Corrected line
                .build();
    }

    @Test
    @DisplayName("Should successfully log in and return a JWT token")
    void shouldLoginSuccessfullyAndReturnJwtToken() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null); // AuthenticationManager doesn't return anything useful for this test
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn(jwtToken);

        // When
        String resultToken = loginUserService.loginUser(username, password);

        // Then
        assertNotNull(resultToken);
        assertEquals(jwtToken, resultToken);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService, times(1)).loadUserByUsername(username);
        verify(jwtService, times(1)).generateToken(userDetails);
    }

    @Test
    @DisplayName("Should throw BadCredentialsException for invalid credentials")
    void shouldThrowBadCredentialsExceptionForInvalidCredentials() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When & Then
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () ->
                loginUserService.loginUser(username, password)
        );

        assertEquals("Invalid credentials", exception.getMessage());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtService, never()).generateToken(any(UserDetails.class));
    }
}
