package org.payment.rule;

import org.payment.action.Action;
import org.payment.model.PaymentTransaction;

public interface Rule {
    boolean evaluate(PaymentTransaction paymentTransaction);
    Action getAction();
}
