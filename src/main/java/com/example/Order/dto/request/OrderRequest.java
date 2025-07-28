package com.example.Order.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {

    private Long userId;
    private String productName;
    private Double productPrice;
    private Integer quantity;
}
