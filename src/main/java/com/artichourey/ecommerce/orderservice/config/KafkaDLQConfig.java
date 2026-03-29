package com.artichourey.ecommerce.orderservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaDLQConfig {

    @Bean
    public NewTopic paymentSuccessDLQ() {
        return TopicBuilder.name("payment-success-topic.DLT")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic paymentFailedDLQ() {
        return TopicBuilder.name("payment-failed-topic.DLT")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic stockReservedDLQ() {
        return TopicBuilder.name("stock-reserved-topic.DLT")
                .partitions(3)
                .replicas(1)
                .build();	
    }

    @Bean
    public NewTopic stockFailedDLQ() {
        return TopicBuilder.name("stock-failed-topic.DLT")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
