package com.example.lifecycle.design;

import java.math.RoundingMode;

/** Adapter translates our port into a legacy vendor API. */
public final class LegacyFraudCheckAdapter implements FraudCheck {

    private final LegacyFraudClient legacyClient;

    public LegacyFraudCheckAdapter(LegacyFraudClient legacyClient) {
        this.legacyClient = legacyClient;
    }

    @Override
    public boolean isAllowed(PaymentCommand command) {
        int minorUnits = command.amount()
                .movePointRight(2)
                .setScale(0, RoundingMode.UNNECESSARY)
                .intValueExact();
        return legacyClient.check(command.orderId(), minorUnits);
    }
}
