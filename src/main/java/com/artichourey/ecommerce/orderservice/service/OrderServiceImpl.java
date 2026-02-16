package com.artichourey.ecommerce.orderservice.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.artichourey.ecommerce.orderservice.client.InventoryFeignClient;
import com.artichourey.ecommerce.orderservice.dto.InventoryResponse;
import com.artichourey.ecommerce.orderservice.dto.OrderRequest;
import com.artichourey.ecommerce.orderservice.dto.OrderResponse;
import com.artichourey.ecommerce.orderservice.entity.Order;
import com.artichourey.ecommerce.orderservice.event.OrderPlacedEvent;
import com.artichourey.ecommerce.orderservice.kafka.OrderEventProducer;
import com.artichourey.ecommerce.orderservice.mapper.OrderMapper;
import com.artichourey.ecommerce.orderservice.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
	
	private final Logger log= LoggerFactory.getLogger(OrderServiceImpl.class);

	private final OrderRepository orderRepository;
	private final OrderMapper orderMapper;
	private final InventoryFeignClient inventoryFeignClient;
	private final OrderEventProducer orderEventProducer;
	
	@Override
	public OrderResponse placeOrder(OrderRequest orderRequest) {
		log.info("Placing order for SKU: {}, quantity: {}", orderRequest.getSkuCode(), orderRequest.getQuantity());
		InventoryResponse inventory=inventoryFeignClient.inStock(orderRequest.getSkuCode());
		 log.info("Inventory check for SKU {}: inStock={}", orderRequest.getSkuCode(), inventory.isInStock());
		if(!inventory.isInStock()) {
			log.warn("Product {} is out of stock", orderRequest.getSkuCode());
			throw new RuntimeException("product is out of stock");
		}
		Order order =orderMapper.toEntity(orderRequest);
		String orderId=UUID.randomUUID().toString();
		order.setOrderNumber(orderId);
		order.setOrderStatus("CREATED");
		Order saved=orderRepository.save(order);
		log.info("Order created successfully: orderId={}", orderId);
		OrderPlacedEvent event= new OrderPlacedEvent();
		event.setEventId(UUID.randomUUID().toString());
		event.setOrderId(orderId);
		event.setSkuCode(orderRequest.getSkuCode());
		event.setQuantity(orderRequest.getQuantity());
		event.setEventTime(LocalDateTime.now());
		orderEventProducer.sendOrderEvent(event);
		
		return orderMapper.toResponse(saved);
	}

}
