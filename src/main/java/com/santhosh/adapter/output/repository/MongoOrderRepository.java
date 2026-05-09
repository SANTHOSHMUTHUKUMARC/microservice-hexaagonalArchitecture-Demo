package com.santhosh.adapter.output.repository;

import com.santhosh.domain.dto.FoodOrder;
import com.santhosh.domain.port.output.OrderRepositoryPort;

public class MongoOrderRepository implements OrderRepositoryPort {

    // inject mongo repository

    @Override
    public void saveOrder(FoodOrder order) {

    }

    @Override
    public String findById(String orderId) {
        return "";
    }
}
