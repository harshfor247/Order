package com.example.Order.kafka.consumer;

import com.example.Order.dto.response.TransactionResponse;
import com.example.Order.entity.Product;
import com.example.Order.enums.OrderPayment;
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
    public void listenPaymentResult(TransactionResponse paymentResponse) {
        Long orderId = paymentResponse.getOrderId();

        if (orderId == null) {
            System.err.println("ERROR: orderId is null in paymentResponse. Skipping processing.");
            return; // or throw exception if appropriate
        }

        PaymentStatus status = paymentResponse.getPaymentStatus();

        Optional<Order> optionalOrder = orderRepository.findById(orderId);

        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();

            if (status == PaymentStatus.SUCCESS) {
                order.setOrderPayment(OrderPayment.PURCHASED);
                orderRepository.save(order);

                // Update product quantity
                Integer updatedQuantity = order.getQuantity();

                Optional<Product> productOpt = productRepository.findByProductName(order.getProductName());

                if (productOpt.isPresent()) {
                    Product product = productOpt.get();
                    product.setQuantity(updatedQuantity);
                    productRepository.save(product);
                    if(product.getQuantity()==0){
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
