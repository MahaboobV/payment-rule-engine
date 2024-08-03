package org.payment;

import org.payment.loader.RuleLoader;
import org.payment.rule.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.List;

@SpringBootApplication
public class PaymentRuleEngineApplication implements CommandLineRunner {

    @Autowired
    private RuleLoader ruleLoader;


    public static void main(String[] args) {
        SpringApplication.run(PaymentRuleEngineApplication.class, args);
        System.out.println("Payment Rule Engine Started!");
    }

    @Override
    public void run(String... args) {
        try {
            List<Rule> rules = ruleLoader.loadRules();
            System.out.println("Loaded rules: " + rules);
        } catch (IOException e) {
            System.err.println("Failed to load rules: " + e.getMessage());
        }
    }
}