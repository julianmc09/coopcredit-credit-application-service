package com.riwi.io.coopcredit_credit_application_service.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String id;
    private String username;
    private String password;
    private Role role;
    private String affiliateId; // Link to affiliate for AFILIADO users
}
