package org.payment.rule;

import org.payment.action.Action;
import org.payment.action.ErrorAction;
import org.payment.model.PaymentTransactionDTO;

public class TransactionRouteRule implements Rule{

    private final String currency;
    private final String paymentNetwork;
    private final Action action;

    public TransactionRouteRule(String currency, String paymentNetwork, Action action) {
        this.currency = currency;
        this.paymentNetwork = paymentNetwork;
        this.action = action;
    }

    @Override
    public boolean evaluate(PaymentTransactionDTO paymentTransaction) {

        return paymentTransaction.getCurrency().equals(currency) && paymentTransaction.getCardType().equals(paymentNetwork);
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
