package com.example.Order.config;

import com.example.Order.dto.response.TransactionResponse;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaOrderConsumerConfig {

    @Bean
    public ConsumerFactory<String, TransactionResponse> paymentResponseConsumerFactory() {
        JsonDeserializer<TransactionResponse> deserializer = new JsonDeserializer<>(TransactionResponse.class, false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(false);
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }


    @Bean(name = "paymentResponseKafkaListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, TransactionResponse> paymentResponseKafkaListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TransactionResponse> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(paymentResponseConsumerFactory());
        return factory;

    }
}
