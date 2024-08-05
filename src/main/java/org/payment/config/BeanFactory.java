package org.payment.config;

import org.payment.service.PaymentRuleEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BeanFactory {
    private final PaymentRuleEngineService paymentRuleEngineService;

    @Autowired
    public BeanFactory(PaymentRuleEngineService paymentRuleEngineService) {
        this.paymentRuleEngineService = paymentRuleEngineService;
    }

}
