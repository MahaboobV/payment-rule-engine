package org.payment.rule;

import org.payment.action.Action;
import org.payment.model.PaymentTransactionDTO;

public class TransactionAmountRule implements Rule{

    private final double minAmount;
    private final double maxAmount;
    private final Action action;

    public TransactionAmountRule(double minAmount, double maxAmount, Action action) {
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.action = action;
    }

    @Override
    public boolean evaluate(PaymentTransactionDTO paymentTransaction) {
        double transactionAmount = paymentTransaction.getAmount();
        return transactionAmount >= minAmount && transactionAmount <= maxAmount;
    }

    @Override
    public Action getAction() {
        return action;
    }
}
