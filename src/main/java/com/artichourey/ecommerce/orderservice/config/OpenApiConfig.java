package com.artichourey.ecommerce.orderservice.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {
	
	@Bean
	public OpenAPI orderServiceAPI() {
	    return new OpenAPI()
	            .info(new Info()
	                    .title("Order Service API")
	                    .description("Order management APIs")
	                    .version("1.0"));
	}

}
