package org.payment.action;

import org.payment.model.PaymentTransaction;

import java.util.List;

public class AdditionalInfoAction implements Action{

    private double thresholdAmount;

    private String messageTemplate;

    private String criteria;
    private List<String> riskCountries;


    public AdditionalInfoAction(double thresholdAmount, String messageTemplate, String criteria, List<String> riskCountries) {
        this.thresholdAmount = thresholdAmount;
        this.messageTemplate = messageTemplate;
        this.criteria = criteria;
        this.riskCountries = riskCountries;
    }


    @Override
    public void execute(PaymentTransaction transaction) {
        if(isConditionMet(transaction)) {
            String message = messageTemplate.replace("{thresholdAmount}", String.valueOf(thresholdAmount));
            System.out.println(message);
        }
    }

    @Override
    public String getMessageTemplate() {
        return messageTemplate;
    }

    public boolean isConditionMet(PaymentTransaction transaction) {
        return transaction.getAmount() > thresholdAmount && riskCountries.contains(transaction.getLocation());
    }


}
