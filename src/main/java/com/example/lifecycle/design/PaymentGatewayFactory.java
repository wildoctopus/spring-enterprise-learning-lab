package com.example.lifecycle.design;

import java.util.Map;

/** Factory centralizes strategy selection and keeps it out of business logic. */
public final class PaymentGatewayFactory {

    private final Map<String, PaymentGateway> gateways;

    public PaymentGatewayFactory(Map<String, PaymentGateway> gateways) {
        this.gateways = Map.copyOf(gateways);
    }

    public PaymentGateway forMethod(String paymentMethod) {
        PaymentGateway gateway = gateways.get(paymentMethod);
        if (gateway == null) {
            throw new IllegalArgumentException("Unsupported payment method: " + paymentMethod);
        }
        return gateway;
    }
}
