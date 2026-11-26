package org.example.service;

import org.example.model.Money;

import java.util.List;

public record CartSummary(List<CartLine> lines, Money total) {

    public boolean isEmpty() {
        return lines.isEmpty();
    }
}
