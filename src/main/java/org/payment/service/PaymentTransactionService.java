package org.payment.service;

import org.payment.engine.PaymentRuleEngine;
import org.payment.entity.PaymentTransaction;
import org.payment.model.PaymentTransactionDTO;
import org.payment.model.PaymentTransactionResponseDTO;
import org.payment.repository.PaymentTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentTransactionService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentTransactionService.class);

    private final PaymentTransactionRepository paymentTransactionRepository;

    private final PaymentRuleEngine paymentRuleEngine;

    @Autowired
    public PaymentTransactionService(PaymentTransactionRepository paymentTransactionRepository, PaymentRuleEngine paymentRuleEngine) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.paymentRuleEngine = paymentRuleEngine;
    }

    /**
     *
     * @param transaction
     * @return
     */
    public PaymentTransactionResponseDTO storePaymentTransaction(PaymentTransactionDTO transaction) {
        PaymentTransactionResponseDTO responseDTO = new PaymentTransactionResponseDTO();
        try {
            if (null != transaction) {
                List<String> rulesApplicable = paymentRuleEngine.evaluatePaymentTransaction(transaction);
                PaymentTransaction payementTransaction = mapToPaymentTransaction(transaction);
                paymentTransactionRepository.save(payementTransaction);

                responseDTO.setCustomerId(transaction.getCustomerId());
                responseDTO.setAmount(transaction.getAmount());
                responseDTO.setCurrency(transaction.getCurrency());
                responseDTO.setRulesApplicable(rulesApplicable);
                responseDTO.setAdditionalInfo("Payment transaction saved!");
            }
        } catch (Exception ex) {
            logger.error("Exception while saving transaction: " + ex.getMessage());
        }
        return responseDTO;
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
