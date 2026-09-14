package com.example.lifecycle.features;

public class FastShippingProvider implements ShippingProvider {

    @Override
    public String providerName() {
        return "fast";
    }

    @Override
    public String createShipment(String orderId) {
        return "FAST-" + orderId;
    }
}
