package org.payment.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.payment.model.PaymentTransactionDTO;
import org.payment.model.PaymentTransactionResponseDTO;
import org.payment.service.PaymentRuleEngineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PaymenTransactionProcessingLamda implements RequestHandler<PaymentTransactionDTO, String> {

    private static final Logger logger = LoggerFactory.getLogger(PaymenTransactionProcessingLamda.class);

    @Autowired
    private PaymentRuleEngineService paymentRuleEngineService;

    @Override
    public String handleRequest(PaymentTransactionDTO transaction, Context context) {
        logger.info("Handling payment transaction request !");

        String transactionResponse="";

        try {
            PaymentTransactionResponseDTO response = paymentRuleEngineService.evaluateTransaction(transaction);
            transactionResponse =  response.toString();
        } catch (IOException e) {
           logger.error("Payment transaction processing failed !.. {}", e.getMessage());
        }
        return transactionResponse;
    }
}
