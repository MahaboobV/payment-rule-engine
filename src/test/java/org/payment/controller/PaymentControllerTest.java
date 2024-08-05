package org.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.payment.action.Action;
import org.payment.action.AuthenticationAction;
import org.payment.action.PaymentMethodAction;
import org.payment.engine.PaymentRuleEngine;
import org.payment.loader.RuleLoader;
import org.payment.model.PaymentTransactionDTO;
import org.payment.model.PaymentTransactionResponseDTO;
import org.payment.rule.CustomerTypeRule;
import org.payment.rule.Rule;
import org.payment.service.PaymentRuleEngineService;
import org.payment.service.PaymentTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentRuleEngineService ruleEngineServiceMock;

    @MockBean
    private RuleLoader ruleLoaderMock;

    @MockBean
    private PaymentTransactionService transactionServiceMock;

    @MockBean
    private PaymentRuleEngine ruleEngine;

    @Autowired
    ObjectMapper objectMapper;

    private PaymentTransactionResponseDTO paymentTransactionResponseDTO;

    private PaymentTransactionDTO paymentTransactionDTO;

    private List<Rule> rules;

    @BeforeEach
    public void setup() {

        paymentTransactionResponseDTO = new PaymentTransactionResponseDTO();
        paymentTransactionResponseDTO.setCustomerType("Non-Employee");
        paymentTransactionResponseDTO.setPaymentMethod("VIPPS");
        paymentTransactionResponseDTO.setLocation("NORWAY");
        paymentTransactionResponseDTO.setAmount(10000.0);
        paymentTransactionResponseDTO.setRulesApplicable(List.of("CustomerType"));

        paymentTransactionDTO = new PaymentTransactionDTO();
        paymentTransactionDTO.setCurrency("NOK");
        paymentTransactionDTO.setLocation("NORWAY");
        paymentTransactionDTO.setAmount(10000.0);
        paymentTransactionDTO.setCustomerType("Non-Employee");
        paymentTransactionDTO.setPaymentCardNetwork("Mastercard");
        paymentTransactionDTO.setPaymentMethod("VIPPS");
        paymentTransactionDTO.setCardType("Credit Card");

        rules = new ArrayList<>();
        Action action1 = new PaymentMethodAction("PaymentMethod", List.of("VIPPS", "Credit Card"));
        Rule rule1 = new CustomerTypeRule("Non-Employee", action1);
        rules.add(rule1);
        Action action2 = new AuthenticationAction("3DS authentication required");
        Rule rule2 = new CustomerTypeRule("Non-Employee", action2);
        rules.add(rule2);
    }

    @Test
    public void testRulesEvaluate() throws Exception {
        when(ruleEngineServiceMock.evaluateTransaction(any(PaymentTransactionDTO.class))).thenReturn(paymentTransactionResponseDTO);

        mockMvc.perform(post("/api/payments/evaluate").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentTransactionDTO))).andExpect(status().isOk());

        verify(ruleEngineServiceMock, times(1)).evaluateTransactionH2(any(PaymentTransactionDTO.class));
    }

    @Test
    public void testRulesEvaluateActual() throws Exception {
        when(ruleEngineServiceMock.evaluateTransaction(any(PaymentTransactionDTO.class))).thenReturn(paymentTransactionResponseDTO);

        mockMvc.perform(post("/api/payments/evaluate/actual").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentTransactionDTO))).andExpect(status().isOk());

        verify(ruleEngineServiceMock, times(1)).evaluateTransaction(any(PaymentTransactionDTO.class));
    }

    @Test
    public void testLoadRules() throws Exception {
        when(ruleLoaderMock.loadPaymentRules()).thenReturn("paymentRules Loaded");

        mockMvc.perform(get("/api/payments/loadRules").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

        verify(ruleLoaderMock, times(1)).loadPaymentRules();
    }

    @Test
    public void testSavePaymentTransaction() throws Exception {
        when(transactionServiceMock.storePaymentTransaction(any(PaymentTransactionDTO.class))).thenReturn(paymentTransactionResponseDTO);

        mockMvc.perform(post("/api/payments/confirm/transaction").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentTransactionDTO))).andExpect(status().isOk());

        verify(transactionServiceMock, times(1)).storePaymentTransaction(any(PaymentTransactionDTO.class));
    }


}
