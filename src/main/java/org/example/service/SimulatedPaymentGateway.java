package org.example.service;

import org.example.model.Money;

/**
 * Fake gateway for the CLI. The next result can be chosen to test the
 * success, failure and wrong-amount scenarios.
 */
public class SimulatedPaymentGateway implements PaymentGateway {

    public enum Outcome {
        SUCCESS,
        FAILURE,
        WRONG_AMOUNT
    }

    private static final long WRONG_AMOUNT_DIFFERENCE = 1_000;

    private Outcome nextOutcome = Outcome.SUCCESS;
    private int referenceCounter;

    public void setNextOutcome(Outcome outcome) {
        this.nextOutcome = outcome;
    }

    @Override
    public GatewayResult charge(String orderId, Money amount) {
        String reference = "GW-" + (++referenceCounter);
        return switch (nextOutcome) {
            case SUCCESS -> new GatewayResult(true, amount, reference);
            case FAILURE -> new GatewayResult(false, Money.ZERO, reference);
            case WRONG_AMOUNT -> new GatewayResult(true,
                    new Money(Math.max(0, amount.amount() - WRONG_AMOUNT_DIFFERENCE)), reference);
        };
    }
}
