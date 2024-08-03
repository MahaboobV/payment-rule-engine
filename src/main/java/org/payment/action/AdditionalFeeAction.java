package org.payment.action;

import org.payment.model.PaymentTransaction;

public class AdditionalFeeAction implements Action{
    private String messageTemplate;
    private double thresholdAmount;

    public AdditionalFeeAction(String messageTemplate, double thresholdAmount) {
        this.messageTemplate = messageTemplate;
        this.thresholdAmount = thresholdAmount;
    }

    @Override
    public void execute(PaymentTransaction transaction) {
        if(isConditionMet(transaction)) {
            String message = messageTemplate.replace("{amount}", String.valueOf(transaction.getAmount()));
            System.out.println(message);
        }
    }

    private boolean isConditionMet(PaymentTransaction transaction) {
        return transaction.getAmount() <= thresholdAmount;
    }

    @Override
    public String getMessageTemplate() {
        return messageTemplate;
    }
}
