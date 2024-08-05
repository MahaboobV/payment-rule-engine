package org.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.payment.action.Action;
import org.payment.entity.PaymentRules;
import org.payment.entity.PaymentTransaction;
import org.payment.loader.RuleLoader;
import org.payment.model.PaymentTransactionDTO;
import org.payment.repository.PaymentRulesRepository;
import org.payment.repository.PaymentTransactionRepository;
import org.payment.rule.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoutingService {

    private static final Logger logger = LoggerFactory.getLogger(RoutingService.class);

    private final PaymentTransactionRepository transactionRepository;

    private final PaymentRulesRepository rulesRepository;

    private final ObjectMapper objectMapper;

    private final RuleLoader ruleLoader;

    @Autowired
    public RoutingService(PaymentTransactionRepository transactionRepository, PaymentRulesRepository rulesRepository,
                          ObjectMapper objectMapper, RuleLoader ruleLoader) {
        this.transactionRepository = transactionRepository;
        this.rulesRepository = rulesRepository;
        this.objectMapper = objectMapper;
        this.ruleLoader = ruleLoader;
    }

    public String routeTransactions() throws JsonProcessingException {
        LocalDateTime starOftheDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        List<PaymentTransaction> transactions = transactionRepository.findTransactionsforDay("Mastercard", "SEK", starOftheDay, LocalDateTime.now());
        if(!transactions.isEmpty()) {
            logger.info("Transaction found : {}", transactions.size());
            List<PaymentRules> paymentRules = rulesRepository.findAll();
            if(!paymentRules.isEmpty()) {
                List<PaymentTransactionDTO> paymentTransactionDTOs = setPaymentTransactionsDTO(transactions);

                for (PaymentRules paymentRule : paymentRules) {
                    JsonNode ruleNode = objectMapper.readTree(paymentRule.getRuleData());
                    if (ruleNode.path("type").asText().equals("TransactionRouteRule") &&
                            ruleNode.path("paymentNetwork").asText().equals("Mastercard") &&
                            ruleNode.path("currency").asText().equals("SEK")) {
                        Rule rule = ruleLoader.parseRule(ruleNode);
                        Action action = rule.getAction();
                        action.execute(paymentTransactionDTOs);
                    }
                }
                return "Transaction Routing Completed for the date : " + LocalDateTime.now();
            }else {
                return "No Payment Rules found at : " + LocalDateTime.now();
            }
        }else {
            return "No Payment transactions found at : " + LocalDateTime.now();
        }
    }

    private List<PaymentTransactionDTO> setPaymentTransactionsDTO(List<PaymentTransaction> transactions) {
        List<PaymentTransactionDTO> transactionDTOS = new ArrayList<>();
        for (PaymentTransaction transaction : transactions) {
            PaymentTransactionDTO paymentTransactionDTO = new PaymentTransactionDTO();
            paymentTransactionDTO.setAmount(transaction.getAmount());
            paymentTransactionDTO.setCustomerId(transaction.getCustomerType());
            paymentTransactionDTO.setLocation(transaction.getLocation());
            paymentTransactionDTO.setPaymentMethod(transaction.getPaymentMethod());
            paymentTransactionDTO.setCustomerType(transaction.getCustomerType());
            paymentTransactionDTO.setDsEnabled(transaction.isDsEnabled());
            paymentTransactionDTO.setTransactionId(transaction.getId());
            transactionDTOS.add(paymentTransactionDTO);
        }
        return transactionDTOS;
    }


}
