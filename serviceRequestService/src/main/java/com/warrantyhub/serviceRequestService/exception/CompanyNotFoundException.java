package com.warrantyhub.serviceRequestService.exception;

public class CompanyNotFoundException extends RuntimeException {

    public CompanyNotFoundException(Long id) {
        super("Company not found with id: " + id);
    }
}