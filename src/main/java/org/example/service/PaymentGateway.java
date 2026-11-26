package org.example.service;

import org.example.model.Money;

/**
 * The external payment gateway. The store only talks to it through this interface.
 */
public interface PaymentGateway {

    GatewayResult charge(String orderId, Money amount);
}
