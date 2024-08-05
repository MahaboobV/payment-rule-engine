package org.payment.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RuleViolation {
    private String rule;
    private String description;
    private String suggestion;
}
