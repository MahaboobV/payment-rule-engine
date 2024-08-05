package org.payment.repository;

import org.payment.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, String> {

    @Query("SELECT count(pt) > 0 FROM PaymentTransaction pt WHERE pt.dsEnabled = true AND pt.transactionDate >= :oneYearAgo AND pt.customerId = :customerId")
    Boolean exist3DSEnabledTransactions(@Param("customerId")String customerId, @Param("oneYearAgo")LocalDateTime oneYearAgo);

    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.paymentCardNetwork = :cardType AND pt.currency = :currency AND (pt.transactionDate BETWEEN :transactionDateStart AND :transactionDateEnd)")
    List<PaymentTransaction> findTransactionsforDay(@Param("cardType") String cardType, @Param("currency") String currency, @Param("transactionDateStart") LocalDateTime transactionDateStart, @Param("transactionDateEnd") LocalDateTime transactionDateEnd);

    @Query("SELECT count(pt) > 0 FROM PaymentTransaction pt WHERE pt.customerId = :customerId")
    Boolean customerExist(@Param("customerId")String customerId);
}
