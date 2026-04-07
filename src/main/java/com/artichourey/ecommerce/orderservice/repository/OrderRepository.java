package com.artichourey.ecommerce.orderservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.artichourey.ecommerce.orderservice.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
	
	
	Optional<Order> findByOrderNumber(String orderNumber);

}
