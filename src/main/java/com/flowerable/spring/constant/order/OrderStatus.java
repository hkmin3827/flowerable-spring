package com.flowerable.spring.constant.order;

public enum OrderStatus {

    CREATED(false),
    REQUESTED(true),
    ACCEPTED(true),
    READY(true),
    COMPLETED(true),
    CANCELED(false);

    private final boolean paid;

    OrderStatus(boolean paid) {
        this.paid = paid;
    }

    public boolean isPaid() {
        return paid;
    }
}