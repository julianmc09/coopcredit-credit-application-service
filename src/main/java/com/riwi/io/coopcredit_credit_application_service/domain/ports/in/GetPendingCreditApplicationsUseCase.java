package com.riwi.io.coopcredit_credit_application_service.domain.ports.in;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.CreditApplication;

import java.util.List;

public interface GetPendingCreditApplicationsUseCase {
    List<CreditApplication> getPendingCreditApplications();
}
