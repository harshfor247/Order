package com.example.Order.entity;

import com.example.Order.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false, unique = true)
    private String productName;

    @Column(nullable = false)
    private Double productPrice;

    @Column(name = "product_description", length = 1000)
    private String productDescription;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;
}
