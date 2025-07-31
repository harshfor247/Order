package com.example.Order.service;

import com.example.Order.dto.request.ProductUpdateRequest;
import com.example.Order.dto.response.ProductResponse;
import com.example.Order.entity.Product;
import com.example.Order.enums.ProductStatus;
import com.example.Order.repository.ProductRepository;
import com.example.Order.dto.request.ProductRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService{

    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProductResponse addProduct(ProductRequest productRequest) {

        Optional<Product> sameName = productRepository.findByProductName(productRequest.getProductName());

        if (sameName.isPresent()) {
            throw new RuntimeException("Same product already exist");
        }
        // 1. Convert DTO to Entity
        Product product = objectMapper.convertValue(productRequest, Product.class);
        product.setProductStatus(ProductStatus.ACTIVE);

        // 2. Save to DB
        Product savedProduct = productRepository.save(product);

        // 4. Convert Entity to Response DTO
        ProductResponse productResponse = objectMapper.convertValue(savedProduct, ProductResponse.class);
        return productResponse;
//        ProductResponse productResponse = objectMapper.convertValue(productRequest, ProductResponse.class);
//        productResponse.setProductStatus(ProductStatus.ACTIVE);
//        return productRepository.save(productResponse);
    }

    public ResponseEntity<ProductResponse> getProduct(Long productId) {
        return productRepository.findById(productId)
                .map(product -> {
                    ProductResponse productResponse = objectMapper.convertValue(product, ProductResponse.class);
                    return ResponseEntity.ok(productResponse);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<ProductResponse> updateProduct(ProductUpdateRequest productUpdateRequest) {
        return productRepository.findById(productUpdateRequest.getProductId())
                .map(existingProduct -> {
                    if (productUpdateRequest.getProductName() != null)
                        existingProduct.setProductName(productUpdateRequest.getProductName());

                    if (productUpdateRequest.getProductPrice() != null)
                        existingProduct.setProductPrice(productUpdateRequest.getProductPrice());

                    if (productUpdateRequest.getProductDescription() != null)
                        existingProduct.setProductDescription(productUpdateRequest.getProductDescription());

                    if (productUpdateRequest.getQuantity() != null)
                        existingProduct.setQuantity(productUpdateRequest.getQuantity());

                    Product updated = productRepository.save(existingProduct);

                    // Convert to UserResponse
                    ProductResponse response = objectMapper.convertValue(updated, ProductResponse.class);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<String> deactivateProduct(Long productId) {
        return productRepository.findById(productId)
                .map(product -> {
                    product.setProductStatus(ProductStatus.INACTIVE);
                    productRepository.save(product);
                    return ResponseEntity.ok("Product deactivated successfully");
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

