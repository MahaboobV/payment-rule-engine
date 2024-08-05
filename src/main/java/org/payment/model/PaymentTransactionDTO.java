package org.payment.model;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentTransactionDTO {
    private String transactionId;

    private String customerId;

    private String customerType;

    private String cardType;

    private String location;

    private double amount;

    private String paymentMethod;

    private String currency;

    private String paymentCardNetwork;

    private boolean dsEnabled;


}
