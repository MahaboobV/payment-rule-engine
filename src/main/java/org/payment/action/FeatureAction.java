package org.payment.action;

import org.payment.model.PaymentTransaction;

public class FeatureAction implements Action{

    private String messageTemplate;

    public FeatureAction(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    @Override
    public void execute(PaymentTransaction transaction) {
        System.out.println(transaction.getCustomerType() + messageTemplate);
    }

    @Override
    public String getMessageTemplate() {
        return messageTemplate;
    }
}
