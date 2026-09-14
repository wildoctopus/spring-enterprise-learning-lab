package com.example.lifecycle.features;

public interface ShippingProvider {

    String providerName();

    String createShipment(String orderId);
}
