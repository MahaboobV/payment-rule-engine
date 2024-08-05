package org.payment.exception;

import org.payment.model.PaymentTransactionErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PaymentRuleEngineExceptionHandler {

    @ExceptionHandler(PaymentRuleViolationException.class)
    public ResponseEntity<PaymentTransactionErrorResponseDTO> handlePaymentRuleViolationExcpetion(PaymentRuleViolationException paymentRuleViolationException) {
        return new ResponseEntity<>(paymentRuleViolationException.getErrorResponseDTO(), HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
