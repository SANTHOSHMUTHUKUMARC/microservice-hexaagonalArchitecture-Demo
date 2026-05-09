package com.santhosh.adapter.output;

import com.santhosh.adapter.output.entity.OrderEntity;
import com.santhosh.adapter.output.repository.SpringDataOrderRepository;
import com.santhosh.domain.dto.FoodOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaOrderRepositoryTest {

    @Mock
    private SpringDataOrderRepository repository;

    @InjectMocks
    private JpaOrderRepository jpaOrderRepository;

    private FoodOrder sampleOrder;

    @BeforeEach
    void setUp() {
        sampleOrder = new FoodOrder();
        sampleOrder.setOrderId("ORD-001");
        sampleOrder.setCustomerName("santhosh");
        sampleOrder.setRestaurantName("Pizza Palace");
        sampleOrder.setItem("Margherita");
        sampleOrder.setStatus("ORDER PLACED");
    }

    @Test
    void saveOrder_shouldDelegateToSpringDataRepository() {
        jpaOrderRepository.saveOrder(sampleOrder);

        verify(repository, times(1)).save(any(OrderEntity.class));
    }

    @Test
    void saveOrder_shouldMapAllDomainFieldsToEntity() {
        jpaOrderRepository.saveOrder(sampleOrder);

        verify(repository).save(argThat(entity ->
                "ORD-001".equals(entity.getOrderId()) &&
                "santhosh".equals(entity.getCustomerName()) &&
                "Pizza Palace".equals(entity.getRestaurantName()) &&
                "Margherita".equals(entity.getItem()) &&
                "ORDER PLACED".equals(entity.getStatus())
        ));
    }

    @Test
    void findById_shouldReturnStatusWhenOrderExists() {
        OrderEntity entity = buildEntity("ORD-001", "ORDER PLACED");
        when(repository.findById("ORD-001")).thenReturn(Optional.of(entity));

        String status = jpaOrderRepository.findById("ORD-001");

        assertThat(status).isEqualTo("ORDER PLACED");
    }

    @Test
    void findById_shouldReturnCorrectStatusForDifferentStates() {
        OrderEntity entity = buildEntity("ORD-002", "DELIVERED");
        when(repository.findById("ORD-002")).thenReturn(Optional.of(entity));

        String status = jpaOrderRepository.findById("ORD-002");

        assertThat(status).isEqualTo("DELIVERED");
    }

    @Test
    void findById_shouldThrowWhenOrderDoesNotExist() {
        when(repository.findById("MISSING")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jpaOrderRepository.findById("MISSING"))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void findById_shouldQueryRepositoryWithProvidedId() {
        OrderEntity entity = buildEntity("ORD-003", "ORDER PLACED");
        when(repository.findById("ORD-003")).thenReturn(Optional.of(entity));

        jpaOrderRepository.findById("ORD-003");

        verify(repository, times(1)).findById("ORD-003");
    }

    private OrderEntity buildEntity(String orderId, String status) {
        OrderEntity entity = new OrderEntity();
        entity.setOrderId(orderId);
        entity.setCustomerName("santhosh");
        entity.setRestaurantName("Pizza Palace");
        entity.setItem("Margherita");
        entity.setStatus(status);
        return entity;
    }
}