package org.payment.action;

import org.payment.model.PaymentTransactionDTO;

public class AdditionalFeeAction implements Action {
    private final String messageTemplate;
    private final double thresholdAmount;

    public AdditionalFeeAction(String messageTemplate, double thresholdAmount) {
        this.messageTemplate = messageTemplate;
        this.thresholdAmount = thresholdAmount;
    }

    @Override
    public String execute(PaymentTransactionDTO transaction) {
        String message = null;
        if (isConditionMet(transaction)) {
            message = messageTemplate.replace("{amount}", String.valueOf(transaction.getAmount()));
            System.out.println(message);
            double additionalFee = (double)2/100;
            transaction.setAmount(transaction.getAmount() * additionalFee);
            return message;
        }
        return message;
    }

    private boolean isConditionMet(PaymentTransactionDTO transaction) {
        return transaction.getAmount() <= thresholdAmount;
    }

    @Override
    public String getMessageTemplate() {
        return messageTemplate;
    }
}
