package com.msproject.service;

import com.msproject.error.BusinessException;
import com.msproject.service.model.OrderModel;

public interface OrderService {
    public OrderModel createOrder(Integer userId, Integer itemId, Integer promoId,Integer amount) throws BusinessException;
}
