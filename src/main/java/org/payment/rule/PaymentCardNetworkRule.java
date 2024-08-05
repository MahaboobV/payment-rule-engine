package org.payment.rule;

import org.payment.action.Action;
import org.payment.action.ErrorAction;
import org.payment.model.PaymentTransactionDTO;

public class PaymentCardNetworkRule implements Rule{

    private final Action action;
    private final String paymentCardNetwork;

    public PaymentCardNetworkRule(String paymentCardNetwork, Action action) {
        this.paymentCardNetwork = paymentCardNetwork;
        this.action = action;
    }

    @Override
    public boolean evaluate(PaymentTransactionDTO paymentTransaction) {
        return paymentTransaction.getPaymentCardNetwork().equals(paymentCardNetwork);
    }

    @Override
    public Action getAction() {
        return action;
    }

    @Override
    public ErrorAction getErrorAction() {
        return null;
    }
}
