package com.artichourey.ecommerce.orderservice.service;

import com.artichourey.ecommerce.orderservice.dto.OrderRequest;
import com.artichourey.ecommerce.orderservice.dto.OrderResponse;

public interface OrderService {
	
	OrderResponse placeOrder(OrderRequest orderRequest);

}
