package com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.adapters;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.Role;
import com.riwi.io.coopcredit_credit_application_service.domain.entities.User;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(
        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {UserJpaAdapter.class}),
        properties = {
                "spring.jpa.hibernate.ddl-auto=validate",
                "spring.flyway.enabled=true",
                "spring.flyway.locations=classpath:db/migration"
        }
)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserJpaAdapterIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserRepositoryPort userRepositoryPort;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Ensure roles are present for user creation
        // In a real scenario, Flyway should handle initial roles, but for isolated tests,
        // we might need to ensure they exist or mock the role repository if not testing role persistence directly.
        // For now, we assume Flyway has run and roles exist.

        // Clean up before each test to ensure isolation
        userRepositoryPort.deleteAll();

        testUser = User.builder()
                .id(UUID.randomUUID().toString())
                .username("testuser-" + UUID.randomUUID())
                .password("encodedpassword")
                .role(Role.ROLE_AFILIADO)
                .build();
    }

    @Test
    @DisplayName("Should save a new user to the database")
    void shouldSaveNewUser() {
        User savedUser = userRepositoryPort.save(testUser);

        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals(testUser.getUsername(), savedUser.getUsername());
        assertEquals(testUser.getRole(), savedUser.getRole());

        Optional<User> foundUser = userRepositoryPort.findByUsername(testUser.getUsername());
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
    }

    @Test
    @DisplayName("Should find a user by username")
    void shouldFindUserByUsername() {
        userRepositoryPort.save(testUser);

        Optional<User> foundUser = userRepositoryPort.findByUsername(testUser.getUsername());

        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getUsername(), foundUser.get().getUsername());
    }

    @Test
    @DisplayName("Should return empty when user by username not found")
    void shouldReturnEmptyWhenUserByUsernameNotFound() {
        Optional<User> foundUser = userRepositoryPort.findByUsername("nonexistentuser");
        assertFalse(foundUser.isPresent());
    }

    @Test
    @DisplayName("Should delete a user by ID")
    void shouldDeleteUserById() {
        User savedUser = userRepositoryPort.save(testUser);
        assertNotNull(savedUser.getId());

        userRepositoryPort.deleteById(savedUser.getId());

        Optional<User> foundUser = userRepositoryPort.findByUsername(savedUser.getUsername());
        assertFalse(foundUser.isPresent());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if role not found when saving user")
    void shouldThrowExceptionIfRoleNotFound() {
        User userWithInvalidRole = User.builder()
                .id(UUID.randomUUID().toString())
                .username("invalidroleuser-" + UUID.randomUUID())
                .password("password")
                .role(Role.valueOf("NON_EXISTENT_ROLE")) // Use a role that doesn't exist in the DB
                .build();

        // Expect an IllegalArgumentException from UserJpaAdapter because the role won't be found
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userRepositoryPort.save(userWithInvalidRole)
        );

        assertTrue(exception.getMessage().contains("Role not found"));
    }
}
