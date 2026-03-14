package com.artichourey.ecommerce.orderservice.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request body for placing an order")
public class OrderRequest {

	@Schema(description = "SKU code of the product", example = "SKU12345", required = true)
	private String skuCode;
	
	@Min(value=1,message="quantity must be atleast 1")
	@Schema(description = "Quantity to order", example = "2", required = true)
	private Integer quantity;
	
	@NotNull(message="price is mendatory")
	@Schema(description = "Price of the product", example = "499.99", required = true)
	private BigDecimal price;
}
