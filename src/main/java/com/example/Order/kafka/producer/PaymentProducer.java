package com.example.Order.kafka.producer;

import com.example.Order.dto.request.TransactionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentProducer {

    private final KafkaTemplate<String, TransactionRequest> kafkaTemplate;

    public void sendPaymentRequest(TransactionRequest transactionRequest){
        kafkaTemplate.send("payment-request", transactionRequest);
    }
}