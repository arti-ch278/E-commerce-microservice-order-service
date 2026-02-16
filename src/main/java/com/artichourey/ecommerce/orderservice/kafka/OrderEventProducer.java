package com.artichourey.ecommerce.orderservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.artichourey.ecommerce.orderservice.event.OrderPlacedEvent;

@Service
public class OrderEventProducer {
	
	private final Logger log=LoggerFactory.getLogger(OrderEventProducer.class);
	
	private final KafkaTemplate<String,OrderPlacedEvent> kafkaTemplate;
	
	public OrderEventProducer(KafkaTemplate<String,OrderPlacedEvent> kafkaTemplate) {
		this.kafkaTemplate=kafkaTemplate;
	}
	
	public void sendOrderEvent(OrderPlacedEvent event ) {
		
    log.info("Sending order event to Kafka: orderId={}, skuCode={}, quantity={}",
	                event.getOrderId(), event.getSkuCode(), event.getQuantity());
    
		kafkaTemplate.send("order",event.getOrderId().toString(),event);
	}

}
