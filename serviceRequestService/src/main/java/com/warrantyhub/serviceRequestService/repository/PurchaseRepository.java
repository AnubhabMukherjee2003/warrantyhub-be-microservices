package com.warrantyhub.serviceRequestService.repository;

import com.warrantyhub.serviceRequestService.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PurchaseRepository
        extends JpaRepository<Purchase, Long> {
    List<Purchase> findByCustomer_Email(String email);
}