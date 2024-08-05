package org.payment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.payment.model.PaymentTransactionDTO;
import org.payment.repository.PaymentTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class AccessServiceTest {

    @MockBean
    private PaymentTransactionRepository transactionRepositoryMock;

    private PaymentTransactionDTO paymentTransactionDTO;

    @Autowired
    private AccessService accessService;

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
    }

    @Test
    public void testCheck3dsAccess() {
        when(transactionRepositoryMock.exist3DSEnabledTransactions(anyString(), any(LocalDateTime.class))).thenReturn(true);
        boolean response = accessService.check3DSAccess(paymentTransactionDTO);

        assertTrue(response);
        verify(transactionRepositoryMock, times(1)).exist3DSEnabledTransactions(anyString(), any(LocalDateTime.class));
    }


}
