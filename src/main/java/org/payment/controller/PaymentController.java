package org.payment.controller;


import org.payment.model.PaymentTransaction;
import org.payment.engine.PaymentRuleEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class    PaymentController {
    private final PaymentRuleEngine ruleEngine;

    @Autowired
    public PaymentController(PaymentRuleEngine ruleEngine) {
        this.ruleEngine = ruleEngine;
    }

    @PostMapping("/evaluate")
    public void evaluateTransaction(@RequestBody PaymentTransaction transaction) {
        ruleEngine.evaluateTransaction(transaction);
    }
}
