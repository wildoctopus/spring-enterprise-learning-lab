package com.example.lifecycle.design;

/** Strategy implementation: bank-transfer-specific payment behavior. */
public final class BankTransferGateway implements PaymentGateway {

    @Override
    public PaymentReceipt charge(PaymentCommand command) {
        return new PaymentReceipt(command.orderId(), "bank-transfer", "PENDING");
    }
}
