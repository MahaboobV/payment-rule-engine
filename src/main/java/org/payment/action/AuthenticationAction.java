package org.payment.action;

import org.payment.model.PaymentTransaction;

public class AuthenticationAction implements Action {

    private String messageTemplate;
    private boolean dsEnabled;

    public AuthenticationAction(String messageTemplate, boolean dsEnabled) {
        this.messageTemplate = messageTemplate;
        this.dsEnabled = dsEnabled;
    }

    @Override
    public void execute(PaymentTransaction transaction) {
        String message = messageTemplate.replace("{dsEnabled}", Boolean.toString(dsEnabled));
        if(isConditionMet(transaction)) {
            System.out.println("Authentication not required :"+message);
        }else  {
            System.out.println("Authentication required :"+message);
        }
    }

    private boolean isConditionMet(PaymentTransaction transaction) {
        return transaction.isDsEnabled() == dsEnabled;
    }

    @Override
    public String getMessageTemplate() {
        return messageTemplate;
    }
}
