package com.example.lifecycle.design;

import java.math.BigDecimal;
import java.util.Objects;

/** Immutable command built at the application boundary. */
public record PaymentCommand(String orderId, BigDecimal amount, String currency) {

    public PaymentCommand {
        Objects.requireNonNull(orderId, "orderId");
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(currency, "currency");
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String orderId;
        private BigDecimal amount;
        private String currency;

        public Builder orderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public PaymentCommand build() {
            return new PaymentCommand(orderId, amount, currency);
        }
    }
}
