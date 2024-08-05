package org.payment.service;

import org.payment.entity.PaymentTransaction;
import org.payment.model.PaymentTransactionDTO;
import org.payment.repository.PaymentTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentTransactionService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentTransactionService.class);

    private final PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    public PaymentTransactionService(PaymentTransactionRepository paymentTransactionRepository) {
        this.paymentTransactionRepository = paymentTransactionRepository;
    }

    /**
     *
     * @param transaction
     * @return
     */
    public String storePaymentTransaction(PaymentTransactionDTO transaction) {
        try {
            if (null != transaction) {
                PaymentTransaction payementTransaction = mapToPaymentTransaction(transaction);
                paymentTransactionRepository.save(payementTransaction);
            }
        } catch (Exception ex) {
            logger.error("Exception while saving transaction: " + ex.getMessage());
        }
        return "Payment Transaction Saved !";
    }

    /**
     *
     * @param transaction
     * @return
     */
    private PaymentTransaction mapToPaymentTransaction(PaymentTransactionDTO transaction) {
        PaymentTransaction payementTransaction = new PaymentTransaction();
        payementTransaction.setCustomerId(transaction.getCustomerId());
        payementTransaction.setCustomerType(transaction.getCustomerType());
        payementTransaction.setCardType(transaction.getCardType());
        payementTransaction.setAmount(transaction.getAmount());
        payementTransaction.setLocation(transaction.getLocation());
        payementTransaction.setCurrency(transaction.getCurrency());
        payementTransaction.setPaymentMethod(transaction.getPaymentMethod());
        payementTransaction.setPaymentCardNetwork(transaction.getPaymentCardNetwork());
        payementTransaction.setDsEnabled(true);

        return payementTransaction;
    }
}
