package com.artichourey.ecommerce.orderservice.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.artichourey.ecommerce.events.PaymentRequestEvent;
import com.artichourey.ecommerce.events.StockFailedEvent;
import com.artichourey.ecommerce.events.StockReservedEvent;
import com.artichourey.ecommerce.orderservice.dto.OrderRequest;
import com.artichourey.ecommerce.orderservice.dto.OrderResponse;
import com.artichourey.ecommerce.orderservice.entity.Order;
import com.artichourey.ecommerce.orderservice.enums.OrderStatus;
import com.artichourey.ecommerce.orderservice.exception.OrderNotFoundException;
import com.artichourey.ecommerce.orderservice.mapper.OrderMapper;
import com.artichourey.ecommerce.orderservice.producer.OrderEventProducer;
import com.artichourey.ecommerce.orderservice.producer.PaymentEventProducer;
import com.artichourey.ecommerce.orderservice.repository.OrderRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderEventProducer orderEventProducer;
    private final PaymentEventProducer paymentEventProducer;
    
    private String logId() {
        return UUID.randomUUID().toString();
    }

    @Override
    @Transactional
    public OrderResponse placeOrder(OrderRequest orderRequest) {
    	String logId = logId();
        log.info("[{}] Initiating order placement | skuCode={}, quantity={}, userId={}",
                logId, orderRequest.getSkuCode(), orderRequest.getQuantity(), orderRequest.getUserId());

        // Generate order number
        String orderId = UUID.randomUUID().toString();

        // Map request to entity
        Order order = orderMapper.toEntity(orderRequest);
        order.setOrderNumber(orderId);
        order.setOrderStatus(OrderStatus.CREATED);
        order.setUserId(orderRequest.getUserId());

        // Save order
        Order savedOrder = orderRepository.save(order);
        log.info("[{}] Order created successfully | orderId={}, status={}", logId,
                orderId, savedOrder.getOrderStatus());

        // Send Kafka event
        orderEventProducer.sendOrderEvent(savedOrder);
        log.info("[{}] OrderPlacedEvent sent to Kafka | orderId={}", logId, orderId);
        

        return orderMapper.toResponse(savedOrder);
    }

    @Override
    public void confirmOrder(String orderId) {
    	 String logId = logId();
        log.info("[{}] Processing order confirmation | orderId={}", logId, orderId);

        Order order = getOrderOrThrow(orderId);

        if (order.getOrderStatus() == OrderStatus.CONFIRMED) {
            log.warn("[{}] Order already confirmed | orderId={}", logId, orderId);
            return; // Idempotent
        }

        order.setOrderStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        log.info("[{}] Order confirmed successfully | orderId={}", logId, orderId);
    }

    @Override
    public void failOrder(String orderId) {
    	 String logId = logId();
        log.info("[{}] Processing order failure | orderId={}", logId, orderId);

        Order order = getOrderOrThrow(orderId);

        if (order.getOrderStatus() == OrderStatus.FAILED) {
            log.warn("[{}] Order already marked as FAILED | orderId={}", logId, orderId);
            return; // Idempotent
        }

        order.setOrderStatus(OrderStatus.FAILED);
        orderRepository.save(order);

        log.info("[{}] Order marked as FAILED | orderId={}", logId, orderId);
    }

    private Order getOrderOrThrow(String orderId) {
        return orderRepository.findByOrderNumber(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found | orderId=" + orderId));
    }

    @Override
    public void handleStockReserved(StockReservedEvent event) {
    	 String logId = logId();
        log.info("[{}] Received STOCK_RESERVED event | orderId={}, skuCode={}, quantity={}",
                logId, event.getOrderId(), event.getSkuCode(), event.getQuantity());

        Order order = getOrderOrThrow(event.getOrderId());

        // Idempotency check
        if (order.getOrderStatus() == OrderStatus.STOCK_RESERVED
                || order.getOrderStatus() == OrderStatus.CONFIRMED) {
            log.warn("[{}] Stock already processed for orderId={} | currentStatus={}",
                    logId, event.getOrderId(), order.getOrderStatus());
            return;
        }

        order.setOrderStatus(OrderStatus.STOCK_RESERVED);
        orderRepository.save(order);
        log.info("[{}] Order status updated to STOCK_RESERVED | orderId={}", logId, event.getOrderId());

        // Trigger payment
        PaymentRequestEvent paymentEvent = new PaymentRequestEvent();
        paymentEvent.setOrderId(order.getOrderNumber());
        paymentEvent.setAmount(order.getPrice());
        paymentEvent.setUserId(String.valueOf(order.getUserId()));

        log.info("[{}] Triggering payment for orderId={}, amount={}",
                logId, order.getOrderNumber(), order.getPrice());

        paymentEventProducer.sendPaymentRequest(paymentEvent);
        log.info("[{}] PaymentRequestEvent sent to Kafka | orderId={}", logId, order.getOrderNumber());
    }

    @Override
    public void handleStockFailed(StockFailedEvent event) {
    	 String logId = logId();
        log.warn("[{}] Received STOCK_FAILED event | orderId={}, reason={}",
                logId, event.getOrderId(), event.getReason());

        Order order = getOrderOrThrow(event.getOrderId());

        // Idempotency check
        if (order.getOrderStatus() == OrderStatus.FAILED) {
            log.warn("[{}] Order already marked FAILED | orderId={}", logId, event.getOrderId());
            return;
        }

        order.setOrderStatus(OrderStatus.FAILED);
        orderRepository.save(order);
        log.info("[{}] Order marked as FAILED due to stock issue | orderId={}", logId, event.getOrderId());
    }
}