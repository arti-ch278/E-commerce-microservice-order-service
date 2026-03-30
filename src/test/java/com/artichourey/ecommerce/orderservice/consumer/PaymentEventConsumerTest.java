package com.artichourey.ecommerce.orderservice.consumer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.artichourey.ecommerce.events.PaymentCompletedEvent;
import com.artichourey.ecommerce.events.PaymentFailedEvent;
import com.artichourey.ecommerce.events.StockFailedEvent;
import com.artichourey.ecommerce.events.StockReservedEvent;
import com.artichourey.ecommerce.orderservice.service.OrderService;

@ExtendWith(MockitoExtension.class)
class PaymentEventConsumerTest {

    @InjectMocks
    private PaymentEventConsumer consumer;

    @Mock
    private OrderService orderService;

    @Test
    void testHandlePaymentSuccess_Success() {
        PaymentCompletedEvent event = new PaymentCompletedEvent(
                "EVT1", "ORD1", "PAY123", "SUCCESS", "USER1", LocalDateTime.now()
        );

        consumer.handlePaymentSuccess(event);

        verify(orderService).confirmOrder("ORD1");
    }

    @Test
    void testHandlePaymentSuccess_Exception() {
        PaymentCompletedEvent event = new PaymentCompletedEvent(
                "EVT1", "ORD1", "PAY123", "SUCCESS", "USER1", LocalDateTime.now()
        );

        doThrow(new RuntimeException("DB error"))
                .when(orderService).confirmOrder("ORD1");

        assertThrows(RuntimeException.class,
                () -> consumer.handlePaymentSuccess(event));

        verify(orderService).confirmOrder("ORD1");
    }

    @Test
    void testHandlePaymentFailed_Success() {
        PaymentFailedEvent event = new PaymentFailedEvent(
                "EVT2", "ORD1", "PAY123", "FAILED", "USER1", LocalDateTime.now()
        );

        consumer.handlePaymentFailed(event);

        verify(orderService).failOrder("ORD1");
    }

    @Test
    void testHandlePaymentFailed_Exception() {
        PaymentFailedEvent event = new PaymentFailedEvent(
                "EVT2", "ORD1", "PAY123", "FAILED", "USER1", LocalDateTime.now()
        );

        doThrow(new RuntimeException("DB error"))
                .when(orderService).failOrder("ORD1");

        assertThrows(RuntimeException.class,
                () -> consumer.handlePaymentFailed(event));

        verify(orderService).failOrder("ORD1");
    }

    @Test
    void testHandleStockReserved() {
        StockReservedEvent event = new StockReservedEvent(
                "ORD1", "SKU123", 2, "USER1"
        );

        consumer.handleStockReserved(event);

        verify(orderService).handleStockReserved(event);
    }

    @Test
    void testHandleStockFailed() {
        StockFailedEvent event = new StockFailedEvent(
                "ORD1", "Out of stock", "USER1"
        );

        consumer.handleStockFailed(event);

        verify(orderService).handleStockFailed(event);
    }
}
