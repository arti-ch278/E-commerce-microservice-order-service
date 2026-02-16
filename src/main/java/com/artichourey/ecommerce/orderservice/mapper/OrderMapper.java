package com.artichourey.ecommerce.orderservice.mapper;

import org.springframework.stereotype.Component;

import com.artichourey.ecommerce.orderservice.dto.OrderRequest;
import com.artichourey.ecommerce.orderservice.dto.OrderResponse;
import com.artichourey.ecommerce.orderservice.entity.Order;

@Component
public class OrderMapper {
	
	public Order toEntity(OrderRequest request) {
		Order order= new Order();
		order.setPrice(request.getPrice());
		order.setQuantity(request.getQuantity());
	    order.setSkuCode(request.getSkuCode());
		
		return order;
		
		
	}
	public OrderResponse toResponse(Order order) {
		return new OrderResponse(order.getOrderNumber(),order.getOrderStatus());
		
	}
	
	}

