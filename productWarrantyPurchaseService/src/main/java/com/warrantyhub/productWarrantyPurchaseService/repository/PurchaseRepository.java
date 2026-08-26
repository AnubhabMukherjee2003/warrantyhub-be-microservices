package com.warrantyhub.productWarrantyPurchaseService.repository;

import com.warrantyhub.productWarrantyPurchaseService.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PurchaseRepository
        extends JpaRepository<Purchase, Long> {
    List<Purchase> findByCustomerEmail(String email);
}
