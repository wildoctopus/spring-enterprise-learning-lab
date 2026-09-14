package com.example.lifecycle.design;

/** Simulates a vendor SDK whose API cannot be changed by our application. */
public final class LegacyFraudClient {

    public boolean check(String customerReference, int amountInMinorUnits) {
        return amountInMinorUnits < 1_000_000;
    }
}
