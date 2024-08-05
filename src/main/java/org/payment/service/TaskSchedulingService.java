package org.payment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TaskSchedulingService {
    private static final Logger logger = LoggerFactory.getLogger(TaskSchedulingService.class);
    private final RestTemplate restTemplate;


    public TaskSchedulingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void callTransactionRouting() {
        logger.info("Transaction routing schedular started .....!");
        String url ="http://localhost:8080/api/routing/run/transactionRouting";
        String response = restTemplate.getForObject(url, String.class);
        logger.info("Transaction routing schedular Response :"+ response);
    }
}
