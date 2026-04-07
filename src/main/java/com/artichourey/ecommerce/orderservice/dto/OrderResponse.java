package com.artichourey.ecommerce.orderservice.dto;

import com.artichourey.ecommerce.orderservice.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
	private String orderNumber;
	private OrderStatus orderStatus;
	private Long userId;

}
