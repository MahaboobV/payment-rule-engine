package org.payment.action;

import org.payment.model.PaymentTransaction;

public class RoutingAction implements Action {
    private String messageTemplate;

    private String currency;

    public RoutingAction(String messageTemplate, String currency) {

        this.messageTemplate = messageTemplate;
        this.currency = currency;
    }

    @Override
    public void execute(PaymentTransaction transaction) {
        if (isConditionMet(transaction)) {
            String message = messageTemplate;
            System.out.println("Routing transacation as :" + message);
        }
    }

    public boolean isConditionMet(PaymentTransaction transaction) {
        return transaction.getCurrency().equals(currency);
    }

    @Override
    public String getMessageTemplate() {
        return messageTemplate;
    }
}
