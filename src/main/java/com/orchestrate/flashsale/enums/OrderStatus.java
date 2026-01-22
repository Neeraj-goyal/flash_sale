package com.orchestrate.flashsale.enums;


public enum OrderStatus {
    PENDING("Pending"),
    COMPLETED("Completed");

    private final String status;
    OrderStatus(String status) {
        this.status = status;
    }
}
