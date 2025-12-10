package com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.controller;

import com.riwi.io.coopcredit_credit_application_service.domain.ports.in.*;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.dto.request.CreditApplicationRequest;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.dto.response.CreditApplicationResponse;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.mapper.CreditApplicationRestMapper;
import io.swagger.v3.oas.annotations.security.SecurityRequirement; // Import SecurityRequirement
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Import PreAuthorize
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/credit-applications")
@AllArgsConstructor
@SecurityRequirement(name = "Bearer Authentication") // Apply SecurityRequirement here
public class CreditApplicationController {

    private final RegisterCreditApplicationUseCase registerCreditApplicationUseCase;
    private final EvaluateCreditApplicationUseCase evaluateCreditApplicationUseCase;
    private final GetCreditApplicationUseCase getCreditApplicationUseCase;
    private final GetAllCreditApplicationsUseCase getAllCreditApplicationsUseCase;
    private final GetPendingCreditApplicationsUseCase getPendingCreditApplicationsUseCase;
    private final CreditApplicationRestMapper creditApplicationRestMapper;

    @PreAuthorize("hasRole('AFILIADO')") // Only AFILIADO can register applications
    @PostMapping
    public ResponseEntity<CreditApplicationResponse> registerCreditApplication(@Valid @RequestBody CreditApplicationRequest request) {
        var creditApplication = registerCreditApplicationUseCase.registerCreditApplication(
                request.getAffiliateId(),
                request.getRequestedAmount(),
                request.getTerm(),
                request.getProposedRate()
        );
        return ResponseEntity.created(URI.create("/credit-applications/" + creditApplication.getId()))
                .body(creditApplicationRestMapper.toResponse(creditApplication));
    }

    @PreAuthorize("hasRole('ANALISTA')") // Only ANALISTA can evaluate applications
    @PostMapping("/{id}/evaluate")
    public ResponseEntity<CreditApplicationResponse> evaluateCreditApplication(@PathVariable String id) {
        var creditApplication = evaluateCreditApplicationUseCase.evaluateCreditApplication(id);
        return ResponseEntity.ok(creditApplicationRestMapper.toResponse(creditApplication));
    }

    @PreAuthorize("hasAnyRole('AFILIADO', 'ANALISTA', 'ADMIN')") // AFILIADO, ANALISTA, ADMIN can get application by ID
    @GetMapping("/{id}")
    public ResponseEntity<CreditApplicationResponse> getCreditApplicationById(@PathVariable String id) {
        return getCreditApplicationUseCase.getCreditApplicationById(id)
                .map(creditApplicationRestMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can get all applications
    @GetMapping
    public ResponseEntity<List<CreditApplicationResponse>> getAllCreditApplications() {
        List<CreditApplicationResponse> applications = creditApplicationRestMapper.toResponseList(getAllCreditApplicationsUseCase.getAllCreditApplications());
        return ResponseEntity.ok(applications);
    }

    @PreAuthorize("hasRole('ANALISTA')") // Only ANALISTA can get pending applications
    @GetMapping("/pending")
    public ResponseEntity<List<CreditApplicationResponse>> getPendingCreditApplications() {
        List<CreditApplicationResponse> applications = creditApplicationRestMapper.toResponseList(getPendingCreditApplicationsUseCase.getPendingCreditApplications());
        return ResponseEntity.ok(applications);
    }
}
