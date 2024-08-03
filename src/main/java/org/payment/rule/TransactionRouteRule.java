package org.payment.rule;

import org.payment.action.Action;
import org.payment.model.PaymentTransaction;

public class TransactionRouteRule implements Rule{

    private String currency;
    private String paymentNetwork;
    private Action action;

    public TransactionRouteRule(String currency, String paymentNetwork, Action action) {
        this.currency = currency;
        this.paymentNetwork = paymentNetwork;
        this.action = action;
    }

    @Override
    public boolean evaluate(PaymentTransaction paymentTransaction) {

        return paymentTransaction.getCurrency().equals(currency) && paymentTransaction.getCardType().equals(paymentNetwork);
    }

    @Override
    public Action getAction() {
        return action;
    }
}
