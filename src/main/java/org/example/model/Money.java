package org.example.model;

public record Money(long amount) {

    public static final Money ZERO = new Money(0);

    public Money {
        if (amount < 0) {
            throw new DomainException("Amount cannot be negative");
        }
    }

    public Money plus(Money other) {
        return new Money(amount + other.amount);
    }

    public Money times(int quantity) {
        if (quantity < 0) {
            throw new DomainException("Quantity cannot be negative");
        }
        return new Money(amount * quantity);
    }

    @Override
    public String toString() {
        return String.format("%,d Toman", amount);
    }
}
