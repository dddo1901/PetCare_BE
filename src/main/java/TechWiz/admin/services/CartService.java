package TechWiz.admin.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import TechWiz.admin.models.CartItem;
import TechWiz.admin.models.Product;
import TechWiz.admin.models.dto.AddToCartRequest;
import TechWiz.admin.models.dto.CartItemResponse;
import TechWiz.admin.repositories.CartItemRepository;
import TechWiz.admin.repositories.ProductRepository;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private ProductRepository productRepository;

    public String addToCart(AddToCartRequest request, Long userId) {
        // Check if product exists and is available
        Optional<Product> productOpt = productRepository.findById(request.getProductId());
        if (productOpt.isEmpty()) {
            return "Product not found";
        }

        Product product = productOpt.get();
        if (!product.getIsActive() || !product.getIsAvailable()) {
            return "Product is not available";
        }

        // Check if item already exists in cart
        CartItem existingItem = cartItemRepository.findByUserIdAndProductId(userId, request.getProductId());
        
        if (existingItem != null) {
            // Update existing cart item
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            
            existingItem.setQuantity(newQuantity);
            existingItem.setUpdatedAt(LocalDateTime.now());
            cartItemRepository.save(existingItem);
            return "Cart updated successfully";
        } else {
            // Create new cart item
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(request.getProductId());
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItem.setAddedAt(LocalDateTime.now());
            cartItem.setUpdatedAt(LocalDateTime.now());
            cartItemRepository.save(cartItem);
            return "Product added to cart successfully";
        }
    }

    public String updateCartItem(Long userId, Long productId, Integer quantity) {
        if (quantity <= 0) {
            return removeFromCart(userId, productId);
        }

        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId);
        if (cartItem == null) {
            return "Item not found in cart";
        }

        // Check product availability
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty() || !productOpt.get().getIsActive() || !productOpt.get().getIsAvailable()) {
            return "Product is no longer available";
        }

        cartItem.setQuantity(quantity);
        cartItem.setUpdatedAt(LocalDateTime.now());
        cartItemRepository.save(cartItem);
        return "Cart updated successfully";
    }

    public String removeFromCart(Long userId, Long productId) {
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId);
        if (cartItem != null) {
            cartItemRepository.delete(cartItem);
            return "Item removed from cart";
        }
        return "Item not found in cart";
    }

    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    public List<CartItemResponse> getCartItems(Long userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserIdOrderByAddedAtDesc(userId);
        
        return cartItems.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public Double getCartTotal(Long userId) {
        Double total = cartItemRepository.calculateCartTotal(userId);
        return total != null ? total : 0.0;
    }

    public Integer getTotalQuantity(Long userId) {
        Integer total = cartItemRepository.calculateTotalQuantity(userId);
        return total != null ? total : 0;
    }

    public Long getCartItemCount(Long userId) {
        return cartItemRepository.countByUserId(userId);
    }

    // Validate cart before checkout
    public String validateCart(Long userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserIdOrderByAddedAtDesc(userId);
        
        if (cartItems.isEmpty()) {
            return "Cart is empty";
        }

        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            
            if (!product.getIsActive() || !product.getIsAvailable()) {
                return "Product '" + product.getName() + "' is no longer available";
            }
        }
        
        return "Cart is valid";
    }

    // Clean up unavailable items from cart
    public List<String> cleanupUnavailableItems(Long userId) {
        List<CartItem> unavailableItems = cartItemRepository.findUnavailableCartItems(userId);
        List<String> removedItems = unavailableItems.stream()
            .map(item -> item.getProduct().getName())
            .collect(Collectors.toList());
            
        cartItemRepository.deleteAll(unavailableItems);
        return removedItems;
    }

    // For order processing - no stock reduction needed with simplified model  
    public boolean processCartForOrder(Long userId) {
        // First validate all items are still available
        String validation = validateCart(userId);
        if (!"Cart is valid".equals(validation)) {
            return false;
        }

        // Clear the cart after successful order
        clearCart(userId);
        return true;
    }

    private CartItemResponse convertToResponse(CartItem cartItem) {
        CartItemResponse response = new CartItemResponse();
        response.setId(cartItem.getId());
        response.setProductId(cartItem.getProductId());
        response.setProductName(cartItem.getProduct().getName());
        response.setProductImage(cartItem.getProduct().getImageUrl());
        response.setPrice(cartItem.getProduct().getPrice());
        response.setQuantity(cartItem.getQuantity());
        response.setSubtotal(cartItem.getProduct().getPrice().multiply(java.math.BigDecimal.valueOf(cartItem.getQuantity())));
        response.setAddedAt(cartItem.getAddedAt());
        response.setIsAvailable(cartItem.getProduct().getIsActive() && cartItem.getProduct().getIsAvailable());
        return response;
    }
}
