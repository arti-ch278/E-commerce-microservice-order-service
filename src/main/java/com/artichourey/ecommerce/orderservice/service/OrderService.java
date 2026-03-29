package com.artichourey.ecommerce.orderservice.service;

import com.artichourey.ecommerce.events.StockFailedEvent;
import com.artichourey.ecommerce.events.StockReservedEvent;
import com.artichourey.ecommerce.orderservice.dto.OrderRequest;
import com.artichourey.ecommerce.orderservice.dto.OrderResponse;

public interface OrderService {
	
	OrderResponse placeOrder(OrderRequest orderRequest);
	public void confirmOrder(String orderId);
	public void failOrder(String orderId);
	public void handleStockReserved(StockReservedEvent event);
	public void handleStockFailed(StockFailedEvent event);
	
}
