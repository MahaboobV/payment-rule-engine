package org.payment.controller;


import org.payment.loader.RuleLoader;
import org.payment.model.PaymentTransactionDTO;
import org.payment.engine.PaymentRuleEngine;
import org.payment.model.PaymentTransactionResponseDTO;
import org.payment.service.PaymentRuleEngineService;
import org.payment.service.PaymentTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/payments")
public class    PaymentController {
    private final PaymentRuleEngine ruleEngine;
    private PaymentRuleEngineService service;
    private RuleLoader loader;
    private PaymentTransactionService transactionService;

    @Autowired
    public PaymentController(PaymentRuleEngine ruleEngine, PaymentRuleEngineService service, RuleLoader loader,
                             PaymentTransactionService transactionService) {
        this.ruleEngine = ruleEngine;
        this.service = service;
        this.loader = loader;
        this.transactionService = transactionService;
    }

    @PostMapping("/evaluate")
    public ResponseEntity<PaymentTransactionResponseDTO> evaluateTransaction(@RequestBody PaymentTransactionDTO transaction) throws IOException {
        PaymentTransactionResponseDTO response = service.evaluateTransactionH2(transaction);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/evaluate/actual")
    public ResponseEntity<PaymentTransactionResponseDTO> evaluateActualTransaction(@RequestBody PaymentTransactionDTO transaction) throws IOException {
        PaymentTransactionResponseDTO response = service.evaluateTransaction(transaction);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/loadRules")
    public ResponseEntity<String> loadPaymentRules() throws IOException {
        String response = loader.loadPaymentRules();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/confirm/transaction")
    public ResponseEntity<PaymentTransactionResponseDTO> savePaymentTransaction(@RequestBody PaymentTransactionDTO transaction) throws IOException {
        PaymentTransactionResponseDTO responseDTO = transactionService.storePaymentTransaction(transaction);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
