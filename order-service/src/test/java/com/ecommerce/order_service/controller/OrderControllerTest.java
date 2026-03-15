package com.ecommerce.order_service.controller;

import com.ecommerce.order_service.dto.OrderItemRequest;
import com.ecommerce.order_service.dto.OrderRequest;
import com.ecommerce.order_service.dto.OrderResponse;
import com.ecommerce.order_service.enums.OrderStatus;
import com.ecommerce.order_service.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    private OrderResponse mockOrderResponse() {
        return OrderResponse.builder()
                .id(1L)
                .orderNumber("ORD-TEST001")
                .customerId(1L)
                .status(OrderStatus.PENDING)
                .totalAmount(new BigDecimal("999.99"))
                .shippingAddress("123 Main St, New York, NY")
                .createdAt(LocalDateTime.now())
                .items(List.of())
                .build();
    }

    @Test
    void createOrder_ReturnsCreatedOrder() throws Exception {
        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(101L);
        item.setProductName("iPhone 15 Pro");
        item.setQuantity(1);
        item.setUnitPrice(new BigDecimal("999.99"));

        OrderRequest request = new OrderRequest();
        request.setCustomerId(1L);
        request.setShippingAddress("123 Main St, New York, NY");
        request.setItems(List.of(item));

        when(orderService.createOrder(any())).thenReturn(mockOrderResponse());

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderNumber").value("ORD-TEST001"))
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void getOrderById_ReturnsOrder() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(mockOrderResponse());

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumber").value("ORD-TEST001"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void getOrderById_WhenNotFound_Returns404() throws Exception {
        when(orderService.getOrderById(99L))
                .thenThrow(new RuntimeException("Order not found with id: 99"));

        mockMvc.perform(get("/api/orders/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order not found with id: 99"));
    }

    @Test
    void updateOrderStatus_ReturnsUpdatedOrder() throws Exception {
        OrderResponse updated = mockOrderResponse();
        when(orderService.updateOrderStatus(eq(1L), eq(OrderStatus.CONFIRMED)))
                .thenReturn(updated);

        mockMvc.perform(put("/api/orders/1/status")
                .param("status", "CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumber").value("ORD-TEST001"));
    }

    @Test
    void cancelOrder_ReturnsCancelledOrder() throws Exception {
        when(orderService.cancelOrder(1L)).thenReturn(mockOrderResponse());

        mockMvc.perform(put("/api/orders/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumber").value("ORD-TEST001"));
    }

    @Test
    void createOrder_WithMissingFields_Returns400() throws Exception {
        OrderRequest invalidRequest = new OrderRequest();
        // Missing required fields

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}