package org.payment.rule;

import org.payment.action.Action;
import org.payment.model.PaymentTransactionDTO;

import java.util.List;

public class PaymentMethodRule implements Rule{
    private final String location;
    private List<String> paymentMethods;
    private final Action action;

    public PaymentMethodRule(String location, List<String> paymentMethods, Action action) {
        this.location = location;
        this.paymentMethods = paymentMethods;
        this.action = action;
    }

    @Override
    public boolean evaluate(PaymentTransactionDTO paymentTransaction) {
        return paymentTransaction.getLocation().equals(location) &&
                paymentMethods.contains(paymentTransaction.getPaymentMethod());
    }

    @Override
    public Action getAction() {
        return action;
    }
}
