package com.santhosh.adapter.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.santhosh.domain.dto.FoodOrder;
import com.santhosh.domain.port.input.PlaceOrderUsecase;
import com.santhosh.domain.port.input.TrackOrderUsecase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlaceOrderUsecase placeOrderUsecase;

    @MockBean
    private TrackOrderUsecase trackOrderUsecase;

    @Test
    void placeOrder_shouldReturn200WithOrderPlacedMessage() throws Exception {
        FoodOrder order = buildOrder("ORD-001", "santhosh", "Pizza Palace", "Margherita");

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(content().string("Order placed"));

        verify(placeOrderUsecase, times(1)).placeOrder(any(FoodOrder.class));
    }

    @Test
    void placeOrder_shouldForwardCorrectOrderToUsecase() throws Exception {
        FoodOrder order = buildOrder("ORD-002", "Kumar", "Burger House", "Cheeseburger");

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk());

        verify(placeOrderUsecase).placeOrder(argThat(o ->
                "ORD-002".equals(o.getOrderId()) &&
                "Kumar".equals(o.getCustomerName()) &&
                "Burger House".equals(o.getRestaurantName()) &&
                "Cheeseburger".equals(o.getItem())
        ));
    }

    @Test
    void trackOrder_shouldReturn200WithStatusPrefix() throws Exception {
        when(trackOrderUsecase.trackOrder("ORD-001")).thenReturn("ORDER PLACED");

        mockMvc.perform(get("/orders/track/ORD-001"))
                .andExpect(status().isOk())
                .andExpect(content().string("Status: ORDER PLACED"));

        verify(trackOrderUsecase, times(1)).trackOrder("ORD-001");
    }

    @Test
    void trackOrder_shouldPassOrderIdPathVariableToUsecase() throws Exception {
        when(trackOrderUsecase.trackOrder("ORD-999")).thenReturn("DELIVERED");

        mockMvc.perform(get("/orders/track/ORD-999"))
                .andExpect(status().isOk())
                .andExpect(content().string("Status: DELIVERED"));
    }

    @Test
    void trackOrder_shouldPropagateExceptionWhenOrderNotFound() {
        // No @ExceptionHandler exists — Spring 6 MockMvc re-throws unhandled exceptions.
        // This test documents that missing-order errors are currently unhandled.
        when(trackOrderUsecase.trackOrder("MISSING")).thenThrow(new NoSuchElementException("Order not found"));

        assertThatThrownBy(() -> mockMvc.perform(get("/orders/track/MISSING")))
                .hasCauseInstanceOf(NoSuchElementException.class);
    }

    private FoodOrder buildOrder(String orderId, String customer, String restaurant, String item) {
        FoodOrder order = new FoodOrder();
        order.setOrderId(orderId);
        order.setCustomerName(customer);
        order.setRestaurantName(restaurant);
        order.setItem(item);
        return order;
    }
}