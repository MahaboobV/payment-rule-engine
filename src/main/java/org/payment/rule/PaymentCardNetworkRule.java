package org.payment.rule;

import org.payment.action.Action;
import org.payment.model.PaymentTransaction;

public class PaymentCardNetworkRule implements Rule{

    private Action action;
    private String paymentCardNetwork;

    public PaymentCardNetworkRule(String paymentCardNetwork, Action action) {
        this.paymentCardNetwork = paymentCardNetwork;
        this.action = action;
    }

    @Override
    public boolean evaluate(PaymentTransaction paymentTransaction) {
        return paymentTransaction.getPaymentCardNetwork().equals(paymentCardNetwork);
    }

    @Override
    public Action getAction() {
        return action;
    }
}
