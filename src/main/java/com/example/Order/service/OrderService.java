package com.example.Order.service;

import com.example.Order.dto.request.OrderRequest;
import com.example.Order.dto.request.OrderUpdateRequest;
import com.example.Order.dto.response.OrderResponse;
import com.example.Order.dto.request.TransactionRequest;
import com.example.Order.entity.Order;
import com.example.Order.enums.OrderPayment;
import com.example.Order.enums.OrderStatus;
import com.example.Order.kafka.producer.PaymentProducer;
import com.example.Order.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentProducer paymentProducer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OrderResponse createOrder(OrderRequest request) {

        Order order = objectMapper.convertValue(request, Order.class);

        order.setOrderStatus(OrderStatus.ACTIVE);
        order.setOrderPayment(OrderPayment.PENDING);

        Order savedOrder = orderRepository.save(order);

        Double amount = order.getProductPrice() * order.getQuantity();

        // 4. Build PaymentRequest (send to Kafka)
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setOrderId(savedOrder.getOrderId());
        transactionRequest.setAmount(amount);

        // 5. Send to Kafka
        paymentProducer.sendPaymentRequest(transactionRequest);

        // 6. Return mapped response
        return objectMapper.convertValue(savedOrder, OrderResponse.class);
    }


    public List<OrderResponse> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);

        if (orders == null || orders.isEmpty()) {
            throw new NoSuchElementException("No orders found for user ID: " + userId);
        }

        return orders.stream()
                .map(order -> OrderResponse.builder()
                        .orderId(order.getOrderId())
                        .userId(order.getUserId())
                        .productName(order.getProductName())
                        .productPrice(order.getProductPrice())
                        .quantity(order.getQuantity())
                        .orderStatus(order.getOrderStatus())
                        .orderPayment(order.getOrderPayment())
                        .build())
                .collect(Collectors.toList());
    }


    public ResponseEntity<OrderResponse> updateOrder(OrderUpdateRequest orderUpdateRequest) {
        return orderRepository.findById(orderUpdateRequest.getOrderId())
                .map(existingOrder -> {
                    if (orderUpdateRequest.getUserId() != null)
                        existingOrder.setUserId(orderUpdateRequest.getUserId());

                    if (orderUpdateRequest.getProductName() != null)
                        existingOrder.setProductName(orderUpdateRequest.getProductName());

                    if (orderUpdateRequest.getProductPrice() != null)
                        existingOrder.setProductPrice(orderUpdateRequest.getProductPrice());

                    if (orderUpdateRequest.getQuantity() != null)
                        existingOrder.setQuantity(orderUpdateRequest.getQuantity());

                    Order updated = orderRepository.save(existingOrder);

                    // Convert to OrderResponse
                    OrderResponse response = OrderResponse.builder()
                            .orderId(updated.getOrderId())
                            .userId(updated.getUserId())
                            .productName(updated.getProductName())
                            .productPrice(updated.getProductPrice())
                            .quantity(updated.getQuantity())
                            .orderStatus(updated.getOrderStatus())
                            .orderPayment(updated.getOrderPayment())
                            .build();

                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    public ResponseEntity<String> deactivateOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    order.setOrderStatus(OrderStatus.INACTIVE);
                    orderRepository.save(order);
                    return ResponseEntity.ok("Order deactivated successfully");
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
