package com.riwi.io.coopcredit_credit_application_service.infrastructure.configuration.security;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.User;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SecurityHelper {

    private final UserRepositoryPort userRepositoryPort;

    /**
     * Gets the currently authenticated user's username
     */
    public Optional<String> getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return Optional.of(userDetails.getUsername());
        }
        return Optional.empty();
    }

    /**
     * Gets the currently authenticated user's domain User entity
     */
    public Optional<User> getCurrentUser() {
        return getCurrentUsername()
                .flatMap(userRepositoryPort::findByUsername);
    }

    /**
     * Gets the affiliate ID of the currently authenticated user (if they are an AFILIADO)
     */
    public Optional<String> getCurrentAffiliateId() {
        return getCurrentUser()
                .map(User::getAffiliateId)
                .filter(id -> id != null && !id.isEmpty());
    }
}

