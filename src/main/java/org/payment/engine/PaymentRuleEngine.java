package org.payment.engine;

import org.payment.action.Action;
import org.payment.loader.RuleLoader;
import org.payment.model.PaymentTransactionDTO;
import org.payment.rule.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class PaymentRuleEngine {

    private List<Rule> rules;

    @Autowired
    public PaymentRuleEngine(RuleLoader ruleLoader) throws IOException {
        this.rules = ruleLoader.loadRules();
    }

    /**
     *
     * @param transaction
     */
    public void evaluateTransaction(PaymentTransactionDTO transaction) {
        for (Rule rule : rules) {
            if (rule.evaluate(transaction)) {
                Action action = rule.getAction();
                String message = action.execute(transaction);
            }
        }
    }
}
