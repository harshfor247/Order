package com.example.Order.dto.response;

import com.example.Order.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {

    private Long productId;

    private String productName;

    private Double productPrice;

    private String productDescription;

    private Integer quantity;

    private ProductStatus productStatus;
}
