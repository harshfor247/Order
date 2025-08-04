package com.example.Order.util;

import com.example.Order.dto.request.OrderRequest;
import com.example.Order.dto.request.OrderTransactionRequest;
import com.example.Order.dto.response.OrderResponse;
import com.example.Order.entity.Order;
import com.example.Order.entity.Product;
import com.example.Order.enums.OrderPayment;
import com.example.Order.enums.OrderStatus;
import com.example.Order.enums.ProductStatus;

public class CommonObjects {

    public static OrderRequest sampleOrderRequest(){
        return new OrderRequest(1L, "t-shirt", 200D, 1);
    }

    public static Product sampleProduct(){
        return new Product(1L, "t-shirt", 150D, "Nice t-shirt", 1, ProductStatus.ACTIVE);
    }

    public static Order sampleOrder(){
        return new Order(1L, 1L, "t-shirt", 200D, 1, 200D, OrderStatus.ACTIVE, OrderPayment.PENDING);
    }

    public static OrderResponse sampleOrderResponse(){
        return new OrderResponse(1L, 1L, "t-shirt", 200D, 1, 200D, OrderStatus.ACTIVE, OrderPayment.PENDING);
    }

    public static OrderTransactionRequest sampleOrderTransactionRequest(){
        return new OrderTransactionRequest(1L, 1L, 200D, OrderPayment.PENDING);
    }
}
