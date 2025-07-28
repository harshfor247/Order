package com.example.Order.config;

import com.example.Order.dto.request.TransactionRequest;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaOrderProducerConfig {

    @Bean
    public ProducerFactory<String, TransactionRequest> paymentRequestProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaProducerFactory<>(
                config,
                new StringSerializer(),
                new JsonSerializer<>()
        );
    }

    @Bean
    public KafkaTemplate<String, TransactionRequest> paymentKafkaTemplate() {
        return new KafkaTemplate<>(paymentRequestProducerFactory());
    }
}
