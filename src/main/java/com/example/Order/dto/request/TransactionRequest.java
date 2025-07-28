package com.example.Order.dto.request;

import com.example.Order.enums.PaymentMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionRequest {

    private Long userId;
    private Long orderId;
    private Double amount;
    private PaymentMode paymentMode;
}
