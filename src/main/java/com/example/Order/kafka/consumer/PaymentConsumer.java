package com.example.Order.kafka.consumer;

import com.example.Order.dto.response.TransactionResponse;
import com.example.Order.enums.OrderPayment;
import com.example.Order.enums.PaymentStatus;
import com.example.Order.repository.OrderRepository;
import com.example.Order.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentConsumer {

    private final OrderRepository orderRepository;

    @KafkaListener(
            topics = "payment-response",
            groupId = "order-group"
    )
    public void listenPaymentResult(TransactionResponse paymentResponse) {
        Long orderId = paymentResponse.getOrderId();
        PaymentStatus status = paymentResponse.getPaymentStatus();

        Optional<Order> optionalOrder = orderRepository.findById(orderId);

        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();

            if (status == PaymentStatus.SUCCESS) {
                order.setOrderPayment(OrderPayment.PURCHASED);
            } else {
                order.setOrderPayment(OrderPayment.CANCELLED);
            }

            orderRepository.save(order);
            System.out.println("Order " + orderId + " payment updated to: " + order.getOrderPayment());
        } else {
            System.err.println("Order not found for ID: " + orderId);
        }
    }
}
