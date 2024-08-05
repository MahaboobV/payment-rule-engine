package org.payment.model;

import lombok.*;

import java.util.List;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentTransactionResponseDTO {
    private String customerId;

    private List<String> rulesApplicable;

    private double amount;

    private String additionalInfo;

    private String location;

    private String paymentMethod;

    private String customerType;

    private String currency;
}
