package org.payment.rule;

import org.payment.action.Action;
import org.payment.model.PaymentTransaction;

public class CardTypeRule implements Rule{
    private String cardType;
    private Action action;

    public CardTypeRule(String cardType, Action action) {
        this.cardType = cardType;
        this.action = action;
    }

    @Override
    public boolean evaluate(PaymentTransaction paymentTransaction) {
        return paymentTransaction.getCardType().equals(cardType);
    }

    @Override
    public Action getAction() {
        return action;
    }
}
