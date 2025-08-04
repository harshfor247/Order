package com.example.Order.service;

import com.example.Order.dto.request.OrderRequest;
import com.example.Order.dto.request.OrderUpdateRequest;
import com.example.Order.dto.response.OrderResponse;
import com.example.Order.dto.request.OrderTransactionRequest;
import com.example.Order.entity.Order;
import com.example.Order.entity.Product;
import com.example.Order.enums.OrderPayment;
import com.example.Order.enums.OrderStatus;
import com.example.Order.exceptions.CannotCreateOrderException;
import com.example.Order.exceptions.OrderNotFoundException;
import com.example.Order.exceptions.ProductNotFoundException;
import com.example.Order.kafka.producer.PaymentProducer;
import com.example.Order.repository.OrderRepository;
import com.example.Order.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PaymentProducer paymentProducer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OrderResponse createOrder(OrderRequest request) {

        // 1. Fetch product by product name
        Optional<Product> productOpt = productRepository.findByProductName(request.getProductName());

        if (productOpt.isEmpty()) {
            throw new ProductNotFoundException("Product not found!");
        }

        Product product = productOpt.get();
        Double availablePrice = product.getProductPrice();
        Integer availableQuantity = product.getQuantity();

        if(!request.getProductPrice().equals(availablePrice)){
            throw new ProductNotFoundException("Requested price is not same as the product price!");
        }

        // 2. Check product quantity
        if (availableQuantity == 0) {
            throw new CannotCreateOrderException("Cannot create order: The Product is INACTIVE");
        }
        if (request.getQuantity() > availableQuantity) {
            throw new CannotCreateOrderException("Cannot create order: insufficient product quantity!");
        }

        List<Order> orders = orderRepository.findByUserId(request.getUserId());

        boolean orderExists = orders.stream().anyMatch(order ->
                request.getUserId().equals(order.getUserId()) &&
                request.getProductName().equals(order.getProductName()) &&
                request.getQuantity().equals(order.getQuantity())
        );

        if (orderExists) {
            throw new CannotCreateOrderException("Cannot create Order as it already exists!");
        }

        Order order = objectMapper.convertValue(request, Order.class);

        Double amount = request.getProductPrice() * request.getQuantity();

        order.setAmount(amount);
        order.setOrderStatus(OrderStatus.ACTIVE);
        order.setOrderPayment(OrderPayment.PENDING);

        Order savedOrder = orderRepository.save(order);

        // 4. Build PaymentRequest (send to Kafka)
        OrderTransactionRequest transactionRequest = new OrderTransactionRequest();
        transactionRequest.setUserId(request.getUserId());
        transactionRequest.setOrderPayment(OrderPayment.PENDING);
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
            throw new OrderNotFoundException("Order not found!");
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
                .orElseThrow(() -> new OrderNotFoundException(
                        "Order not found!"));
    }


    public ResponseEntity<String> deactivateOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .map(order -> {
                    order.setOrderStatus(OrderStatus.INACTIVE);
                    orderRepository.save(order);
                    return ResponseEntity.ok("Order deactivated successfully");
                })
                .orElseThrow(() -> new OrderNotFoundException(
                        "Order not found!"));
    }
}
