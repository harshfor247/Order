package com.example.Order.dto.request;

import com.example.Order.enums.OrderPayment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderTransactionRequest {

    private Long userId;
    private Long orderId;
    private Double amount;
    private OrderPayment orderPayment;
}
