package org.payment.rule;

import org.payment.action.Action;
import org.payment.model.PaymentTransactionDTO;
import org.payment.service.AccessService;
import org.payment.service.PaymentRuleEngineService;

public class DSAuthenticationRule implements Rule {
    private String customerType;
    private Action action;
    private AccessService accessService;

    public DSAuthenticationRule(AccessService accessService , String customerType, Action action) {
        this.accessService = accessService;
        this.customerType = customerType;
        this.action = action;
    }

    @Override
    public boolean evaluate(PaymentTransactionDTO paymentTransaction) {
        return accessService.check3DSAccess(paymentTransaction);
    }

    @Override
    public Action getAction() {
        return action;
    }

}
