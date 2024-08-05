package org.payment.rule;

import org.payment.action.Action;
import org.payment.action.ErrorAction;
import org.payment.model.PaymentTransactionDTO;

public class NewFeatureRule implements Rule{
    private final String customerType;
    private final Action action;

    public NewFeatureRule(String customerType, Action action) {
        this.customerType = customerType;
        this.action = action;
    }
    @Override
    public boolean evaluate(PaymentTransactionDTO paymentTransaction) {
        return paymentTransaction.getCustomerType().equals(customerType);
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
