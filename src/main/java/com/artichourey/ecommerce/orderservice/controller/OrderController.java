package com.artichourey.ecommerce.orderservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.artichourey.ecommerce.orderservice.dto.OrderRequest;
import com.artichourey.ecommerce.orderservice.dto.OrderResponse;
import com.artichourey.ecommerce.orderservice.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
	
	private final OrderService orderService;
	
	@PostMapping("/")
	public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody OrderRequest orderRequest){
		
		return ResponseEntity.status(HttpStatus.CREATED).body(orderService.placeOrder(orderRequest));
		
		
	}

}
