package org.payment.controller;

import org.junit.jupiter.api.Test;
import org.payment.service.RoutingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoutingController.class)
public class RoutingControllerTest {

    @MockBean
    private RoutingService routingServiceMock;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testRoutingTransactions() throws Exception {
        when(routingServiceMock.routeTransactions()).thenReturn("Routing Successfull!");

        mockMvc.perform(get("/api/routing/run/transactionRouting").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().string("Routing Successfull!"));

        verify(routingServiceMock, times(1)).routeTransactions();
    }
}
