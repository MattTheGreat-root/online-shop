package org.example.service;

import org.example.model.Money;

public record GatewayResult(boolean successful, Money paidAmount, String reference) {
}
