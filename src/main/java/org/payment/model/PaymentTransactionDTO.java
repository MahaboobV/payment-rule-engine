package org.payment.model;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentTransactionDTO {
    private String transactionId;

    @NotEmpty
    @NotNull
    private String customerId;

    @NotEmpty
    @NotNull
    private String customerType;

    private String cardType;

    @NotEmpty
    @NotNull
    private String location;

    private double amount;

    @NotEmpty
    @NotNull
    private String paymentMethod;

    private String currency;

    private String paymentCardNetwork;

    private boolean dsAuthenticationRequired;

    private boolean additionalVerificationRequired;

    private boolean employeeFeatureEnabled;


}
