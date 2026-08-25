package com.warrantyhub.customerCompanyService.exception;

public class ServiceRequestNotFoundException extends RuntimeException {

    public ServiceRequestNotFoundException(Long id) {
        super("Service request not found with id: " + id);
    }
}