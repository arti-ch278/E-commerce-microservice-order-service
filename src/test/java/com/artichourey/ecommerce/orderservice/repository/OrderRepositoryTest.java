package com.artichourey.ecommerce.orderservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.artichourey.ecommerce.orderservice.entity.Order;

@DataJpaTest

public class OrderRepositoryTest {
	@Autowired
    private OrderRepository orderRepository;

    @Test
    void saveOrder_ShouldPersistData() {

        Order order = new Order();
        order.setOrderNumber("ORD1");
        order.setSkuCode("SKU123");
        order.setQuantity(2);
        order.setPrice(new BigDecimal("500"));
        order.setOrderStatus("CREATED");

        Order saved = orderRepository.save(order);

        assertNotNull(saved.getId());
        assertEquals("SKU123", saved.getSkuCode());
    }
}