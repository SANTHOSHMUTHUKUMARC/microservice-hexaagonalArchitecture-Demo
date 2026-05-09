package com.santhosh.domain.service;

import com.santhosh.domain.dto.FoodOrder;
import com.santhosh.domain.port.output.OrderRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepositoryPort orderRepositoryPort;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepositoryPort);
    }

    @Test
    void placeOrder_shouldSetStatusToOrderPlaced() {
        FoodOrder order = buildOrder("ORD-001", "santhosh", "Pizza Palace", "Margherita");

        orderService.placeOrder(order);

        assertThat(order.getStatus()).isEqualTo("ORDER PLACED");
    }

    @Test
    void placeOrder_shouldDelegateToRepository() {
        FoodOrder order = buildOrder("ORD-001", "santhosh", "Pizza Palace", "Margherita");

        orderService.placeOrder(order);

        verify(orderRepositoryPort, times(1)).saveOrder(order);
    }

    @Test
    void placeOrder_shouldOverwriteAnyPreviousStatus() {
        FoodOrder order = buildOrder("ORD-002", "Kumar", "Burger House", "Cheeseburger");
        order.setStatus("DRAFT");

        orderService.placeOrder(order);

        assertThat(order.getStatus()).isEqualTo("ORDER PLACED");
    }

    @Test
    void trackOrder_shouldReturnStatusFromRepository() {
        when(orderRepositoryPort.findById("ORD-001")).thenReturn("ORDER PLACED");

        String status = orderService.trackOrder("ORD-001");

        assertThat(status).isEqualTo("ORDER PLACED");
        verify(orderRepositoryPort, times(1)).findById("ORD-001");
    }

    @Test
    void trackOrder_shouldPropagateExceptionWhenOrderNotFound() {
        when(orderRepositoryPort.findById("MISSING")).thenThrow(new NoSuchElementException());

        assertThatThrownBy(() -> orderService.trackOrder("MISSING"))
                .isInstanceOf(NoSuchElementException.class);
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