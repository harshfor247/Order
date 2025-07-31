package com.example.Order.kafka.consumer;

import com.example.Order.dto.response.UpdatedOrderResponse;
import com.example.Order.entity.Product;
import com.example.Order.enums.OrderPayment;
import com.example.Order.enums.OrderStatus;
import com.example.Order.enums.PaymentStatus;
import com.example.Order.enums.ProductStatus;
import com.example.Order.repository.OrderRepository;
import com.example.Order.entity.Order;
import com.example.Order.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Data
@RequiredArgsConstructor
public class PaymentConsumer {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    @KafkaListener(
            topics = "payment-response",
            groupId = "order-group",
            containerFactory = "paymentResponseKafkaListenerFactory"
    )
    public void listenPaymentResult(UpdatedOrderResponse updatedOrderResponse) {
        Long orderId = updatedOrderResponse.getOrderId();

        if (orderId == null) {
            System.err.println("ERROR: orderId is null in paymentResponse. Skipping processing.");
            return; // or throw exception if appropriate
        }

        PaymentStatus status = updatedOrderResponse.getPaymentStatus();

        Optional<Order> optionalOrder = orderRepository.findById(orderId);

        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();

            if (status == PaymentStatus.SUCCESS) {
                order.setOrderPayment(OrderPayment.PURCHASED);
                order.setOrderStatus(OrderStatus.INACTIVE);
                String productName = order.getProductName();
                Integer purchasedQuantity = order.getQuantity();
                orderRepository.save(order);

                // Update product quantity
                Optional<Product> productOpt = productRepository.findByProductName(productName);

                if (productOpt.isPresent()) {
                    Product product = productOpt.get();
                    Integer updatedQuantity = product.getQuantity() - purchasedQuantity;
                    product.setQuantity(updatedQuantity);
                    productRepository.save(product);
                    if(updatedQuantity.equals(0)){
                        product.setProductStatus(ProductStatus.INACTIVE);
                    }
                }

                System.out.println("Order " + orderId + " payment updated to: " + order.getOrderPayment());
            }
        } else {
            System.err.println("Order not found for ID: " + orderId);
        }
    }
}
