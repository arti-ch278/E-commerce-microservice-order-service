package com.artichourey.ecommerce.orderservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.artichourey.ecommerce.orderservice.client.InventoryFeignClient;
import com.artichourey.ecommerce.orderservice.dto.InventoryResponse;
import com.artichourey.ecommerce.orderservice.dto.OrderRequest;
import com.artichourey.ecommerce.orderservice.dto.OrderResponse;
import com.artichourey.ecommerce.orderservice.entity.Order;
import com.artichourey.ecommerce.orderservice.event.OrderPlacedEvent;
import com.artichourey.ecommerce.orderservice.kafka.OrderEventProducer;
import com.artichourey.ecommerce.orderservice.mapper.OrderMapper;
import com.artichourey.ecommerce.orderservice.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {
	
	@Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private InventoryFeignClient inventoryFeignClient;

    @Mock
    private OrderEventProducer orderEventProducer;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void placeOrder_ShouldCreateOrder_WhenInStock() {

        OrderRequest request = new OrderRequest(
                "SKU123",
                2,
                new BigDecimal("500")
        );

        InventoryResponse inventory =
                new InventoryResponse("SKU123", 10, true);

        Order order = new Order();
        order.setSkuCode("SKU123");
        order.setQuantity(2);

        when(inventoryFeignClient.inStock("SKU123"))
                .thenReturn(inventory);

        when(orderMapper.toEntity(request))
                .thenReturn(order);

        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);

        when(orderMapper.toResponse(any(Order.class)))
                .thenReturn(new OrderResponse("ORD1", "CREATED"));

        OrderResponse response = orderService.placeOrder(request);

        assertEquals("CREATED", response.getOrderStatus());

        verify(orderRepository).save(any(Order.class));
        verify(orderEventProducer).sendOrderEvent(any());
    }

    @Test
    void placeOrder_ShouldThrowException_WhenOutOfStock() {

        OrderRequest request = new OrderRequest(
                "SKU123",
                2,
                new BigDecimal("500")
        );

        InventoryResponse inventory =
                new InventoryResponse("SKU123", 0, false);

        when(inventoryFeignClient.inStock("SKU123"))
                .thenReturn(inventory);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> orderService.placeOrder(request)
        );

        assertEquals("product is out of stock", exception.getMessage());

        verify(orderRepository, never()).save(any());
        verify(orderEventProducer, never()).sendOrderEvent(any());
    }
    
    @Test
    void placeOrder_ShouldSendKafkaEvent() {

        OrderRequest request =
                new OrderRequest("SKU123", 2, new BigDecimal("500"));

        InventoryResponse inventory =
                new InventoryResponse("SKU123", 10, true);

        when(inventoryFeignClient.inStock("SKU123"))
                .thenReturn(inventory);

        when(orderMapper.toEntity(request))
                .thenReturn(new Order());

        when(orderRepository.save(any()))
                .thenReturn(new Order());

        orderService.placeOrder(request);

        verify(orderEventProducer, times(1))
                .sendOrderEvent(any(OrderPlacedEvent.class));
    }
}


