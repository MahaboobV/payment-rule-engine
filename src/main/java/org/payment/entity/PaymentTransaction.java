package org.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Payment_Transaction")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column
    private String customerId;

    @Column
    private String customerType;

    @Column
    private String cardType;

    @Column
    private String location;

    @Column
    private double amount;

    @Column
    private String paymentMethod;

    @Column
    private String currency;

    @Column
    private String paymentCardNetwork;

    @Column
    private boolean dsEnabled;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime transactionDate;

    @PrePersist
    protected void onCreate() {
        transactionDate = LocalDateTime.now();
    }

}
