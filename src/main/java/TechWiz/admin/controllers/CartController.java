package TechWiz.admin.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import TechWiz.admin.services.CartService;
import TechWiz.admin.models.dto.AddToCartRequest;
import TechWiz.admin.models.dto.CartItemResponse;
import TechWiz.auths.models.dto.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
@PreAuthorize("hasRole('PET_OWNER')")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@Valid @RequestBody AddToCartRequest request,
                                     @RequestHeader("user-id") Long userId) {
        try {
            String result = cartService.addToCart(request, userId);
            boolean isSuccess = result.contains("successfully");
            return ResponseEntity.ok(new ApiResponse(isSuccess, result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error adding to cart: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getCartItems(@RequestHeader("user-id") Long userId) {
        try {
            List<CartItemResponse> cartItems = cartService.getCartItems(userId);
            Double total = cartService.getCartTotal(userId);
            Integer totalQuantity = cartService.getTotalQuantity(userId);
            
            CartSummary summary = new CartSummary(cartItems, total, totalQuantity, cartItems.size());
            return ResponseEntity.ok(new ApiResponse(true, "Cart items retrieved", summary));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving cart: " + e.getMessage()));
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> updateCartItem(@PathVariable Long productId,
                                          @RequestParam Integer quantity,
                                          @RequestHeader("user-id") Long userId) {
        try {
            String result = cartService.updateCartItem(userId, productId, quantity);
            boolean isSuccess = result.contains("successfully") || result.contains("removed");
            return ResponseEntity.ok(new ApiResponse(isSuccess, result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error updating cart: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long productId,
                                          @RequestHeader("user-id") Long userId) {
        try {
            String result = cartService.removeFromCart(userId, productId);
            boolean isSuccess = result.contains("removed");
            return ResponseEntity.ok(new ApiResponse(isSuccess, result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error removing from cart: " + e.getMessage()));
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(@RequestHeader("user-id") Long userId) {
        try {
            cartService.clearCart(userId);
            return ResponseEntity.ok(new ApiResponse(true, "Cart cleared successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error clearing cart: " + e.getMessage()));
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getCartSummary(@RequestHeader("user-id") Long userId) {
        try {
            Double total = cartService.getCartTotal(userId);
            Integer totalQuantity = cartService.getTotalQuantity(userId);
            Long itemCount = cartService.getCartItemCount(userId);
            
            CartSummaryInfo summaryInfo = new CartSummaryInfo(total, totalQuantity, itemCount.intValue());
            return ResponseEntity.ok(new ApiResponse(true, "Cart summary retrieved", summaryInfo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving cart summary: " + e.getMessage()));
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateCart(@RequestHeader("user-id") Long userId) {
        try {
            String validation = cartService.validateCart(userId);
            boolean isValid = "Cart is valid".equals(validation);
            
            if (!isValid) {
                // Clean up unavailable items
                List<String> removedItems = cartService.cleanupUnavailableItems(userId);
                if (!removedItems.isEmpty()) {
                    return ResponseEntity.ok(new ApiResponse(false, 
                        validation + ". Removed unavailable items: " + String.join(", ", removedItems)));
                }
            }
            
            return ResponseEntity.ok(new ApiResponse(isValid, validation));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error validating cart: " + e.getMessage()));
        }
    }

    // Inner classes for response structure
    public static class CartSummary {
        public List<CartItemResponse> items;
        public Double total;
        public Integer totalQuantity;
        public Integer itemCount;

        public CartSummary(List<CartItemResponse> items, Double total, Integer totalQuantity, Integer itemCount) {
            this.items = items;
            this.total = total;
            this.totalQuantity = totalQuantity;
            this.itemCount = itemCount;
        }
    }

    public static class CartSummaryInfo {
        public Double total;
        public Integer totalQuantity;
        public Integer itemCount;

        public CartSummaryInfo(Double total, Integer totalQuantity, Integer itemCount) {
            this.total = total;
            this.totalQuantity = totalQuantity;
            this.itemCount = itemCount;
        }
    }
}
