package org.payment.controller;

import org.payment.service.RoutingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/routing")
public class RoutingController {
    private final RoutingService routingService;

    @Autowired
    public RoutingController(RoutingService routingService) {
        this.routingService = routingService;
    }

    @GetMapping("/run/transactionRouting")
    public ResponseEntity<String> routeTransactions() throws IOException {
        String response = routingService.routeTransactions();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
