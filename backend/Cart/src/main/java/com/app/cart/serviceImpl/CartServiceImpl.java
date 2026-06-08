package com.app.cart.serviceImpl;

import com.app.cart.dto.CartDto;
import com.app.cart.dto.ItemDto;
import com.app.cart.dto.ProductDto;
import com.app.cart.entity.Cart;
import com.app.cart.entity.Items;
import com.app.cart.exception.CartException;
import com.app.cart.repository.CartRepository;
import com.app.cart.repository.ItemRepository;
import com.app.cart.service.CartService;
import com.app.cart.service.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepo;

    @Autowired
    private ItemRepository itemRepo;

    @Autowired
    private ProductService productService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CartDto getCart(int id) throws CartException {

        Cart cart = cartRepo.findByUserId(id)
                .orElseThrow(() -> new CartException("Cart not found"));

        CartDto dto = modelMapper.map(cart, CartDto.class);
        List<ItemDto> itemDtos = new ArrayList<>();
        double grandTotal = 0.0;

        for (Items item : cart.getItems()) {
            ProductDto product = productService.getProductfromProductService(item.getProductId());

            if (product == null) {
                continue;
            }

            ItemDto itemDto = modelMapper.map(item, ItemDto.class);

            double price = product.getPrice();
            double total = price * item.getQuantity();

            itemDto.setPrice(price);
            itemDto.setTotal(total);

            grandTotal += total;
            itemDtos.add(itemDto);
        }

        dto.setItems(itemDtos);
        dto.setGrandTotal(grandTotal);

        return dto;
    }

    @Override
    public Map<String, Object> addCart(int id, ItemDto itemDto) {
        if (itemDto.getQuantity() <= 0) {
            throw new CartException("Quantity must be greater than 0");
        }
        Cart cart = cartRepo.findByUserId(id)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(id);
                    return cartRepo.save(newCart);
                });

        Optional<Items> existingItem = cart.getItems()
                .stream()
                .filter(i -> i.getProductId() == itemDto.getProductId())
                .findFirst();
        ProductDto productDto = productService.getProductfromProductService(itemDto.getProductId());
        if (productDto == null) {
            throw new CartException("Product not found");
        }
        if (existingItem.isPresent()) {
            Items item = existingItem.get();
            int quantity = item.getQuantity() + itemDto.getQuantity();
            if(productDto.getStock()< quantity)
                throw new CartException("Not enough stocks");
            item.setQuantity(quantity);
        } else {
            if(productDto.getStock() < itemDto.getQuantity())
                throw new CartException("Not enough stocks");
            Items newItem = new Items();
            newItem.setProductId(itemDto.getProductId());
            newItem.setQuantity(itemDto.getQuantity());
            newItem.setCart(cart);
            cart.getItems().add(newItem);
        }

        cartRepo.save(cart);

        return buildCartResponse("Item added to cart successfully", cart);
    }

    @Override
    public Map<String, Object> updateCart(int id, ItemDto itemDto) {
        if (itemDto.getQuantity() <= 0) {
            throw new CartException("Quantity must be greater than 0");
        }
        Cart cart = cartRepo.findByUserId(id)
                .orElseThrow(() -> new CartException("Cart not found"));

        Items item = cart.getItems()
                .stream()
                .filter(i -> i.getProductId() == itemDto.getProductId())
                .findFirst()
                .orElseThrow(() -> new CartException("Item not found in cart"));
        ProductDto productDto = productService.getProductfromProductService(itemDto.getProductId());
        if (productDto == null) {
            throw new CartException("Product not found");
        }
        if(productDto.getStock()< itemDto.getQuantity())
            throw new CartException("Not enough stocks");
        item.setQuantity(itemDto.getQuantity());

        cartRepo.save(cart);

        return buildCartResponse("Cart Updated successfully", cart);
    }

    @Override
    public Map<String, Object> getCartCount(int userId) {

        Cart cart = cartRepo.findByUserId(userId)
                .orElse(null);

        if (cart == null || cart.getItems() == null) {
            return Map.of(
                    "count", 0,
                    "success", true
            );
        }

        int totalCount = cart.getItems()
                .stream()
                .mapToInt(Items::getQuantity)
                .sum();

        return Map.of(
                "count", totalCount,
                "success", true
        );
    }

    @Override
    public Map<String, Object> clearCart(int id) {

        Cart cart = cartRepo.findByUserId(id)
                .orElseThrow(() -> new CartException("Cart not found"));
        cart.getItems().clear();
        cartRepo.save(cart);

        return buildCartResponse("Cart cleared successfully", cart);
    }

    private Map<String, Object> buildCartResponse(String message, Cart cart) {

        CartDto dto = convertToDto(cart);

        int totalCount = dto.getItems()
                .stream()
                .mapToInt(ItemDto::getQuantity)
                .sum();

        return Map.of(
                "message", message,
                "success", true,
                "cart", dto,
                "count", totalCount
        );
    }

    @Override
    public Map<String, Object> removeItem(int userId, int productId) {

        Cart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new CartException("Cart not found"));

        Items item = cart.getItems()
                .stream()
                .filter(i -> i.getProductId() == productId)
                .findFirst()
                .orElseThrow(() -> new CartException("Item not found"));

        cart.getItems().remove(item);
        cartRepo.save(cart);

        return buildCartResponse("Item removed successfully", cart);
    }

    private CartDto convertToDto(Cart cart) {

        CartDto dto = new CartDto();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUserId());

        List<ItemDto> itemDtos = new ArrayList<>();
        double grandTotal = 0.0;

        for (Items item : cart.getItems()) {

            ProductDto product = productService.getProductfromProductService(item.getProductId());
            if (product == null) continue;

            ItemDto itemDto = modelMapper.map(item, ItemDto.class);

            double price = product.getPrice();
            double total = price * item.getQuantity();

            itemDto.setPrice(price);
            itemDto.setTotal(total);

            grandTotal += total;
            itemDtos.add(itemDto);
        }

        dto.setItems(itemDtos);
        dto.setGrandTotal(grandTotal);

        return dto;
    }

}
