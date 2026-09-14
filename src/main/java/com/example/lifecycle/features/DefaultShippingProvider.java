package com.example.lifecycle.features;

public class DefaultShippingProvider implements ShippingProvider {

    @Override
    public String providerName() {
        return "default";
    }

    @Override
    public String createShipment(String orderId) {
        return "MANUAL-" + orderId;
    }
}
