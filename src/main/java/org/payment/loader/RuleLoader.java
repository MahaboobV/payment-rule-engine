package org.payment.loader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.payment.action.*;
import org.payment.rule.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class RuleLoader {

    private ResourceLoader resourceLoader;

    @Value("${rules.file.path}")
    private String filePath;

    public RuleLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public List<Rule> loadRules() throws IOException {

        Resource resource = resourceLoader.getResource("classpath:" + filePath);

        if (!resource.exists()) {
            throw new IOException("File not found: " + resource.getURI());
        }
        ObjectMapper objectMapper = new ObjectMapper();

        try (InputStream inputStream = resource.getInputStream()) {
            JsonNode rootNode = objectMapper.readTree(inputStream);
            JsonNode rulesNode = rootNode.path("rules");
            return parseRules(rulesNode);
        }
    }

    private List<Rule> parseRules(JsonNode rootNode) {
        List<Rule> rules = new ArrayList<>();
        for (JsonNode ruleNode : rootNode) {
            String ruleType = ruleNode.path("type").asText();
            JsonNode actionNode = ruleNode.path("action");
            Action action = loadAction(actionNode);

            switch (ruleType) {
                case "PaymentMethodRule":
                    rules.add(new PaymentMethodRule(ruleNode.path("location").asText(), action));
                    break;
                case "CustomerTypeRule":
                    rules.add(new CustomerTypeRule(ruleNode.path("customerType").asText(), action));
                    break;
                case "TransactionAmountRule":
                    rules.add(new TransactionAmountRule(ruleNode.path("minAmount").asDouble(),
                            ruleNode.path("maxAmount").asDouble(), action));
                    break;
                case "TransactionRouteRule":
                    rules.add(new TransactionRouteRule(ruleNode.path("currency").asText(),
                            ruleNode.path("paymentCardNetwork").asText(), action));
                    break;
                case "NewFeatureRule":
                    rules.add(new NewFeatureRule(ruleNode.path("customerType").asText(), action));
                    break;
                case "CardTypeRule":
                    rules.add(new CardTypeRule(ruleNode.path("cardType").asText(), action));
                    break;
                case "PaymentCardNetworkRule":
                    rules.add(new PaymentCardNetworkRule(ruleNode.path("paymentNetwork").asText(), action));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown rule type: " + ruleType);
            }
        }
        return rules;
    }

    private Action loadAction(JsonNode actionNode) {
        String actionType = actionNode.path("type").asText();
        String messageTemplate = actionNode.path("messageTemplate").asText();

        switch (actionType) {
            case "RoutingAction":
                String currency = actionNode.path("currency").asText();
                return new RoutingAction(messageTemplate, currency);
            case "AdditionalInfoAction":
                List<String> riskCountries = new ArrayList<>();
                double thresholdAmount = actionNode.path("thresholdAmount").asDouble();
                String currencyTransaction = actionNode.path("currency").asText();
                Iterator<JsonNode> locationNodes = actionNode.path("location").elements();
                while(locationNodes.hasNext()) {
                    riskCountries.add(locationNodes.next().asText());
                }
                return new AdditionalInfoAction(thresholdAmount, messageTemplate, currencyTransaction, riskCountries);
            case "AdditionalFeeAction":
                double tAmount = actionNode.path("thresholdAmount").asDouble();
                return new AdditionalFeeAction(messageTemplate, tAmount);
            case "PaymentMethodAction":
                return new PaymentMethodAction(messageTemplate);
            case "FeatureAction":
                return new FeatureAction(messageTemplate);
            case "AuthenticationAction":
                boolean dsLastYear = actionNode.path("3dslastyear").asBoolean();
                return new AuthenticationAction(messageTemplate, dsLastYear);
            default:
                throw new IllegalArgumentException("Unknown action types: " + actionType);
        }
    }
}
