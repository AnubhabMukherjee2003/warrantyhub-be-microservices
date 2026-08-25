package com.warrantyhub.productWarrantyPurchaseService.repository;

import com.warrantyhub.productWarrantyPurchaseService.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface CompanyRepository
        extends JpaRepository<Company, Long> {
          Optional<Company> findByEmail(String email);      
}