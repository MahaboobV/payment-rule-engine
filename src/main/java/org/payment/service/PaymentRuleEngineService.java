package org.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.payment.engine.PaymentRuleEngine;
import org.payment.model.PaymentTransactionDTO;
import org.payment.model.PaymentTransactionResponseDTO;
import org.payment.repository.PaymentRuleEngineRepository;
import org.payment.rule.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
public class PaymentRuleEngineService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentRuleEngineService.class);

    private final PaymentRuleEngine paymentRuleEngine;
    private final PaymentRuleEngineRepository paymentRuleEngineRepository;
    private final PaymentTransactionService transactionService;

    @Autowired
    public PaymentRuleEngineService(PaymentRuleEngine paymentRuleEngine, PaymentRuleEngineRepository paymentRuleEngineRepository, PaymentTransactionService transactionService){
        this.paymentRuleEngine = paymentRuleEngine;
        this.paymentRuleEngineRepository = paymentRuleEngineRepository;
        this.transactionService = transactionService;
    }

    /**
     *
     *
     * @param transaction
     * @return
     * @throws IOException
     */
    public PaymentTransactionResponseDTO evaluateTransaction(PaymentTransactionDTO transaction) throws IOException {

        List<Rule> rules = paymentRuleEngineRepository.getAllRules();
        List<String> rulesApplicable = paymentRuleEngine.evaluatePaymentTransaction(rules, transaction);

        PaymentTransactionResponseDTO response = transactionService.storePaymentTransaction(transaction);
        response.setRulesApplicable(rulesApplicable.stream().toList());
        return response;

    }

    /**
     *
     * @param transaction
     * @return
     * @throws IOException
     */
    public PaymentTransactionResponseDTO evaluateTransactionH2(PaymentTransactionDTO transaction) throws IOException {
        return paymentRuleEngine.evaluateTransactionH2(transaction);
    }

    /**
     *
     * @param rootNode
     * @return
     */
    @Transactional
    public String processRules(JsonNode rootNode) {
      return paymentRuleEngine.processRules(rootNode);
    }


}
