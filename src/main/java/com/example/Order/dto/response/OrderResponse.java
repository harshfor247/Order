package com.example.Order.dto.response;

import com.example.Order.enums.OrderPayment;
import com.example.Order.enums.OrderStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {

    private Long orderId;
    private Long userId;
    private String productName;
    private Double productPrice;
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    private OrderPayment orderPayment;
}
