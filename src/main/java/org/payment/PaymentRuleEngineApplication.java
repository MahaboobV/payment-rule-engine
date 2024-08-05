package org.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
public class PaymentRuleEngineApplication {
    private static final Logger logger = LoggerFactory.getLogger(PaymentRuleEngineApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(PaymentRuleEngineApplication.class, args);
        logger.info("Payment Rule Engine Started!");
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}