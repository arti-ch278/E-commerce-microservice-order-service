package com.artichourey.ecommerce.orderservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.artichourey.ecommerce.orderservice.entity.Order;
import com.artichourey.ecommerce.orderservice.enums.OrderStatus;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void testSaveOrder() {
        Order order = new Order();
        order.setOrderNumber("ORD123");
        order.setSkuCode("SKU123");
        order.setQuantity(2);
        order.setPrice(BigDecimal.valueOf(499.99));
        order.setOrderStatus(OrderStatus.CREATED);
        order.setUserId(1L);

        Order savedOrder = orderRepository.save(order);

        assertNotNull(savedOrder.getId());
        assertEquals("ORD123", savedOrder.getOrderNumber());
    }

    @Test
    void testFindByOrderNumber() {
        Order order = new Order(null, "ORD124", "SKU456", 1, BigDecimal.valueOf(299.99), OrderStatus.CREATED, 2L);
        orderRepository.save(order);

        Optional<Order> foundOrder = orderRepository.findByOrderNumber("ORD124");
        assertTrue(foundOrder.isPresent());
        assertEquals("SKU456", foundOrder.get().getSkuCode());
    }
}