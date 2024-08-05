package org.payment.rule;

import org.payment.action.Action;
import org.payment.action.ErrorAction;
import org.payment.model.PaymentTransactionDTO;
import org.payment.service.AccessService;

public class DSAuthenticationRule implements Rule {
    private String customerType;
    private Action action;
    private ErrorAction errorAction;
    private AccessService accessService;

    public DSAuthenticationRule(AccessService accessService , String customerType,
                                Action action, ErrorAction errorAction) {
        this.accessService = accessService;
        this.customerType = customerType;
        this.action = action;
        this.errorAction = errorAction;
    }

    @Override
    public boolean evaluate(PaymentTransactionDTO paymentTransaction) {
        return accessService.check3DSAccess(paymentTransaction);
    }

    @Override
    public Action getAction() {
        return action;
    }

    @Override
    public ErrorAction getErrorAction() {
        return errorAction;
    }

}
