package com.artichourey.ecommerce.orderservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.artichourey.ecommerce.orderservice.dto.OrderRequest;
import com.artichourey.ecommerce.orderservice.dto.OrderResponse;
import com.artichourey.ecommerce.orderservice.enums.OrderStatus;
import com.artichourey.ecommerce.orderservice.service.OrderService;


@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void testPlaceOrder_Success() throws Exception {
        OrderRequest request = new OrderRequest("SKU123", 2, BigDecimal.valueOf(499.99), 1L);
        OrderResponse response = new OrderResponse("ORD123", OrderStatus.CREATED, 1L);

        when(orderService.placeOrder(any(OrderRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"skuCode\":\"SKU123\",\"quantity\":2,\"price\":499.99,\"userId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderNumber").value("ORD123"))
                .andExpect(jsonPath("$.orderStatus").value("CREATED"));
    }

    @Test
    void testPlaceOrder_InvalidQuantity() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"skuCode\":\"SKU123\",\"quantity\":0,\"price\":499.99,\"userId\":1}"))
                .andExpect(status().isBadRequest());
    }
}