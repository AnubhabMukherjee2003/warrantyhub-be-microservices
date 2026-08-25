package com.warrantyhub.productWarrantyPurchaseService.exception;

public class ServiceRequestNotFoundException extends RuntimeException {

    public ServiceRequestNotFoundException(Long id) {
        super("Service request not found with id: " + id);
    }
}