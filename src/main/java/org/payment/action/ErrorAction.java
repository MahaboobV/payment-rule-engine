package org.payment.action;

import org.payment.model.PaymentTransactionDTO;

public class ErrorAction implements Action{

    private String messageTemplate;

    public ErrorAction(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    @Override
    public String execute(PaymentTransactionDTO transaction) {
        return messageTemplate.replace("{location}", transaction.getLocation());
    }

    @Override
    public String getMessageTemplate() {
        return messageTemplate;
    }
}
