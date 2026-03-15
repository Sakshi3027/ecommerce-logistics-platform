package com.ecommerce.order_service.service;

import com.ecommerce.order_service.dto.OrderItemRequest;
import com.ecommerce.order_service.dto.OrderRequest;
import com.ecommerce.order_service.dto.OrderResponse;
import com.ecommerce.order_service.entity.Order;
import com.ecommerce.order_service.enums.OrderStatus;
import com.ecommerce.order_service.kafka.OrderEventProducer;
import com.ecommerce.order_service.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEventProducer orderEventProducer;

    @InjectMocks
    private OrderService orderService;

    private Order mockOrder;

    @BeforeEach
    void setUp() {
        mockOrder = new Order();
        mockOrder.setId(1L);
        mockOrder.setOrderNumber("ORD-TEST001");
        mockOrder.setCustomerId(1L);
        mockOrder.setStatus(OrderStatus.PENDING);
        mockOrder.setTotalAmount(new BigDecimal("999.99"));
        mockOrder.setShippingAddress("123 Main St, New York, NY");
        mockOrder.setCreatedAt(LocalDateTime.now());
        mockOrder.setUpdatedAt(LocalDateTime.now());
        mockOrder.setItems(List.of());
    }

    @Test
    void getOrderById_WhenOrderExists_ReturnsOrder() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

        // Act
        OrderResponse response = orderService.getOrderById(1L);

        // Assert
        assertNotNull(response);
        assertEquals("ORD-TEST001", response.getOrderNumber());
        assertEquals(1L, response.getCustomerId());
        assertEquals(OrderStatus.PENDING, response.getStatus());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void getOrderById_WhenOrderNotFound_ThrowsException() {
        // Arrange
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.getOrderById(99L));
        assertTrue(exception.getMessage().contains("99"));
        verify(orderRepository, times(1)).findById(99L);
    }

    @Test
    void updateOrderStatus_WhenOrderExists_UpdatesStatus() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // Act
        OrderResponse response = orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED);

        // Assert
        assertNotNull(response);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderEventProducer, times(1)).publishOrderEvent(any());
    }

    @Test
    void cancelOrder_WhenOrderIsPending_CancelsSuccessfully() {
        // Arrange
        mockOrder.setStatus(OrderStatus.PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // Act
        OrderResponse response = orderService.cancelOrder(1L);

        // Assert
        assertNotNull(response);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_CalculatesTotalCorrectly() {
        // Arrange
        OrderRequest request = new OrderRequest();
        request.setCustomerId(1L);
        request.setShippingAddress("123 Main St");

        OrderItemRequest item1 = new OrderItemRequest();
        item1.setProductId(101L);
        item1.setProductName("iPhone 15 Pro");
        item1.setQuantity(2);
        item1.setUnitPrice(new BigDecimal("999.99"));

        OrderItemRequest item2 = new OrderItemRequest();
        item2.setProductId(102L);
        item2.setProductName("AirPods Pro");
        item2.setQuantity(1);
        item2.setUnitPrice(new BigDecimal("249.99"));

        request.setItems(List.of(item1, item2));

        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // Act
        OrderResponse response = orderService.createOrder(request);

        // Assert
        assertNotNull(response);
        verify(orderRepository, times(1)).save(any(Order.class));
        // Total should be (2 * 999.99) + (1 * 249.99) = 2249.97
        verify(orderEventProducer, times(1)).publishOrderEvent(any());
    }
}