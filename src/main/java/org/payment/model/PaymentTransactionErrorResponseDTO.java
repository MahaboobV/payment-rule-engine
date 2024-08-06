package org.payment.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Setter
@Getter
public class PaymentTransactionErrorResponseDTO {

    private String status;
    private String erroMessage;
    private RuleViolation ruleViolation;
}
