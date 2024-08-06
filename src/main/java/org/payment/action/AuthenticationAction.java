package org.payment.action;

import org.payment.model.PaymentTransactionDTO;

public class AuthenticationAction implements Action {

    private final String messageTemplate;

    public AuthenticationAction(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    @Override
    public String execute(PaymentTransactionDTO transaction) {
        String message = messageTemplate.replace("{dsEnabled}", String.valueOf(true));
        System.out.println("Authentication not required :"+message);
        transaction.setDsAuthenticationRequired(false);
        return "Authentication not required :" + message;

    }

    @Override
    public String getMessageTemplate() {
        return messageTemplate;
    }
}
