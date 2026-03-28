package com.artichourey.ecommerce.orderservice.entity;

import java.math.BigDecimal;

import com.artichourey.ecommerce.orderservice.enums.OrderStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private String orderNumber;
	private String skuCode;
	private Integer quantity;
	private BigDecimal price;
	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;
	
	private Long userId;

}
