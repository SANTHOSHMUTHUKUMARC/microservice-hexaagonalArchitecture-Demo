package com.santhosh.domain.port.input;

import com.santhosh.domain.dto.FoodOrder;

public interface PlaceOrderUsecase {

    void placeOrder(FoodOrder order);
}
