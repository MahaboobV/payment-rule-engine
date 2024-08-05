package org.payment.rule;

import org.payment.action.Action;
import org.payment.action.ErrorAction;
import org.payment.model.PaymentTransactionDTO;

public interface Rule {
    boolean evaluate(PaymentTransactionDTO paymentTransaction);
    Action getAction();
    ErrorAction getErrorAction();
}
