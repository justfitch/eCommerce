package com.ecommerce.order.service;

import com.ecommerce.order.clients.ProductServiceClient;
import com.ecommerce.order.clients.UserServiceClient;
import com.ecommerce.order.dto.CartItemRequest;
import com.ecommerce.order.dto.ProductResponse;
import com.ecommerce.order.dto.UserResponse;
import com.ecommerce.order.model.CartItem;
import com.ecommerce.order.repository.CartItemRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;

    @CircuitBreaker(name = "product-service")
    public boolean addToCart(String userId, CartItemRequest request) {
      try {
        ProductResponse productResponse = productServiceClient.getProductById(request.getProductId());

        if (productResponse == null || productResponse.getStockQuantity() < request.getQuantity()) {
          return false;
        }

        UserResponse userResponse = userServiceClient.getUserById(userId);
        if (userResponse == null) {
          return false;
        }
      } catch (HttpClientErrorException e) {
        return false;
      }

      CartItem existingCartItem = cartItemRepository.findByUserIdAndProductId(userId, request.getProductId());
      if (existingCartItem != null) {
          // Update the quantity
          existingCartItem.setQuantity(existingCartItem.getQuantity() + request.getQuantity());
          existingCartItem.setPrice(BigDecimal.valueOf(1000));
          cartItemRepository.save(existingCartItem);
      } else {
          // Create new cart item
         CartItem cartItem = new CartItem();
         cartItem.setUserId(userId);
         cartItem.setProductId(Long.valueOf(request.getProductId()));
         cartItem.setQuantity(request.getQuantity());
         cartItem.setPrice(BigDecimal.valueOf(1000));
         cartItemRepository.save(cartItem);
      }
      return true;
    }

    public boolean deleteItemFromCart(String userId, String productId) {
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId);
        if (cartItem != null) {
            cartItemRepository.deleteByUserIdAndProductId(userId, productId);
            return true;
        }
        return false;
    }

    public List<CartItem> getCart(String userId) {
        return cartItemRepository.findByUserId(userId);
    }

    public void clearCart(String userId) {
         cartItemRepository.deleteByUserId(userId);
    }
}
