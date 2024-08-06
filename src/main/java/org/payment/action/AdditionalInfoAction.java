package org.payment.action;

import org.payment.exception.PaymentRuleViolationException;
import org.payment.model.PaymentTransactionDTO;
import org.payment.model.PaymentTransactionErrorResponseDTO;

import java.util.List;

public class AdditionalInfoAction implements Action {

    private final double thresholdAmount;

    private final String messageTemplate;

    private final List<String> riskCountries;


    public AdditionalInfoAction(double thresholdAmount, String messageTemplate, String criteria, List<String> riskCountries) {
        this.thresholdAmount = thresholdAmount;
        this.messageTemplate = messageTemplate;
        this.riskCountries = riskCountries;
    }


    @Override
    public String execute(PaymentTransactionDTO transaction) {
        String message = null;
        if (isConditionMet(transaction)) {
            message = messageTemplate.replace("{thresholdAmount}", String.valueOf(thresholdAmount));
            System.out.println(message);
            transaction.setAdditionalVerificationRequired(true);
            PaymentTransactionErrorResponseDTO errorResponseDTO = new PaymentTransactionErrorResponseDTO();
            errorResponseDTO.setErroMessage(message);
            throw new PaymentRuleViolationException(errorResponseDTO);
        }
        return message;
    }

    @Override
    public String getMessageTemplate() {
        return messageTemplate;
    }

    public boolean isConditionMet(PaymentTransactionDTO transaction) {
        return transaction.getAmount() > thresholdAmount && riskCountries.contains(transaction.getLocation());
    }


}
