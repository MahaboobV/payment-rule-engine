package org.payment.model;

import lombok.Data;

import java.util.List;

@Data
public class ErrorDetails {
    private String code;
    private String message;
    private List<RuleViolation> details;
}
