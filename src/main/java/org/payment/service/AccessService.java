package org.payment.service;

import org.payment.model.PaymentTransactionDTO;
import org.payment.repository.PaymentTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccessService {
    private static final Logger logger = LoggerFactory.getLogger(AccessService.class);

    private PaymentTransactionRepository transactionRepository;

    @Autowired
    public AccessService(PaymentTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public boolean check3DSAccess(PaymentTransactionDTO paymentTransactionDTO) {
        if(transactionRepository.customerExist(paymentTransactionDTO.getCustomerId())) {
            LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
            return transactionRepository.exist3DSEnabledTransactions(paymentTransactionDTO.getCustomerId(), oneYearAgo);
        }else {
            return false;
        }
    }

    public void routeToAcquirer(List<PaymentTransactionDTO> transactions, String acquirerName) {
        for (PaymentTransactionDTO transaction : transactions) {
            logger.info("Routing transaction with ID:" + transaction.getTransactionId() + "Acquirer : " + acquirerName);
        }

    }
}
