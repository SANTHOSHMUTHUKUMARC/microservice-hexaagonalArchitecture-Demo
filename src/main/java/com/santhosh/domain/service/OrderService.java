package com.santhosh.domain.service;

import com.santhosh.domain.dto.FoodOrder;
import com.santhosh.domain.port.input.PlaceOrderUsecase;
import com.santhosh.domain.port.input.TrackOrderUsecase;
import com.santhosh.domain.port.output.OrderRepositoryPort;

public class OrderService implements PlaceOrderUsecase, TrackOrderUsecase {

    private final OrderRepositoryPort orderRepositoryPort;

    public OrderService(OrderRepositoryPort orderRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    public void placeOrder(FoodOrder order) {
        order.setStatus("ORDER PLACED");
        System.out.println("--CORE EXECUTED WITH INPUT PORT--");
        // Here you would typically call a repository to save the order
        orderRepositoryPort.saveOrder(order);
    }

    @Override
    public String trackOrder(String orderId) {
        System.out.println("--CORE EXECUTED WITH INPUT PORT--");
        return orderRepositoryPort.findById(orderId);
    }
}
