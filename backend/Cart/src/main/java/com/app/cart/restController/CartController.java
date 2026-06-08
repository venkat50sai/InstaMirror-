package com.app.cart.restController;

import com.app.cart.dto.CartDto;
import com.app.cart.dto.ItemDto;
import com.app.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/getCart")
    public ResponseEntity<CartDto> getCart(@RequestHeader("X-User-Id") Integer id){
        CartDto cart = cartService.getCart(id);
        return new ResponseEntity<>(cart, HttpStatus.OK);
    }

    @PostMapping("/addCart")
    public ResponseEntity<Map<String, Object>> addCart(@RequestHeader("X-User-Id") Integer id, @RequestBody ItemDto itemDto){
        Map<String, Object> res = cartService.addCart(id, itemDto);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("/updateCart")
    public ResponseEntity<Map<String, Object>> updateCart(@RequestHeader("X-User-Id") Integer id, @RequestBody ItemDto itemDto) {
        Map<String, Object> res = cartService.updateCart(id,itemDto);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getCount(@RequestHeader("X-User-Id") Integer id){
        Map<String, Object> res = cartService.getCartCount(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("/clearCart")
    public ResponseEntity<Map<String, Object>> clearCart(@RequestHeader("X-User-Id") Integer id) {
        Map<String, Object> res = cartService.clearCart(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("removeItem")
    public ResponseEntity<Map<String, Object>> removeItem(@RequestHeader("X-User-Id") Integer id, @RequestParam("productId") int productId) {
        Map<String, Object> res = cartService.removeItem(id, productId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
