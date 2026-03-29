package com.artichourey.ecommerce.orderservice.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.artichourey.ecommerce.events.PaymentCompletedEvent;
import com.artichourey.ecommerce.events.PaymentFailedEvent;
import com.artichourey.ecommerce.events.StockFailedEvent;
import com.artichourey.ecommerce.events.StockReservedEvent;
import com.artichourey.ecommerce.orderservice.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final OrderService orderService;

   
    // Handle payment success
    
    @KafkaListener(topics = "payment-success-topic", groupId = "order-group")
    public void handlePaymentSuccess(PaymentCompletedEvent event) {
        log.info("Received PAYMENT_SUCCESS event | orderId={}, paymentId={}",
                event.getOrderId(), event.getPaymentId());

        try {
            orderService.confirmOrder(event.getOrderId());
            log.info("Order confirmed successfully | orderId={}", event.getOrderId());
        } catch (Exception e) {
            log.error("Error confirming order | orderId={}", event.getOrderId(), e);
            // Re-throw to trigger retry/backoff and DLQ
            throw e;
        }
    }

    
    //Handle payment failure

    @KafkaListener(topics = "payment-failed-topic", groupId = "order-group")
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.warn("Received PAYMENT_FAILED event | orderId={}, paymentId={}",
                event.getOrderId(), event.getPaymentId());

        try {
            orderService.failOrder(event.getOrderId());
            log.info("Order marked as FAILED | orderId={}", event.getOrderId());
        } catch (Exception e) {
            log.error("Error marking order as FAILED | orderId={}", event.getOrderId(), e);
            // Re-throw to trigger retry/backoff and DLQ
            throw e;
        }
    }
    @KafkaListener(topics = "stock-reserved-topic", groupId = "order-group")
    public void handleStockReserved(StockReservedEvent event) {
        orderService.handleStockReserved(event);
    }
    
    @KafkaListener(topics = "stock-failed-topic", groupId = "order-group")
    public void handleStockFailed(StockFailedEvent event) {
        orderService.handleStockFailed(event);
    }

}