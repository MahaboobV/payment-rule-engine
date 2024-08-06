package org.payment.repository;

import org.payment.loader.RuleLoader;
import org.payment.rule.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Repository
public class PaymentRuleEngineRepository {
    private static final Logger logger = LoggerFactory.getLogger(PaymentRuleEngineRepository.class);

    //private final DynamoDbClient dynamoDbClient;

    private final RuleLoader ruleLoader;

    private final String TABLE_NAME = "PaymentRulesTable";

    @Autowired
    public PaymentRuleEngineRepository(RuleLoader ruleLoader) {
        //this.dynamoDbClient = dynamoDbClient;
        this.ruleLoader = ruleLoader;
    }

    public List<Rule> getAllRules() throws IOException {
        return ruleLoader.loadRules();


    }

    public String storeRules(Map<String, AttributeValue> item) {
        PutItemRequest putItemRequest = PutItemRequest.builder().tableName(TABLE_NAME).item(item).build();
        String response = null;
        /*try {
            PutItemResponse putItemResponse = dynamoDbClient.putItem(putItemRequest);
            logger.info("Successfully inserted item: " + putItemResponse);
            if(putItemResponse.sdkHttpResponse().isSuccessful()) {
                response = String.valueOf(putItemResponse.sdkHttpResponse().statusText());
            }
        } catch (DynamoDbException ex) {
            logger.error("Failed to insert item: " + ex.getMessage());
        }*/
        return response;
    }
}
