package org.payment.action;

import org.payment.model.PaymentTransactionDTO;
import org.payment.service.AccessService;
import org.payment.service.RoutingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class RoutingAction implements Action {
    private static final Logger logger = LoggerFactory.getLogger(RoutingService.class);

    private final String messageTemplate;

    private final int acquirerAPecentage;

    private final int acquirerBPecentage;

    private final int acquirerCPecentage;

    private AccessService accessService;

    public RoutingAction(String messageTemplate,
                         int acquirerAPecentage, int acquirerBPecentage,
                         int acquirerCPecentage, AccessService accessService) {

        this.messageTemplate = messageTemplate;

        this.acquirerAPecentage = acquirerAPecentage;
        this.acquirerBPecentage = acquirerBPecentage;
        this.acquirerCPecentage = acquirerCPecentage;
        this.accessService = accessService;
    }

    @Override
    public void execute(List<PaymentTransactionDTO> transactions) {
        int totalTransactions = transactions.size();

        double acrAPerc = (double) acquirerAPecentage/100;
        double acrBPerc = (double) acquirerBPecentage/100;
        double acrCPerc = (double) acquirerCPecentage/100;

        int noOfTransactionsToRouteAcquirerA = (int) Math.ceil(totalTransactions * acrAPerc);
        int noOfTransactionsToRouteAcquirerB = (int) Math.ceil(totalTransactions * acrBPerc);
        int noOfTransactionsToRouteAcquirerC = (int) Math.ceil(totalTransactions * acrCPerc);

        List<PaymentTransactionDTO> transactionToRouteA = transactions.stream()
                .limit(noOfTransactionsToRouteAcquirerA).collect(Collectors.toList());

        accessService.routeToAcquirer(transactionToRouteA, "AcquirerA");
        String finalMessage = messageTemplate.replace("{countA}", String.valueOf(transactionToRouteA.size()));

        List<PaymentTransactionDTO> transactionToRouteB = transactions.stream()
                .limit(noOfTransactionsToRouteAcquirerB).collect(Collectors.toList());

        accessService.routeToAcquirer(transactionToRouteB, "AcquirerB");
        finalMessage = finalMessage.replace("{countB}", String.valueOf(transactionToRouteB.size()));

        List<PaymentTransactionDTO> transactionToRouteC = transactions.stream()
                .limit(noOfTransactionsToRouteAcquirerC).collect(Collectors.toList());

        accessService.routeToAcquirer(transactionToRouteB, "AcquirerC");
        finalMessage = finalMessage.replace("{countC}", String.valueOf(transactionToRouteC.size()));

        logger.info(finalMessage);
    }

    @Override
    public String getMessageTemplate() {
        return messageTemplate;
    }
}
