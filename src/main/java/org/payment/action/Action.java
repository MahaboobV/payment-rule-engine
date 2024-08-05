package org.payment.action;

import org.payment.model.PaymentTransactionDTO;

import java.util.List;

public interface Action {
    default String execute(PaymentTransactionDTO transaction){
        return "";
    }
    default void execute(List<PaymentTransactionDTO> transactions) {
        //no-op implementation
    }
    String getMessageTemplate();
}
