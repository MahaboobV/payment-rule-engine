package org.payment.engine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.payment.action.Action;
import org.payment.entity.PaymentRules;
import org.payment.entity.PaymentTransaction;
import org.payment.loader.RuleLoader;
import org.payment.model.PaymentTransactionDTO;
import org.payment.model.PaymentTransactionResponseDTO;
import org.payment.repository.PaymentRuleEngineRepository;
import org.payment.repository.PaymentRulesRepository;
import org.payment.rule.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PaymentRuleEngine {
    private static final Logger logger = LoggerFactory.getLogger(PaymentRuleEngine.class);

    private final List<Rule> rules;

    private final PaymentRuleEngineRepository paymentRuleEngineRepository;

    private final ObjectMapper mapper;

    private final RuleLoader ruleLoader;

    private final PaymentRulesRepository rulesRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    public PaymentRuleEngine(RuleLoader ruleLoader, PaymentRuleEngineRepository paymentRuleEngineRepository, ObjectMapper mapper, RuleLoader ruleLoader1, PaymentRulesRepository rulesRepository) throws IOException {
        this.rules = ruleLoader.loadRules();
        this.paymentRuleEngineRepository = paymentRuleEngineRepository;
        this.mapper = mapper;
        this.ruleLoader = ruleLoader1;
        this.rulesRepository = rulesRepository;
    }

    /**
     * @param transaction
     */
    public void evaluateTransaction(PaymentTransactionDTO transaction) {
        for (Rule rule : rules) {
            if (rule.evaluate(transaction)) {
                Action action = rule.getAction();
                String message = action.execute(transaction);
            }
        }
    }

    public List<String> evaluatePaymentTransaction(List<Rule> rules, PaymentTransactionDTO transaction) throws IOException {
        List<String> rulesApplicable = new ArrayList<>();
        for (Rule paymentRule : rules) {
            if (paymentRule.evaluate(transaction)) {
                Action action = paymentRule.getAction();
                String message = action.execute(transaction);
                rulesApplicable.add(message);
            }
        }
        return rulesApplicable;
    }

    public List<String> evaluatePaymentTransaction(PaymentTransactionDTO transaction) throws IOException {
        List<String> rulesApplicable = new ArrayList<>();
        List<Rule> rules = ruleLoader.loadRules();
        for (Rule paymentRule : rules) {
            if (paymentRule.evaluate(transaction)) {
                Action action = paymentRule.getAction();
                String message = action.execute(transaction);
                rulesApplicable.add(message);
            }
        }
        return rulesApplicable;
    }

    public PaymentTransactionResponseDTO evaluateTransactionH2(PaymentTransactionDTO transaction) throws IOException {
        List<PaymentRules> PaymentRules = rulesRepository.findAll();
        List<String> rulesApplicable = new ArrayList<>();

        for (PaymentRules paymentRule : PaymentRules) {
            JsonNode ruleNode = mapper.readTree(paymentRule.getRuleData());
            Rule rule = ruleLoader.parseRule(ruleNode);

            if (rule.evaluate(transaction)) {
                Action action = rule.getAction();
                String message = action.execute(transaction);
                rulesApplicable.add(message);
            }
        }
        PaymentTransactionResponseDTO response = new PaymentTransactionResponseDTO();
        response.setAmount(transaction.getAmount());
        response.setCurrency(transaction.getCurrency());

        response.setCustomerId(transaction.getCustomerId());
        response.setCustomerType(transaction.getCustomerType());
        response.setLocation(transaction.getLocation());
        response.setRulesApplicable(rulesApplicable);
        return response;
    }

    @Transactional
    public String processRules(JsonNode rootNode) {
        String responseMessage = "";
        try {
            if (null != rootNode) {
                logger.info("Parsing Json content.....");
                //Parse Rules from Json
                JsonNode rulesNode = rootNode.path("rules");
                for (JsonNode ruleNode : rulesNode) {
                    //Store in DynamoDB
                    //storeInDynamoDb(ruleNode);
                    responseMessage = storeInH2Db(ruleNode);
                }
                logger.info("Successfully processed and store Json file from S3");
            }
        } catch (Exception e) {
            logger.error("Error processing file from S3:" + e.getMessage());
        }
        return responseMessage;
    }

    public void evaluatePaymentTransactionRoutingRules(List<PaymentRules> paymentRules, List<PaymentTransaction> transactions) {
        List<PaymentTransactionDTO> paymentTransactionDTOs = setPaymentTransactionsDTO(transactions);

        for (PaymentRules paymentRule : paymentRules) {
            JsonNode ruleNode = null;
            try {
                ruleNode = objectMapper.readTree(paymentRule.getRuleData());
                if (ruleNode.path("type").asText().equals("TransactionRouteRule") &&
                        ruleNode.path("paymentNetwork").asText().equals("Mastercard") &&
                        ruleNode.path("currency").asText().equals("SEK")) {
                    Rule rule = ruleLoader.parseRule(ruleNode);
                    Action action = rule.getAction();
                    action.execute(paymentTransactionDTOs);
                }
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage());
            }
        }
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
