package org.payment.rule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.payment.action.Action;
import org.payment.action.AdditionalFeeAction;
import org.payment.action.ErrorAction;
import org.payment.action.PaymentMethodAction;
import org.payment.engine.PaymentRuleEngine;
import org.payment.entity.PaymentRules;
import org.payment.exception.PaymentRuleViolationException;
import org.payment.loader.RuleLoader;
import org.payment.model.PaymentTransactionDTO;
import org.payment.model.PaymentTransactionErrorResponseDTO;
import org.payment.model.PaymentTransactionResponseDTO;
import org.payment.model.RuleViolation;
import org.payment.repository.PaymentRuleEngineRepository;
import org.payment.repository.PaymentRulesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class PaymentRuleEngineTest {

    @MockBean
    private PaymentRuleEngineRepository paymentRuleEngineRepositoryMock;


    @MockBean
    private PaymentRulesRepository rulesRepositoryMock;

    @MockBean
    private RuleLoader ruleLoaderMock;

    @Autowired
    private PaymentRuleEngine paymentRuleEngine;
    @Autowired
    private ObjectMapper objectMapper;

    private List<Rule> rules;

    private PaymentTransactionDTO paymentTransactionDTO;

    private List<PaymentRules> paymentRulesList;

    private JsonNode ruleNode;

    private PaymentTransactionErrorResponseDTO errorResponse;

    private PaymentTransactionResponseDTO paymentTransactionResponseDTO;

    @BeforeEach
    public void setup() throws JsonProcessingException {
        paymentTransactionDTO = new PaymentTransactionDTO();
        paymentTransactionDTO.setCurrency("NOK");
        paymentTransactionDTO.setLocation("NORWAY");
        paymentTransactionDTO.setAmount(10000.0);
        paymentTransactionDTO.setCustomerType("Non-Employee");
        paymentTransactionDTO.setPaymentCardNetwork("Mastercard");
        paymentTransactionDTO.setPaymentMethod("VIPPS");
        paymentTransactionDTO.setCardType("Credit Card");

        paymentTransactionResponseDTO = new PaymentTransactionResponseDTO();
        paymentTransactionResponseDTO.setCustomerType("Non-Employee");
        paymentTransactionResponseDTO.setPaymentMethod("VIPPS");
        paymentTransactionResponseDTO.setLocation("NORWAY");
        paymentTransactionResponseDTO.setAmount(10000.0);
        paymentTransactionResponseDTO.setRulesApplicable(List.of("CustomerType"));

        String ruleJsonNode = "{\n" +
                "  \"rules\": [\n" +
                "    {\n" +
                "      \"id\": \"Rule3\",\n" +
                "      \"type\": \"PaymentMethodRule\",\n" +
                "      \"location\": \"NORWAY\",\n" +
                "      \"paymentMethods\": [\"VIPPS\", \"Credit Card\", \"Debit Card\", \"OnlineBanking\"],\n" +
                "      \"action\": {\n" +
                "        \"type\": \"PaymentMethodAction\",\n" +
                "        \"paymentMethods\": [\"VIPPS\", \"Credit Card\", \"Debit Card\", \"OnlineBanking\"],\n" +
                "        \"messageTemplate\": \"Available Payment methods in {location} are : {paymentMethods}\"\n" +
                "      },\n" +
                "      \"errorAction\": {\n" +
                "        \"type\": \"PaymentMethodViolationAction\",\n" +
                "        \"messageTemplate\": \"Selected Payment is not valid in the {location}\"\n" +
                "      }\n" +
                "    }  ]\n" +
                "}";
        rules = new ArrayList<>();
        ErrorAction errorAction = new ErrorAction("Error while processing!");
        Action action1 = new PaymentMethodAction("PaymentMethod", List.of("VIPPS", "Credit Card"));
        Rule rule1 = new PaymentMethodRule("NORWAY", List.of("VIPPS", "Credit Card"), action1, errorAction);
        rules.add(rule1);
        Action action2 = new AdditionalFeeAction("Addition fee will be charged!", 10000.00);
        Rule rule2 = new TransactionAmountRule(100.00, 10000.00, action2);
        rules.add(rule2);

        paymentRulesList = new ArrayList<>();
        PaymentRules paymentRules1 = new PaymentRules();
        paymentRules1.setRuleId("rule1");
        paymentRules1.setRuleData(objectMapper.writeValueAsString(rule1));
        paymentRulesList.add(paymentRules1);

        PaymentRules paymentRules2 = new PaymentRules();
        paymentRules2.setRuleId("rule2");
        paymentRules2.setRuleData(objectMapper.writeValueAsString(rule2));
        paymentRulesList.add(paymentRules2);

        ruleNode = objectMapper.readTree(ruleJsonNode);

        List<RuleViolation> violationList = new ArrayList<>();
        RuleViolation ruleViolation = new RuleViolation();
        ruleViolation.setRule("PaymentMethodRule");
        ruleViolation.setDescription("PaymentMethodRule valuation failed");
        ruleViolation.setSuggestion("Select only available payment methods");
        violationList.add(ruleViolation);

        errorResponse = new PaymentTransactionErrorResponseDTO();
        errorResponse.setErroMessage("Rule valuation failed");
        errorResponse.setDetails(violationList);
    }

    @Test
    public void testEvaluatePaymentTransaction() throws IOException {
        when(rulesRepositoryMock.findAll()).thenReturn(paymentRulesList);
        when(ruleLoaderMock.parseRule(any(JsonNode.class))).thenReturn(rules.get(0));

        PaymentTransactionResponseDTO responseDTO = paymentRuleEngine.evaluateTransactionH2(paymentTransactionDTO);
        assertEquals(responseDTO.getAmount(), paymentTransactionResponseDTO.getAmount());

        verify(rulesRepositoryMock, times(1)).findAll();
        verify(ruleLoaderMock, times(2)).parseRule(any(JsonNode.class));
    }

    @Test
    public void testEvaluatePaymentTransactionWithRule() throws IOException {

        List<String> applicableRules = paymentRuleEngine.evaluatePaymentTransaction(rules, paymentTransactionDTO);
        assertTrue(applicableRules.size() != 0);
    }

    @Test
    public void testEvaluatePaymentTransactionWithoutRule() throws IOException {
        when(ruleLoaderMock.loadRules()).thenReturn(rules);

        List<String> applicableRules = paymentRuleEngine.evaluatePaymentTransaction(paymentTransactionDTO);
        assertTrue(applicableRules.size()!=0);

        verify(ruleLoaderMock, times(2)).loadRules();
    }

    @Test
    public void testEvaluateTransactionH2_failure() throws IOException {
        ErrorAction errorAction = new ErrorAction("Error while processing!");
        Action action1 = new PaymentMethodAction("PaymentMethod", List.of("VIPPS", "Credit Card"));
        Rule rule = new PaymentMethodRule("TEST", List.of("Credit Card"), action1, errorAction);

        when(rulesRepositoryMock.findAll()).thenReturn(paymentRulesList);
        when(ruleLoaderMock.parseRule(any(JsonNode.class))).thenReturn(rule);
        PaymentRuleViolationException violationException = assertThrows(PaymentRuleViolationException.class, () -> {
            paymentRuleEngine.evaluateTransactionH2(paymentTransactionDTO);
        });
        assertEquals("Rules violated !", violationException.getMessage());

        verify(rulesRepositoryMock, times(1)).findAll();
        verify(ruleLoaderMock, times(2)).parseRule(any(JsonNode.class));
    }

    @Test
    public void testProcessRules() throws IOException {
        when(ruleLoaderMock.loadPaymentRules()).thenReturn("Payment Rules loaded!");

        String respone = paymentRuleEngine.processRules(ruleNode);
        assertEquals(respone, "Payment Rules loaded!");

        verify(ruleLoaderMock, times(1)).loadPaymentRules();
    }


}
