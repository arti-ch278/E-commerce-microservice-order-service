package com.artichourey.ecommerce.orderservice.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.artichourey.ecommerce.events.PaymentRequestEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPaymentRequest(PaymentRequestEvent event) {
        log.info("Sending payment request event | orderId={}", event.getOrderId());
        kafkaTemplate.send("payment-request-topic", event.getOrderId(), event);
    }
}
