package com.app.cart.service;

import com.app.cart.dto.CartDto;
import com.app.cart.dto.ItemDto;
import com.app.cart.exception.CartException;

import java.util.Map;

public interface CartService {
    CartDto getCart(int id) throws CartException;
    Map<String, Object> addCart(int id, ItemDto itemDto);
    Map<String, Object> updateCart(int id, ItemDto itemDto);
    Map<String, Object> clearCart(int id);
    Map<String, Object> getCartCount(int userId);
    Map<String, Object> removeItem(int userId, int postId);
}
