package com.example.lifecycle.design;

import java.util.List;

/**
 * Application service: coordinates a use case while depending on abstractions.
 * It does not know vendor SDKs, concrete gateways, or notification details.
 */
public final class EnterprisePaymentService {

    private final PaymentGatewayFactory gatewayFactory;
    private final FraudCheck fraudCheck;
    private final List<PaymentAuthorizedListener> listeners;

    public EnterprisePaymentService(PaymentGatewayFactory gatewayFactory, FraudCheck fraudCheck,
            List<PaymentAuthorizedListener> listeners) {
        this.gatewayFactory = gatewayFactory;
        this.fraudCheck = fraudCheck;
        this.listeners = List.copyOf(listeners);
    }

    public PaymentReceipt authorize(PaymentCommand command, String paymentMethod) {
        if (!fraudCheck.isAllowed(command)) {
            throw new IllegalStateException("Payment rejected by fraud check");
        }

        PaymentReceipt receipt = gatewayFactory.forMethod(paymentMethod).charge(command);
        listeners.forEach(listener -> listener.onPaymentCompleted(receipt));
        return receipt;
    }
}
