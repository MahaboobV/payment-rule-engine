package org.payment.action;

import org.payment.model.PaymentTransaction;

public class PaymentMethodAction implements Action{

    private String messageTemplate;

    public PaymentMethodAction(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    @Override
    public void execute(PaymentTransaction transaction) {
       if(isConditionMet(transaction)) {
            String message = messageTemplate.replace("{location}", transaction.getLocation());
            System.out.println(message);
        }
    }

    @Override
    public String getMessageTemplate() {
        return null;
    }

    public boolean isConditionMet(PaymentTransaction transaction) {
        return true;
    }


}
