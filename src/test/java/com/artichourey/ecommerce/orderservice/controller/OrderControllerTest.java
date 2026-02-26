package com.artichourey.ecommerce.orderservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.artichourey.ecommerce.orderservice.dto.OrderRequest;
import com.artichourey.ecommerce.orderservice.dto.OrderResponse;
import com.artichourey.ecommerce.orderservice.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {
	
	    @Autowired
	    private MockMvc mockMvc;

	    @MockBean
	    private OrderService orderService;

	    @Autowired
	    private ObjectMapper objectMapper;

	    @Test
	    void placeOrder_ShouldReturnCreated() throws Exception {

	        OrderRequest request = new OrderRequest(
	                "SKU123",
	                2,
	                new BigDecimal("1000")
	        );

	        OrderResponse response = new OrderResponse(
	                "ORD-123",
	                "CREATED"
	        );

	        when(orderService.placeOrder(any(OrderRequest.class)))
	                .thenReturn(response);

	        mockMvc.perform(post("/api/orders/")
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(objectMapper.writeValueAsString(request)))
	                .andExpect(status().isCreated())
	                .andExpect(jsonPath("$.orderStatus").value("CREATED"));
	    }

	    @Test
	    void placeOrder_ShouldReturnBadRequest_WhenQuantityInvalid() throws Exception {

	        OrderRequest request = new OrderRequest(
	                "SKU123",
	                0, // invalid
	                new BigDecimal("1000")
	        );

	        mockMvc.perform(post("/api/orders/")
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(objectMapper.writeValueAsString(request)))
	                .andExpect(status().isBadRequest());
	    }

}
