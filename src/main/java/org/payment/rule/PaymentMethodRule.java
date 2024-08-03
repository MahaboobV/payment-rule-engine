package org.payment.rule;

import org.payment.action.Action;
import org.payment.model.PaymentTransaction;

public class PaymentMethodRule implements Rule{
    private String location;
    private Action action;

    public PaymentMethodRule(String location, Action action) {
        this.location = location;
        this.action = action;
    }

    @Override
    public boolean evaluate(PaymentTransaction paymentTransaction) {
        return paymentTransaction.getLocation().equals(location);
    }

    @Override
    public Action getAction() {
        return action;
    }
}
