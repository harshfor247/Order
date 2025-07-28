package com.example.Order.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {
    private String productName;
    private Double productPrice;
    private String productDescription;
    private Integer quantity;
}
