package com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.adapters;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.User;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.UserRepositoryPort;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.entities.RoleEntity;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.entities.UserEntity;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.mappers.UserMapper;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.repositories.RoleJpaRepository;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.repositories.UserJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class UserJpaAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;
    private final RoleJpaRepository roleJpaRepository; // To find RoleEntity by Role enum
    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        // Ensure the RoleEntity exists before saving UserEntity
        RoleEntity roleEntity = roleJpaRepository.findByName(user.getRole())
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + user.getRole()));

        UserEntity userEntity = userMapper.toEntity(user);
        userEntity.setRole(roleEntity); // Set the managed RoleEntity

        return userMapper.toDomain(userJpaRepository.save(userEntity));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username).map(userMapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        userJpaRepository.deleteById(id);
    }
}
