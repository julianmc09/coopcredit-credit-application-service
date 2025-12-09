package com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.dto.response;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String id;
    private String username;
    private Role role;
}
