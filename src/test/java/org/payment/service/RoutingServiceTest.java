package org.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.payment.action.*;
import org.payment.entity.PaymentRules;
import org.payment.entity.PaymentTransaction;
import org.payment.loader.RuleLoader;
import org.payment.model.PaymentTransactionDTO;
import org.payment.repository.PaymentRulesRepository;
import org.payment.repository.PaymentTransactionRepository;
import org.payment.rule.PaymentMethodRule;
import org.payment.rule.Rule;
import org.payment.rule.TransactionAmountRule;
import org.payment.rule.TransactionRouteRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class RoutingServiceTest {

    @MockBean
    private PaymentTransactionRepository transactionRepositoryMock;

    @MockBean
    private PaymentRulesRepository rulesRepositoryMock;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RuleLoader ruleLoaderMock;

    @Autowired
    private RoutingService routingService;

    @Autowired
     private AccessService accessService;


    List<PaymentTransaction> transactions;

    List<PaymentRules> paymentRulesList;

    List<Rule> rules;


    @BeforeEach
    public void setup() throws JsonProcessingException {

        PaymentTransaction paymentTransactionEntity = new PaymentTransaction();
        paymentTransactionEntity.setId("121231312323");
        paymentTransactionEntity.setCustomerId("123456789");
        paymentTransactionEntity.setCurrency("NOK");
        paymentTransactionEntity.setLocation("NORWAY");
        paymentTransactionEntity.setAmount(10000.0);
        paymentTransactionEntity.setCustomerType("Non-Employee");
        paymentTransactionEntity.setPaymentCardNetwork("Mastercard");
        paymentTransactionEntity.setPaymentMethod("VIPPS");
        paymentTransactionEntity.setCardType("Credit Card");

        transactions = new ArrayList<>();
        transactions.add(paymentTransactionEntity);
        rules = new ArrayList<>();

        ErrorAction errorAction = new ErrorAction("Error while processing!");
        Action action1 = new RoutingAction("PaymentMethod", 20, 50, 30, accessService);
        Rule rule1 = new TransactionRouteRule("SEK",  "Mastercard", action1);
        rules.add(rule1);

        Action action2 = new AdditionalFeeAction("Addition fee will be charged!", 10000.00);
        Rule rule2 = new TransactionAmountRule(100.00, 10000.00, action2);
        rules.add(rule2);

        String ruleJsonNode = """
                {
                 "id": "Rule6",
                 "type": "TransactionRouteRule",
                 "paymentNetwork": "Mastercard",
                 "currency": "SEK",
                 "action": {
                   "type": "RoutingAction",
                   "paymentNetwork": "Mastercard",
                   "currency": "SEK",
                   "AcquirerA": "20",
                   "AcquirerB": "50",
                   "AcquirerC": "30",
                   "messageTemplate": "20% of transactions = {countA} routed to Acquirer A. 50% transactions = {countB} routed to Acquirer B. 30% of transactions = {countC} routed to Acquirer C"
                  }
                 }
                 """;

        paymentRulesList = new ArrayList<>();
        PaymentRules paymentRules1 = new PaymentRules();
        paymentRules1.setRuleId("rule1");
        paymentRules1.setRuleData(objectMapper.writeValueAsString(ruleJsonNode));
        paymentRulesList.add(paymentRules1);
    }

    @Test
    public void testRouteTransactions() throws JsonProcessingException {
        when(rulesRepositoryMock.findAll()).thenReturn(paymentRulesList);

        when(transactionRepositoryMock.findTransactionsforDay(anyString(), anyString(),
                any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(transactions);

        when(ruleLoaderMock.parseRule(any(JsonNode.class))).thenReturn(rules.get(0));

        String response = routingService.routeTransactions();

        assertTrue(response.contains("Transaction Routing Completed for the date :"));

        verify(rulesRepositoryMock, times(1)).findAll();
        verify(transactionRepositoryMock, times(1)).findTransactionsforDay(anyString(), anyString(),
                any(LocalDateTime.class), any(LocalDateTime.class));
    }

}
