package org.payment.loader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.payment.action.*;
import org.payment.entity.PaymentRules;
import org.payment.repository.PaymentRulesRepository;
import org.payment.rule.*;
import org.payment.service.AccessService;
import org.springframework.beans.factory.annotation.Autowired;
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

    private PaymentRulesRepository rulesRepository;

    private AccessService accessService;

    private ObjectMapper mapper;

    @Value("${rules.file.path}")
    private String filePath;

    @Autowired
    public RuleLoader(ResourceLoader resourceLoader, PaymentRulesRepository rulesRepository,
                      AccessService accessService, ObjectMapper mapper) {
        this.resourceLoader = resourceLoader;
        this.rulesRepository = rulesRepository;
        this.accessService = accessService;
        this.mapper = mapper;
    }

    /**
     *
     * @return
     * @throws IOException
     */
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

    /**
     *
     * @return
     * @throws IOException
     */
    public String loadPaymentRules() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + filePath);

        if (!resource.exists()) {
            throw new IOException("File not found: " + resource.getURI());
        }
        ObjectMapper objectMapper = new ObjectMapper();

        try (InputStream inputStream = resource.getInputStream()) {
            JsonNode rootNode = objectMapper.readTree(inputStream);
            JsonNode rulesNode = rootNode.path("rules");
            Iterator<JsonNode> elements = rulesNode.elements();

            while (elements.hasNext()) {
                JsonNode ruleNode = elements.next();
                String ruleId = ruleNode.get("id").asText();
                String ruleJson = objectMapper.writeValueAsString(ruleNode);
                PaymentRules paymentRules = new PaymentRules(ruleId, ruleJson);
                rulesRepository.save(paymentRules);
            }
            return "paymentRules Loaded";
        }
    }

    /**
     *
     * @param rootNode
     * @return
     */
    public List<Rule> parseRules(JsonNode rootNode) {
        List<Rule> rules = new ArrayList<>();
        for (JsonNode ruleNode : rootNode) {
            String ruleType = ruleNode.path("type").asText();
            JsonNode actionNode = ruleNode.path("action");
            Action action = loadAction(actionNode);

            JsonNode errorActionNode = ruleNode.path("errorAction");
            ErrorAction errorAction = loadErrorAction(errorActionNode);

            switch (ruleType) {
                case "PaymentMethodRule" -> {
                    List<String> paymentMethods = new ArrayList<>();
                    JsonNode paymentMethodNode = ruleNode.path("paymentMethods");
                    Iterator<JsonNode> paymentMethodNodeIterator = ruleNode.path("paymentMethods").elements();
                    while (paymentMethodNodeIterator.hasNext()) {
                        paymentMethods.add(paymentMethodNodeIterator.next().asText());
                    }
                    rules.add(new PaymentMethodRule(ruleNode.path("location").asText(), paymentMethods, action, errorAction));
                }

                case "CustomerTypeRule" -> rules.add(new CustomerTypeRule(ruleNode.path("customerType").asText(), action));

                case "TransactionAmountRule" -> rules.add(new TransactionAmountRule(ruleNode.path("minAmount").asDouble(),
                        ruleNode.path("maxAmount").asDouble(), action));

                case "TransactionRouteRule" -> rules.add(new TransactionRouteRule(ruleNode.path("currency").asText(),
                        ruleNode.path("paymentCardNetwork").asText(), action));

                case "NewFeatureRule" -> rules.add(new NewFeatureRule(ruleNode.path("customerType").asText(), action));

                case "CardTypeRule" -> rules.add(new CardTypeRule(ruleNode.path("cardType").asText(), action));

                case "PaymentCardNetworkRule" -> rules.add(new PaymentCardNetworkRule(ruleNode.path("paymentNetwork").asText(), action));

                case "DSAuthenticationRule" -> rules.add(new DSAuthenticationRule(accessService, ruleNode.path("customerType").asText(), action, errorAction));

                default -> throw new IllegalArgumentException("Unknown rule type: " + ruleType);
            }
        }
        return rules;
    }

    private Action loadAction(JsonNode actionNode) {
        String actionType = actionNode.path("type").asText();
        String messageTemplate = actionNode.path("messageTemplate").asText();

        switch (actionType) {
            case "RoutingAction":
                int acquirerAPercentage = actionNode.path("AcquirerA").asInt();
                int acquirerBPercentage = actionNode.path("AcquirerB").asInt();
                int acquirerCPercentage = actionNode.path("AcquirerC").asInt();

                return new RoutingAction(messageTemplate, acquirerAPercentage, acquirerBPercentage, acquirerCPercentage, accessService);

            case "AdditionalInfoAction":
                List<String> riskCountries = new ArrayList<>();
                double thresholdAmount = actionNode.path("thresholdAmount").asDouble();
                String currencyTransaction = actionNode.path("currency").asText();
                Iterator<JsonNode> locationNodes = actionNode.path("location").elements();
                while (locationNodes.hasNext()) {
                    riskCountries.add(locationNodes.next().asText());
                }
                return new AdditionalInfoAction(thresholdAmount, messageTemplate, currencyTransaction, riskCountries);

            case "AdditionalFeeAction":
                double tAmount = actionNode.path("thresholdAmount").asDouble();
                return new AdditionalFeeAction(messageTemplate, tAmount);

            case "PaymentMethodAction":
                JsonNode paymentMethodNode = actionNode.path("paymentMethods");
                List<String> paymentMethods = new ArrayList<>();
                if (paymentMethodNode.isArray()) {
                    Iterator<JsonNode> elements = paymentMethodNode.elements();
                    while (elements.hasNext()) {
                        JsonNode method = elements.next();
                        paymentMethods.add(method.asText());
                    }
                } else {
                    paymentMethods.add(paymentMethodNode.asText());
                }
                return new PaymentMethodAction(messageTemplate, paymentMethods);

            case "FeatureAction":
                return new FeatureAction(messageTemplate);

            case "AuthenticationAction":
                return new AuthenticationAction(messageTemplate);

            default:
                throw new IllegalArgumentException("Unknown action type: " + actionType);
        }
    }

    private ErrorAction loadErrorAction(JsonNode actionNode) {
        String errorActionType = actionNode.path("type").asText();
        String messageTemplate = actionNode.path("messageTemplate").asText();
        return switch (errorActionType) {
            case "PaymentMethodViolationAction", "DSAuthenticationViolationAction", "TransactionAmountRuleViolationAction" -> new ErrorAction(messageTemplate);
            default -> new ErrorAction(messageTemplate);
            //  throw new IllegalArgumentException("Unknown error action type :" + errorActionType);
        };

    }

    /**
     *
     * @param ruleNode
     * @return
     */
    public Rule parseRule(JsonNode ruleNode) {
        String ruleType = ruleNode.path("type").asText();
        JsonNode actionNode = ruleNode.path("action");
        Action action = loadAction(actionNode);
        JsonNode errorActionNode = ruleNode.path("errorAction");
        ErrorAction errorAction = loadErrorAction(errorActionNode);

        switch (ruleType) {
            case "PaymentMethodRule" -> {
                List<String> paymentMethods = new ArrayList<>();
                JsonNode paymentMethodNode = ruleNode.path("paymentMethods");
                Iterator<JsonNode> paymentMethodNodeIterator = ruleNode.path("paymentMethods").elements();
                while (paymentMethodNodeIterator.hasNext()) {
                    paymentMethods.add(paymentMethodNodeIterator.next().asText());
                }
                return new PaymentMethodRule(ruleNode.path("location").asText(), paymentMethods,  action, errorAction);
            }
            case "CustomerTypeRule" -> {
                return new CustomerTypeRule(ruleNode.path("customerType").asText(), action);
            }
            case "TransactionAmountRule" -> {
                return new TransactionAmountRule(ruleNode.path("minAmount").asDouble(),
                        ruleNode.path("maxAmount").asDouble(), action);
            }
            case "TransactionRouteRule" -> {
                return new TransactionRouteRule(ruleNode.path("currency").asText(),
                        ruleNode.path("paymentCardNetwork").asText(), action);
            }
            case "NewFeatureRule" -> {
                return new NewFeatureRule(ruleNode.path("customerType").asText(), action);
            }
            case "CardTypeRule" -> {
                return new CardTypeRule(ruleNode.path("cardType").asText(), action);
            }
            case "PaymentCardNetworkRule" -> {
                return new PaymentCardNetworkRule(ruleNode.path("paymentNetwork").asText(), action);
            }

            case "DSAuthenticationRule" -> {
                return new DSAuthenticationRule(accessService, ruleNode.path("customerType").asText(), action, errorAction);
            }

            default -> throw new IllegalArgumentException("Unknown rule type: " + ruleType);
        }
    }
}
