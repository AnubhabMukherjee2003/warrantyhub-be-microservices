package com.warrantyhub.customerCompanyService.service;

import com.warrantyhub.customerCompanyService.dto.CompanyRequest;
import com.warrantyhub.customerCompanyService.dto.CompanyResponse;
import com.warrantyhub.customerCompanyService.model.Company;
import com.warrantyhub.customerCompanyService.repository.CompanyRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    public CompanyService(CompanyRepository companyRepository, PasswordEncoder passwordEncoder) {
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public CompanyResponse createCompany(CompanyRequest request) {
        if (companyRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use.");
        }
        Company company = new Company();
        company.setCompanyName(request.getCompanyName());
        company.setEmail(request.getEmail());
        company.setPassword(passwordEncoder.encode(request.getPassword()));
        company.setPhone(request.getPhone());
        company.setStatus(request.getStatus());

        Company savedCompany = companyRepository.save(company);
        return toResponse(savedCompany);
    }

    public CompanyResponse getCompanyByEmail(String email) {
        Company company = companyRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Company not found with email: " + email));
        return toResponse(company);
    }

    public CompanyResponse getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found with id: " + id));
        return toResponse(company);
    }

    private CompanyResponse toResponse(Company company) {
        return new CompanyResponse(
                company.getCompanyId(),
                company.getCompanyName(),
                company.getEmail(),
                company.getPhone(),
                company.getStatus()
        );
    }
}
