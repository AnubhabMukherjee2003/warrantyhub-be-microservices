package com.warrantyhub.productWarrantyPurchaseService.exception;

public class PurchaseNotFoundException extends RuntimeException {

    public PurchaseNotFoundException(Long id) {
        super("Purchase not found with id: " + id);
    }
}