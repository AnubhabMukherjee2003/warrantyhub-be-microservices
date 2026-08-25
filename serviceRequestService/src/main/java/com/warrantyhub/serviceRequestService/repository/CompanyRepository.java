package com.warrantyhub.serviceRequestService.repository;

import com.warrantyhub.serviceRequestService.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface CompanyRepository
        extends JpaRepository<Company, Long> {
          Optional<Company> findByEmail(String email);      
}