package org.example.service;

import org.example.model.Money;

public record CartLine(String productId, String productName, Money unitPrice, int quantity, Money subtotal) {
}
