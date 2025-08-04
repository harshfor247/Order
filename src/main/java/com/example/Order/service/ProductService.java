package com.example.Order.service;

import com.example.Order.dto.request.ProductUpdateRequest;
import com.example.Order.dto.response.ProductResponse;
import com.example.Order.entity.Product;
import com.example.Order.enums.ProductStatus;
import com.example.Order.exceptions.ProductAlreadyExistException;
import com.example.Order.exceptions.ProductNotFoundException;
import com.example.Order.repository.ProductRepository;
import com.example.Order.dto.request.ProductRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService{

    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProductResponse addProduct(ProductRequest productRequest) {

        Optional<Product> sameName = productRepository.findByProductName(productRequest.getProductName());

        if (sameName.isPresent()) {
            throw new ProductAlreadyExistException("Product already exist!");
        }
        // 1. Convert DTO to Entity
        Product product = objectMapper.convertValue(productRequest, Product.class);
        product.setProductStatus(ProductStatus.ACTIVE);

        // 2. Save to DB
        Product savedProduct = productRepository.save(product);

        // 4. Convert Entity to Response DTO
        ProductResponse productResponse = objectMapper.convertValue(savedProduct, ProductResponse.class);
        return productResponse;
    }

    public ProductResponse getProduct(Long productId) {
        Product product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found!"));

        return ProductResponse.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .productPrice(product.getProductPrice())
                .productDescription(product.getProductDescription())
                .quantity(product.getQuantity())
                .productStatus(product.getProductStatus())
                .build();
    }


    public ResponseEntity<ProductResponse> updateProduct(ProductUpdateRequest productUpdateRequest) {
        return productRepository.findByProductId(productUpdateRequest.getProductId())
                .map(existingProduct -> {
                    if (productUpdateRequest.getProductName() != null)
                        existingProduct.setProductName(productUpdateRequest.getProductName());

                    if (productUpdateRequest.getProductPrice() != null)
                        existingProduct.setProductPrice(productUpdateRequest.getProductPrice());

                    if (productUpdateRequest.getProductDescription() != null)
                        existingProduct.setProductDescription(productUpdateRequest.getProductDescription());

                    if (productUpdateRequest.getQuantity() != null){
                        existingProduct.setQuantity(productUpdateRequest.getQuantity());
                    }

                    existingProduct.setProductStatus(ProductStatus.ACTIVE);

                    Product updated = productRepository.save(existingProduct);

                    // Convert to OrderResponse
                    ProductResponse response = ProductResponse.builder()
                            .productId(updated.getProductId())
                            .productName(updated.getProductName())
                            .productPrice(updated.getProductPrice())
                            .productDescription(updated.getProductDescription())
                            .quantity(updated.getQuantity())
                            .productStatus(updated.getProductStatus())
                            .build();

                    return ResponseEntity.ok(response);
                })
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product not found!"));
    }

    public ResponseEntity<String> deactivateProduct(Long productId) {
        return productRepository.findByProductId(productId)
                .map(product -> {
                    product.setProductStatus(ProductStatus.INACTIVE);
                    productRepository.save(product);
                    return ResponseEntity.ok("Product deactivated successfully");
                })
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product not found!"));
    }
}

