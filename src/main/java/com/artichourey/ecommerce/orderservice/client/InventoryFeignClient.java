package com.artichourey.ecommerce.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.artichourey.ecommerce.orderservice.dto.InventoryResponse;

@FeignClient(name="inventory-service")
public interface InventoryFeignClient {
	
	@GetMapping("/api/inventory/{skuCode}")
	InventoryResponse inStock(@PathVariable String skuCode) ;
		
	

}
