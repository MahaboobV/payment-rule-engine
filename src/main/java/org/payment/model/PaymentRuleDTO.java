package org.payment.model;

import lombok.*;

import java.util.Map;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRuleDTO {
    private String ruleId;

    private Map<String, String> ruleData;
}
