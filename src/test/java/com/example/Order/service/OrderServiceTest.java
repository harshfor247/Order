package com.example.Order.service;

import com.example.Order.dto.request.OrderRequest;
import com.example.Order.dto.request.OrderTransactionRequest;
import com.example.Order.dto.response.OrderResponse;
import com.example.Order.entity.Order;
import com.example.Order.entity.Product;
import com.example.Order.exceptions.CannotCreateOrderException;
import com.example.Order.exceptions.OrderNotFoundException;
import com.example.Order.exceptions.ProductNotFoundException;
import com.example.Order.kafka.producer.PaymentProducer;
import com.example.Order.repository.OrderRepository;
import com.example.Order.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.example.Order.util.CommonObjects.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PaymentProducer paymentProducer;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OrderService orderService;

    @Test
    void test_createOrder_ProductNotExist(){

        //given
        OrderRequest forOrderRequest = new OrderRequest(1L, "Car", 200D, 1);
        when(productRepository.findByProductName(forOrderRequest.getProductName())).thenReturn(Optional.empty());

        //when
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class,
                () -> orderService.createOrder(forOrderRequest));

        //then
        assertEquals("Product not found!", exception.getMessage());

        //verify
        verify(productRepository, never()).save(any());
    }

    @Test
    void test_createOrder_InvalidPrice(){

        //given
        OrderRequest forOrderRequest = sampleOrderRequest();
        Product forProduct = sampleProduct();
        when(productRepository.findByProductName(forOrderRequest.getProductName())).thenReturn(Optional.of(forProduct));

        //when
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class,
                () -> orderService.createOrder(forOrderRequest));

        //then
        assertEquals("Requested price is not same as the product price!", exception.getMessage());

        //verify
        verify(productRepository).findByProductName(forOrderRequest.getProductName());
        verify(orderRepository, never()).save(any());
        verify(paymentProducer, never()).sendPaymentRequest(any());
    }

    @Test
    void test_createOrder_InactiveProduct(){

        //given
        OrderRequest forOrderRequest = sampleOrderRequest();
        forOrderRequest.setQuantity(0);
        Product forProduct = sampleProduct();
        when(productRepository.findByProductName(forOrderRequest.getProductName())).thenReturn(Optional.of(forProduct));

        //when
        CannotCreateOrderException exception = assertThrows(CannotCreateOrderException.class,
                () -> orderService.createOrder(forOrderRequest));

        //then
        assertEquals("Cannot create order: The Product is INACTIVE", exception.getMessage());

        //verify
        verify(productRepository).findByProductName(forOrderRequest.getProductName());
        verify(orderRepository, never()).save(any());
        verify(paymentProducer, never()).sendPaymentRequest(any());
    }

    @Test
    void test_createOrder_InsufficientProduct(){

        //given
        OrderRequest forOrderRequest = sampleOrderRequest();
        forOrderRequest.setQuantity(5);
        Product forProduct = sampleProduct();
        when(productRepository.findByProductName(forOrderRequest.getProductName())).thenReturn(Optional.of(forProduct));

        //when
        CannotCreateOrderException exception = assertThrows(CannotCreateOrderException.class,
                () -> orderService.createOrder(forOrderRequest));

        //then
        assertEquals("Cannot create order: insufficient product quantity!", exception.getMessage());

        //verify
        verify(productRepository).findByProductName(forOrderRequest.getProductName());
        verify(orderRepository, never()).save(any());
        verify(paymentProducer, never()).sendPaymentRequest(any());
    }

    @Test
    void test_createOrder_AlreadyExists(){

        //given
        OrderRequest forOrderRequest = new OrderRequest(1L, "t-shirt", 150D, 1);
        Product forProduct = sampleProduct();
        Order forOrder = sampleOrder();
        when(productRepository.findByProductName(forOrderRequest.getProductName())).thenReturn(Optional.of(forProduct));
        when(orderRepository.findByUserId(forOrderRequest.getUserId())).thenReturn(List.of(forOrder));

        //when
        CannotCreateOrderException exception = assertThrows(CannotCreateOrderException.class,
                () -> orderService.createOrder(forOrderRequest));

        //then
        assertEquals("Cannot create Order as it already exists!", exception.getMessage());

        //verify
        verify(productRepository).findByProductName(forOrderRequest.getProductName());
        verify(orderRepository).findByUserId(forOrderRequest.getUserId());
        verify(paymentProducer, never()).sendPaymentRequest(any());
    }

    @Test
    void test_createOrder(){

        //given
        OrderRequest forOrderRequest = sampleOrderRequest();
        Product forProduct = sampleProduct();
        forProduct.setProductPrice(200D);
        Order forOrder = sampleOrder();
        OrderResponse forOrderResponse = sampleOrderResponse();
        when(productRepository.findByProductName(forOrderRequest.getProductName())).thenReturn(Optional.of(forProduct));
        when(orderRepository.findByUserId(forOrderRequest.getUserId())).thenReturn(List.of(forOrder));
        when(objectMapper.convertValue(forOrderRequest, Order.class)).thenReturn(forOrder);
        when(orderRepository.save(forOrder)).thenReturn(forOrder);
        when(objectMapper.convertValue(forOrder, OrderResponse.class))
                .thenReturn(forOrderResponse);

        //when
        OrderResponse actualResponse = orderService.createOrder(forOrderRequest);

        //then
        assertNotNull(actualResponse);
        assertEquals(forOrderResponse, actualResponse);

        //verify
        verify(productRepository).findByProductName(forOrderRequest.getProductName());
        verify(orderRepository).findByUserId(forOrderRequest.getUserId());
        verify(orderRepository).save(forOrder);
        verify(paymentProducer).sendPaymentRequest(any(OrderTransactionRequest.class));
        verify(objectMapper).convertValue(forOrder, OrderResponse.class);
    }

    @Test
    void test_getOrdersByUserId_NotExists(){

        //given
        Long userId = 1L;
        when(orderRepository.findByUserId(userId)).thenReturn(List.of());

        //when
        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class,
                () -> orderService.getOrdersByUserId(userId));

        //then
        assertEquals("Order not found!", exception.getMessage());

        //verify
        verify(orderRepository).findByUserId(userId);
    }
}
