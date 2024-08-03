package org.payment.action;

import org.payment.model.PaymentTransaction;

public interface Action {
    void execute(PaymentTransaction transaction);
    String getMessageTemplate();
}
