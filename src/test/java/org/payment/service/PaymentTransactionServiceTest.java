package org.payment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.payment.engine.PaymentRuleEngine;
import org.payment.entity.PaymentTransaction;
import org.payment.model.PaymentTransactionDTO;
import org.payment.model.PaymentTransactionResponseDTO;
import org.payment.repository.PaymentTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class PaymentTransactionServiceTest {

    @MockBean
    private PaymentTransactionRepository paymentTransactionRepositoryMock;

    @MockBean
    private PaymentRuleEngine paymentRuleEngineMock;

    @Autowired
    private PaymentTransactionService transactionService;


    private PaymentTransactionDTO paymentTransactionDTO;

    private PaymentTransactionResponseDTO transactionResponseDTO;

    private PaymentTransaction paymentTransactionEntity;

    @BeforeEach
    public void setup() {
        paymentTransactionDTO = new PaymentTransactionDTO();
        paymentTransactionDTO.setCustomerId("123456789");
        paymentTransactionDTO.setCurrency("NOK");
        paymentTransactionDTO.setLocation("NORWAY");
        paymentTransactionDTO.setAmount(10000.0);
        paymentTransactionDTO.setCustomerType("Non-Employee");
        paymentTransactionDTO.setPaymentCardNetwork("Mastercard");
        paymentTransactionDTO.setPaymentMethod("VIPPS");
        paymentTransactionDTO.setCardType("Credit Card");

        paymentTransactionEntity = new PaymentTransaction();
        paymentTransactionEntity.setId("121231312323");
        paymentTransactionEntity.setCustomerId("123456789");
        paymentTransactionEntity.setCurrency("NOK");
        paymentTransactionEntity.setLocation("NORWAY");
        paymentTransactionEntity.setAmount(10000.0);
        paymentTransactionEntity.setCustomerType("Non-Employee");
        paymentTransactionEntity.setPaymentCardNetwork("Mastercard");
        paymentTransactionEntity.setPaymentMethod("VIPPS");
        paymentTransactionEntity.setCardType("Credit Card");

        transactionResponseDTO = new PaymentTransactionResponseDTO();
        transactionResponseDTO.setAdditionalInfo("Payment Transaction Saved !");

    }

    @Test
    public void testStorePaymentTransaction() throws IOException {
        when(paymentTransactionRepositoryMock.save(paymentTransactionEntity)).thenReturn(paymentTransactionEntity);

        PaymentTransactionResponseDTO respone = transactionService.storePaymentTransaction(paymentTransactionDTO);

        assertEquals("Payment transaction saved!", respone.getAdditionalInfo());

        verify(paymentTransactionRepositoryMock, times(1)).save(any(PaymentTransaction.class));

    }
}
