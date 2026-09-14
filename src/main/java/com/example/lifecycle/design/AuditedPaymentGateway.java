package com.example.lifecycle.design;

import java.util.function.Consumer;

/** Decorator adds audit behavior without changing every gateway strategy. */
public final class AuditedPaymentGateway implements PaymentGateway {

    private final PaymentGateway delegate;
    private final Consumer<PaymentReceipt> auditSink;

    public AuditedPaymentGateway(PaymentGateway delegate, Consumer<PaymentReceipt> auditSink) {
        this.delegate = delegate;
        this.auditSink = auditSink;
    }

    @Override
    public PaymentReceipt charge(PaymentCommand command) {
        PaymentReceipt receipt = delegate.charge(command);
        auditSink.accept(receipt);
        return receipt;
    }
}
