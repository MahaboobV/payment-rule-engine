package org.payment.rule;

import org.payment.action.Action;
import org.payment.model.PaymentTransaction;

public class TransactionAmountRule implements Rule{

    private double minAmount;
    private double maxAmount;
    private Action action;

    public TransactionAmountRule(double minAmount, double maxAmount, Action action) {
        this.minAmount = minAmount;

        this.maxAmount = maxAmount;
        this.action = action;
    }

    @Override
    public boolean evaluate(PaymentTransaction paymentTransaction) {
        double transactionAmount = paymentTransaction.getAmount();
        return transactionAmount >= minAmount && transactionAmount <= maxAmount;
    }

    @Override
    public Action getAction() {
        return action;
    }
}
