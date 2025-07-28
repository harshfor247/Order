package com.example.Order.controller;

import com.example.Order.dto.request.OrderRequest;
import com.example.Order.dto.request.OrderUpdateRequest;
import com.example.Order.dto.request.ProductRequest;
import com.example.Order.dto.request.ProductUpdateRequest;
import com.example.Order.dto.response.OrderResponse;
import com.example.Order.entity.Order;
import com.example.Order.entity.Product;
import com.example.Order.service.OrderService;
import com.example.Order.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getUserOrders(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    @PutMapping("/update")
    public ResponseEntity<OrderResponse> updateOrder(@RequestBody OrderUpdateRequest orderUpdateRequest) {
        return orderService.updateOrder(orderUpdateRequest);
    }

    @PutMapping("/orderStatus/{orderId}")
    public ResponseEntity<String> deactivateOrder(@PathVariable Long orderId) {
        return orderService.deactivateOrder(orderId);
    }
}
