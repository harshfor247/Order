package com.example.Order.entity;

import com.example.Order.enums.OrderPayment;
import com.example.Order.enums.OrderStatus;
import com.example.Order.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private Long userId;
    private String productName;
    private Double productPrice;
    private Integer quantity;

    @Column(name = "order_amount")
    private Double amount;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_payment")
    private OrderPayment orderPayment;
}
