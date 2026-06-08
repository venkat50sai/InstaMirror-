package com.app.order.serviceImpl;

import com.app.order.dto.OrderDto;
import com.app.order.entity.Order;
import com.app.order.exception.OrderException;
import com.app.order.repository.OrderRepository;
import com.app.order.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final  OrderRepository orderRepo;

    public OrderServiceImpl(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @Override
    public Map<String, Object> addOrder(OrderDto orderDto) {
        return ConstructMap(null);
    }

    @Override
    public Map<String, Object> getOrderById(int orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new OrderException("Order not found with id: " + orderId));
        return ConstructMap(order);
    }

    @Override
    public Map<String, Object> getOrdersByUserId(int userId) {
        List<Order> orders = orderRepo.findByUserId(userId);

        if (orders.isEmpty()) {
            throw new OrderException("No orders found for user id: " + userId);
        }
        return ConstructMap(orders);
    }

    @Override
    public Map<String, Object> deleteOrder(int orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new OrderException("Order not found with id: " + orderId));
        orderRepo.deleteById(orderId);
        return ConstructMap(order);
    }

    private Map<String, Object> ConstructMap(Object order) {
        Map<String, Object> Map = new HashMap<>();
        Map.put("success", true);
        Map.put("data", order);
        return Map;
    }
}
