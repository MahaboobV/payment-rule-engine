package org.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.payment.engine.PaymentRuleEngine;
import org.payment.entity.PaymentRules;
import org.payment.entity.PaymentTransaction;
import org.payment.loader.RuleLoader;
import org.payment.repository.PaymentRulesRepository;
import org.payment.repository.PaymentTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoutingService {

    private static final Logger logger = LoggerFactory.getLogger(RoutingService.class);

    private final PaymentTransactionRepository transactionRepository;

    private final PaymentRulesRepository rulesRepository;

    private final PaymentRuleEngine paymentRuleEngine;

    @Autowired
    public RoutingService(PaymentTransactionRepository transactionRepository, PaymentRulesRepository rulesRepository,
                          ObjectMapper objectMapper, RuleLoader ruleLoader, PaymentRuleEngine paymentRuleEngine) {
        this.transactionRepository = transactionRepository;
        this.rulesRepository = rulesRepository;
        this.paymentRuleEngine = paymentRuleEngine;
    }

    public String routeTransactions() {
        LocalDateTime starOftheDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        List<PaymentTransaction> transactions = transactionRepository.findTransactionsforDay("Mastercard", "SEK", starOftheDay, LocalDateTime.now());
        if(!transactions.isEmpty()) {
            logger.info("Transaction found : {}", transactions.size());
            List<PaymentRules> paymentRules = rulesRepository.findAll();
            if(! paymentRules.isEmpty()) {
                paymentRuleEngine.evaluatePaymentTransactionRoutingRules(paymentRules, transactions);
                return "Transaction Routing Completed for the date : " + LocalDateTime.now();
            }else {
                return "No Payment Rules found at : " + LocalDateTime.now();
            }
        }else {
            return "No Payment transactions found at : " + LocalDateTime.now();
        }
    }
}
