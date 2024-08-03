package org.payment.rule;

import org.payment.action.Action;
import org.payment.model.PaymentTransaction;

public class NewFeatureRule implements Rule{
    private String customerType;
    private Action action;

    public NewFeatureRule(String customerType, Action action) {
        this.customerType = customerType;
        this.action = action;
    }
    @Override
    public boolean evaluate(PaymentTransaction paymentTransaction) {
        return paymentTransaction.getCustomerType().equals(customerType);
    }

    @Override
    public Action getAction() {
        return action;
    }
}
