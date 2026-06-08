package com.app.order.restController;


import com.app.order.dto.OrderDto;
import com.app.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderServ;

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addOrder(@RequestBody OrderDto orderDto) {
        return new ResponseEntity<>(orderServ.addOrder(orderDto), HttpStatus.OK);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable int orderId) {
        return new ResponseEntity<>(orderServ.getOrderById(orderId), HttpStatus.OK);
    }

    @GetMapping("/orders/{userId}")
    public ResponseEntity<Map<String, Object>> getOrdersByUserId(@PathVariable int userId) {
        return new ResponseEntity<>(orderServ.getOrdersByUserId(userId), HttpStatus.OK);
    }

     @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<Map<String, Object>> deleteOrder(@PathVariable int orderId) {
         return new ResponseEntity<>(orderServ.deleteOrder(orderId), HttpStatus.OK);
     }

}
