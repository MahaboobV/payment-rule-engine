package org.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PaymentRulesTable")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRules {

    @Id
    private String ruleId;

    @Lob
    private String ruleData;

}
