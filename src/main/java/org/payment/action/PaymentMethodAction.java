package org.payment.action;

import org.payment.exception.PaymentRuleViolationException;
import org.payment.model.PaymentTransactionDTO;
import org.payment.model.PaymentTransactionErrorResponseDTO;

import java.util.List;

public class PaymentMethodAction implements Action {

    private final String messageTemplate;
    private final List<String> paymentMethods;

    public PaymentMethodAction(String messageTemplate, List<String> paymentMethods) {
        this.messageTemplate = messageTemplate;
        this.paymentMethods = paymentMethods;
    }

    @Override
    public String execute(PaymentTransactionDTO transaction) {
        String message = null;
        if (matchCondition(transaction)) {
            message = messageTemplate.replace("{location}", transaction.getLocation());
            message= message.replace("{paymentMethods}", paymentMethods.toString());
            System.out.println(message);
        }else {
            message = "Selected payment method :"+transaction.getPaymentMethod() +"is not supported for the country :"+transaction.getLocation();
            PaymentTransactionErrorResponseDTO errorResponseDTO= new PaymentTransactionErrorResponseDTO();
            errorResponseDTO.setErroMessage(message);
            throw new PaymentRuleViolationException(errorResponseDTO);
        }
        return message;
    }

    @Override
    public String getMessageTemplate() {
        return null;
    }

    public boolean matchCondition(PaymentTransactionDTO transaction) {

        return  paymentMethods.contains(transaction.getPaymentMethod());
    }


}
