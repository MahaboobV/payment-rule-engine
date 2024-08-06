package org.payment.rule;

import org.payment.action.Action;
import org.payment.action.ErrorAction;
import org.payment.model.PaymentTransactionDTO;

import java.util.List;

public class PaymentMethodRule implements Rule{
    private final String location;
    private List<String> paymentMethods;
    private final Action action;
    private final ErrorAction errorAction;

    public PaymentMethodRule(String location, List<String> paymentMethods, Action action, ErrorAction errorAction) {
        this.location = location;
        this.paymentMethods = paymentMethods;
        this.action = action;
        this.errorAction = errorAction;
    }

    @Override
    public boolean evaluate(PaymentTransactionDTO paymentTransaction) {
        return paymentTransaction.getLocation().equals(location);
    }

    @Override
    public Action getAction() {
        return action;
    }

    @Override
    public ErrorAction getErrorAction() {
        return errorAction;
    }
}
