package org.payment.exception;

import org.payment.model.PaymentTransactionErrorResponseDTO;

public class PaymentRuleViolationException extends RuntimeException{
    private final PaymentTransactionErrorResponseDTO errorResponseDTO;

    public PaymentRuleViolationException(PaymentTransactionErrorResponseDTO paymentTransactionErrorResponseDTO) {
        super(paymentTransactionErrorResponseDTO.getErroMessage());
        this.errorResponseDTO = paymentTransactionErrorResponseDTO;
    }

    public PaymentTransactionErrorResponseDTO getErrorResponseDTO() {
        return errorResponseDTO;
    }
}
