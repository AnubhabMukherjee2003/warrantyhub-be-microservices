package com.warrantyhub.customerCompanyService.exception;

public class PurchaseNotFoundException extends RuntimeException {

    public PurchaseNotFoundException(Long id) {
        super("Purchase not found with id: " + id);
    }
}