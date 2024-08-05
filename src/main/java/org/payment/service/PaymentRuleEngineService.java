package org.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.payment.action.Action;
import org.payment.entity.PaymentRules;
import org.payment.loader.RuleLoader;
import org.payment.model.PaymentTransactionDTO;
import org.payment.model.PaymentTransactionResponseDTO;
import org.payment.repository.PaymentRuleEngineRepository;
import org.payment.repository.PaymentRulesRepository;
import org.payment.repository.PaymentTransactionRepository;
import org.payment.rule.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentRuleEngineService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentRuleEngineService.class);

    private final PaymentRuleEngineRepository paymentRuleEngineRepository;

    private final ObjectMapper objectMapper;

    private final PaymentRulesRepository rulesRepository;

    private final RuleLoader ruleLoader;

    private final PaymentTransactionRepository transactionRepository;

    @Autowired
    public PaymentRuleEngineService(PaymentRuleEngineRepository paymentRuleEngineRepository, ObjectMapper objectMapper,
                                    PaymentRulesRepository rulesRepository, RuleLoader ruleLoader,
                                    PaymentTransactionRepository transactionRepository) {
        this.paymentRuleEngineRepository = paymentRuleEngineRepository;
        this.objectMapper = objectMapper;
        this.rulesRepository = rulesRepository;
        this.ruleLoader = ruleLoader;
        this.transactionRepository = transactionRepository;
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
        List<String> rulesApplicable = new ArrayList<>();

        for (Rule paymentRule : rules) {
            //Rule rule = objectMapper.readValue(paymentRule.getRuleData().toString(), Rule.class);
            if (paymentRule.evaluate(transaction)) {
                Action action = paymentRule.getAction();
                String message = action.execute(transaction);
                rulesApplicable.add(message);
            }
        }
        PaymentTransactionResponseDTO response = new PaymentTransactionResponseDTO();
        response.setAmount(transaction.getAmount());
        response.setCustomerId(transaction.getCustomerType());
        response.setLocation(transaction.getLocation());
        response.setDsEnabled(true);
        response.setRulesApplicable(rulesApplicable);
        return response;
    }

    /**
     *
     * @param transaction
     * @return
     * @throws IOException
     */
    public PaymentTransactionResponseDTO evaluateTransactionH2(PaymentTransactionDTO transaction) throws IOException {
        List<PaymentRules> PaymentRules = rulesRepository.findAll();
        List<String> rulesApplicable = new ArrayList<>();

        for (PaymentRules paymentRule : PaymentRules) {
            JsonNode ruleNode = objectMapper.readTree(paymentRule.getRuleData());
            Rule rule = ruleLoader.parseRule(ruleNode);
            if (rule.evaluate(transaction)) {
                Action action = rule.getAction();
                String message = action.execute(transaction);
                rulesApplicable.add(message);
            }
        }

        PaymentTransactionResponseDTO response = new PaymentTransactionResponseDTO();
        response.setAmount(transaction.getAmount());
        response.setCustomerId(transaction.getCustomerId());
        response.setCustomerType(transaction.getCustomerType());
        response.setLocation(transaction.getLocation());
        response.setRulesApplicable(rulesApplicable);
        return response;
    }

    /**
     *
     * @param rootNode
     * @return
     */
    @Transactional
    public String processRules(JsonNode rootNode) {
        try {
            if (null != rootNode) {
                logger.info("Parsing Json content.....");
                //Parse Rules from Json
                JsonNode rulesNode = rootNode.path("rules");
                for (JsonNode ruleNode : rulesNode) {
                    //Store in DynamoDB
                    //storeInDynamoDb(ruleNode);
                    storeInH2Db(ruleNode);
                }
                logger.info("Successfully processed and store Json file from S3");
            }
        } catch (Exception e) {
            logger.error("Error processing file from S3:" + e.getMessage());
        }
        return "Done";
    }

    private String storeInH2Db(JsonNode ruleNode) throws IOException {
        return ruleLoader.loadPaymentRules();
    }

    private void storeInDynamoDb(JsonNode ruleNode) {
        String ruleId = ruleNode.path("id").asText();
        String ruleData = ruleNode.toString();

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("RuleId", AttributeValue.builder().s(ruleId).build());
        item.put("RuleData", AttributeValue.builder().s(ruleData).build());

        String putItemResponse = paymentRuleEngineRepository.storeRules(item);
        logger.info("Dynamo DB respose :{}", putItemResponse);
    }


}
