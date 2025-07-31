package com.example.Order.dto.request;

import com.example.Order.enums.OrderPayment;
import com.example.Order.enums.OrderStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateRequest {

    private Long orderId;

    private Long userId;
    private String productName;
    private Double productPrice;
    private Integer quantity;
}
