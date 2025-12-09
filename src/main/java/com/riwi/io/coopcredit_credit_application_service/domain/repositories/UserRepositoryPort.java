package com.riwi.io.coopcredit_credit_application_service.domain.repositories;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.User;

import java.util.Optional;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findByUsername(String username);

    void deleteById(String id); // Added this method
}
