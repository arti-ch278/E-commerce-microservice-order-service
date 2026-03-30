package com.artichourey.ecommerce.orderservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.artichourey.ecommerce.events.PaymentRequestEvent;
import com.artichourey.ecommerce.events.StockFailedEvent;
import com.artichourey.ecommerce.events.StockReservedEvent;
import com.artichourey.ecommerce.orderservice.dto.OrderRequest;
import com.artichourey.ecommerce.orderservice.dto.OrderResponse;
import com.artichourey.ecommerce.orderservice.entity.Order;
import com.artichourey.ecommerce.orderservice.enums.OrderStatus;
import com.artichourey.ecommerce.orderservice.mapper.OrderMapper;
import com.artichourey.ecommerce.orderservice.producer.OrderEventProducer;
import com.artichourey.ecommerce.orderservice.producer.PaymentEventProducer;
import com.artichourey.ecommerce.orderservice.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderEventProducer orderEventProducer;

    @Mock
    private PaymentEventProducer paymentEventProducer;
    
    private Order order;

    @BeforeEach
    void setup() {
        order = new Order();
        order.setOrderNumber("ORD123");
        order.setSkuCode("SKU123");
        order.setQuantity(2);
        order.setPrice(BigDecimal.valueOf(499.99));
        order.setOrderStatus(OrderStatus.CREATED);
        order.setUserId(1L);
    }


    @Test
    void testPlaceOrder() {
        OrderRequest request = new OrderRequest("SKU123", 2, BigDecimal.valueOf(499.99), 1L);
        Order orderEntity = new Order();
        OrderResponse responseDto = new OrderResponse("ORD123", OrderStatus.CREATED, 1L);

        when(orderMapper.toEntity(request)).thenReturn(orderEntity);
        when(orderRepository.save(any(Order.class))).thenReturn(orderEntity);
        when(orderMapper.toResponse(orderEntity)).thenReturn(responseDto);

        OrderResponse result = orderService.placeOrder(request);

        assertEquals("ORD123", result.getOrderNumber());
        assertEquals(OrderStatus.CREATED, result.getOrderStatus());
        verify(orderRepository).save(orderEntity);
        verify(orderEventProducer).sendOrderEvent(orderEntity);
    }

    @Test
    void testConfirmOrderAlreadyConfirmed() {
        Order order = new Order();
        order.setOrderNumber("ORD123");
        order.setOrderStatus(OrderStatus.CONFIRMED);
        when(orderRepository.findByOrderNumber("ORD123")).thenReturn(Optional.of(order));

        orderService.confirmOrder("ORD123");

        // Idempotent: should not call save again
        verify(orderRepository, times(0)).save(any());
    }
    
    @Test
    void testFailOrder_Success() {
        when(orderRepository.findByOrderNumber("ORD123")).thenReturn(Optional.of(order));
        orderService.failOrder("ORD123");

        assertEquals(OrderStatus.FAILED, order.getOrderStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void testFailOrder_AlreadyFailed() {
        order.setOrderStatus(OrderStatus.FAILED);
        when(orderRepository.findByOrderNumber("ORD123")).thenReturn(Optional.of(order));

        orderService.failOrder("ORD123");

        // Should not save again (idempotent)
        verify(orderRepository, times(0)).save(any());
    }
    
    @Test
    void testHandleStockReserved_NewOrder() {
        StockReservedEvent event = new StockReservedEvent("ORD123", "SKU123", 2 ,"1");

        when(orderRepository.findByOrderNumber("ORD123")).thenReturn(Optional.of(order));

        orderService.handleStockReserved(event);

        assertEquals(OrderStatus.STOCK_RESERVED, order.getOrderStatus());
        verify(orderRepository).save(order);
        verify(paymentEventProducer).sendPaymentRequest(any(PaymentRequestEvent.class));
    }

    @Test
    void testHandleStockReserved_AlreadyReservedOrConfirmed() {
        order.setOrderStatus(OrderStatus.CONFIRMED);
        StockReservedEvent event = new StockReservedEvent("ORD123", "SKU123", 2 ,"1");

        when(orderRepository.findByOrderNumber("ORD123")).thenReturn(Optional.of(order));

        orderService.handleStockReserved(event);

        // Should not save again or send payment (idempotent)
        verify(orderRepository, times(0)).save(any());
        verify(paymentEventProducer, times(0)).sendPaymentRequest(any());
    }
    @Test
    void testHandleStockFailed_NewFailure() {
        StockFailedEvent event = new StockFailedEvent("ORD123", "SKU123", "Out of stock");

        when(orderRepository.findByOrderNumber("ORD123")).thenReturn(Optional.of(order));

        orderService.handleStockFailed(event);

        assertEquals(OrderStatus.FAILED, order.getOrderStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void testHandleStockFailed_AlreadyFailed() {
        order.setOrderStatus(OrderStatus.FAILED);
        StockFailedEvent event = new StockFailedEvent("ORD123", "SKU123", "Out of stock");

        when(orderRepository.findByOrderNumber("ORD123")).thenReturn(Optional.of(order));

        orderService.handleStockFailed(event);

        // Should not save again (idempotent)
        verify(orderRepository, times(0)).save(any());
    }
}