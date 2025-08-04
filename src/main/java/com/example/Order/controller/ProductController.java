package com.example.Order.controller;

import com.example.Order.dto.request.ProductRequest;
import com.example.Order.dto.request.ProductUpdateRequest;
import com.example.Order.dto.response.ProductResponse;
import com.example.Order.entity.Product;
import com.example.Order.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/add")
    public ResponseEntity<ProductResponse> addProduct(@RequestBody ProductRequest productRequest) {
        ProductResponse productResponse = productService.addProduct(productRequest);
        return ResponseEntity.ok(productResponse);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProduct(productId));
    }

    @PutMapping("/update")
    public ResponseEntity<ProductResponse> updateProduct(@RequestBody ProductUpdateRequest productUpdateRequest) {
        return productService.updateProduct(productUpdateRequest);
    }

    @PutMapping("/deactivate/{productId}")
    public ResponseEntity<String> deactivateProduct(@PathVariable Long productId) {
        return productService.deactivateProduct(productId);
    }
}
