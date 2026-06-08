package com.app.order.service;


import com.app.order.dto.OrderDto;

import java.util.Map;

public interface OrderService {
    Map<String, Object> addOrder(OrderDto orderDto);
    Map<String, Object> getOrderById(int orderId);
    Map<String, Object> getOrdersByUserId(int userId);
    Map<String, Object> deleteOrder(int orderId);
}
