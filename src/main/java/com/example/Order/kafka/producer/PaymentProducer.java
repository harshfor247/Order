package com.example.Order.kafka.producer;

import com.example.Order.dto.request.OrderTransactionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentProducer {

    private final KafkaTemplate<String, OrderTransactionRequest> kafkaTemplate;

    public void sendPaymentRequest(OrderTransactionRequest orderTransactionRequest){
        kafkaTemplate.send("payment-request", orderTransactionRequest);
    }
}