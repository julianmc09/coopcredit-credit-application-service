package com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.entities;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Assuming auto-increment for role ID
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private Role name;
}
