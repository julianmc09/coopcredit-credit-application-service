package com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.controller;

import com.riwi.io.coopcredit_credit_application_service.domain.ports.in.GetAffiliateUseCase;
import com.riwi.io.coopcredit_credit_application_service.domain.ports.in.RegisterAffiliateUseCase;
import com.riwi.io.coopcredit_credit_application_service.domain.ports.in.UpdateAffiliateUseCase;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.dto.request.AffiliateRequest;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.dto.response.AffiliateResponse;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.mapper.AffiliateRestMapper;
import io.swagger.v3.oas.annotations.security.SecurityRequirement; // Import SecurityRequirement
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/affiliates")
@AllArgsConstructor
@SecurityRequirement(name = "Bearer Authentication") // Apply SecurityRequirement here
public class AffiliateController {

    private final RegisterAffiliateUseCase registerAffiliateUseCase;
    private final UpdateAffiliateUseCase updateAffiliateUseCase;
    private final GetAffiliateUseCase getAffiliateUseCase;
    private final AffiliateRestMapper affiliateRestMapper;

    @PostMapping
    public ResponseEntity<AffiliateResponse> registerAffiliate(@Valid @RequestBody AffiliateRequest request) {
        var affiliate = registerAffiliateUseCase.registerAffiliate(
                request.getDocument(),
                request.getName(),
                request.getSalary(),
                request.getAffiliationDate()
        );
        return ResponseEntity.created(URI.create("/affiliates/" + affiliate.getId()))
                .body(affiliateRestMapper.toResponse(affiliate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AffiliateResponse> updateAffiliate(@PathVariable String id, @Valid @RequestBody AffiliateRequest request) {
        return updateAffiliateUseCase.updateAffiliate(
                        id,
                        request.getName(),
                        request.getSalary(),
                        request.getAffiliationDate(),
                        request.getStatus()
                )
                .map(affiliateRestMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AffiliateResponse> getAffiliateById(@PathVariable String id) {
        return getAffiliateUseCase.getAffiliateById(id)
                .map(affiliateRestMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/document/{document}")
    public ResponseEntity<AffiliateResponse> getAffiliateByDocument(@PathVariable String document) {
        return getAffiliateUseCase.getAffiliateByDocument(document)
                .map(affiliateRestMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<AffiliateResponse>> getAllAffiliates() {
        List<AffiliateResponse> affiliates = affiliateRestMapper.toResponseList(getAffiliateUseCase.getAllAffiliates());
        return ResponseEntity.ok(affiliates);
    }
}
