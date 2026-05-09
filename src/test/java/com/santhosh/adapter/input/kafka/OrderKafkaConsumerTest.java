package com.santhosh.adapter.input.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.santhosh.domain.dto.FoodOrder;
import com.santhosh.domain.port.input.PlaceOrderUsecase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderKafkaConsumerTest {

    @Mock
    private PlaceOrderUsecase placeOrderUseCase;

    @InjectMocks
    private OrderKafkaConsumer kafkaConsumer;

    @BeforeEach
    void setUp() {
        // placeOrderUseCase is injected by @InjectMocks via field injection
    }

    @Test
    void consume_shouldDeserializeJsonAndInvokePlaceOrder() throws Exception {
        String message = "{\"orderId\":\"ORD-001\",\"customerName\":\"santhosh\",\"restaurantName\":\"Pizza Palace\",\"item\":\"Margherita\"}";

        kafkaConsumer.consume(message);

        verify(placeOrderUseCase, times(1)).placeOrder(any(FoodOrder.class));
    }

    @Test
    void consume_shouldDeserializeAllFieldsCorrectly() throws Exception {
        String message = "{\"orderId\":\"ORD-002\",\"customerName\":\"Kumar\",\"restaurantName\":\"Burger House\",\"item\":\"Cheeseburger\"}";

        kafkaConsumer.consume(message);

        verify(placeOrderUseCase).placeOrder(argThat(order ->
                "ORD-002".equals(order.getOrderId()) &&
                "Kumar".equals(order.getCustomerName()) &&
                "Burger House".equals(order.getRestaurantName()) &&
                "Cheeseburger".equals(order.getItem())
        ));
    }

    @Test
    void consume_shouldHandleOrderWithStatusField() throws Exception {
        String message = "{\"orderId\":\"ORD-003\",\"customerName\":\"Carol\",\"restaurantName\":\"Sushi Bar\",\"item\":\"California Roll\",\"status\":\"PENDING\"}";

        kafkaConsumer.consume(message);

        verify(placeOrderUseCase, times(1)).placeOrder(any(FoodOrder.class));
    }

    @Test
    void consume_shouldThrowOnInvalidJson() {
        String invalidMessage = "not-valid-json";

        assertThatThrownBy(() -> kafkaConsumer.consume(invalidMessage))
                .isInstanceOf(JsonProcessingException.class);
    }

    @Test
    void consume_shouldThrowOnEmptyString() {
        assertThatThrownBy(() -> kafkaConsumer.consume(""))
                .isInstanceOf(JsonProcessingException.class);
    }
}