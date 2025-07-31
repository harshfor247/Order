package com.example.Order.dto.request;

import com.example.Order.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateRequest {

    private Long productId;

    private String productName;

    private Double productPrice;

    private String productDescription;

    private Integer quantity;
}
