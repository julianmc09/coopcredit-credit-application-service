package com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @Id
    private String id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER) // Eagerly fetch role as it's small and frequently needed
    @JoinColumn(name = "role_id", nullable = false)
    private RoleEntity role;
}
