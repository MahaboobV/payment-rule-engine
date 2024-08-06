package org.payment.action;

import org.payment.model.PaymentTransactionDTO;

public class FeatureAction implements Action {

    private String messageTemplate;

    public FeatureAction(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    @Override
    public String execute(PaymentTransactionDTO transaction) {
        System.out.println(transaction.getCustomerType() + messageTemplate);
        transaction.setEmployeeFeatureEnabled(true);
        return transaction.getCustomerType() + messageTemplate;
    }

    @Override
    public String getMessageTemplate() {
        return messageTemplate;
    }
}
