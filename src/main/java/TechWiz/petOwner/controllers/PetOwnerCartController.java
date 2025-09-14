package TechWiz.petOwner.controllers;

import TechWiz.auths.models.User;
import TechWiz.petOwner.dto.PetOwnerCartItemRequest;
import TechWiz.petOwner.models.PetOwnerCart;
import TechWiz.petOwner.services.PetOwnerCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/pet-owner/cart")
@PreAuthorize("hasRole('PET_OWNER')")
@CrossOrigin(origins = "*")
public class PetOwnerCartController {

    @Autowired
    private PetOwnerCartService cartService;

    @GetMapping
    public ResponseEntity<?> getCart(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            
            if (size > 0) {
                Pageable pageable = PageRequest.of(page, size);
                Page<PetOwnerCart> cartItems = cartService.getCartByOwner(user.getId(), pageable);
                
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cart retrieved successfully",
                    "data", cartItems.getContent(),
                    "pagination", Map.of(
                        "currentPage", cartItems.getNumber(),
                        "totalPages", cartItems.getTotalPages(),
                        "totalElements", cartItems.getTotalElements(),
                        "pageSize", cartItems.getSize()
                    )
                ));
            } else {
                List<PetOwnerCart> cartItems = cartService.getCartByOwner(user.getId());
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cart retrieved successfully",
                    "data", cartItems
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping
    public ResponseEntity<?> addToCart(@Valid @RequestBody PetOwnerCartItemRequest request, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            PetOwnerCart cartItem = cartService.addToCart(request, user.getId());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Item added to cart successfully",
                "data", cartItem
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/{cartItemId}")
    public ResponseEntity<?> updateCartItem(@PathVariable Long cartItemId, 
                                           @Valid @RequestBody PetOwnerCartItemRequest request, 
                                           Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            PetOwnerCart cartItem = cartService.updateCartItem(cartItemId, request, user.getId());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Cart item updated successfully",
                "data", cartItem
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long cartItemId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            boolean removed = cartService.removeFromCart(cartItemId, user.getId());
            
            if (removed) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Item removed from cart successfully"
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<?> removeProductFromCart(@PathVariable Long productId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            boolean removed = cartService.removeProductFromCart(productId, user.getId());
            
            if (removed) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Product removed from cart successfully"
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            cartService.clearCart(user.getId());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Cart cleared successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/products")
    public ResponseEntity<?> removeMultipleProducts(@RequestBody List<Long> productIds, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            cartService.removeMultipleProducts(user.getId(), productIds);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Products removed from cart successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> getCartStatistics(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            
            Map<String, Object> stats = Map.of(
                "itemCount", cartService.getCartItemCount(user.getId()),
                "totalPrice", cartService.getCartTotal(user.getId()),
                "totalQuantity", cartService.getCartTotalQuantity(user.getId())
            );
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Cart statistics retrieved successfully",
                "data", stats
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<?> checkProductInCart(@PathVariable Long productId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            boolean inCart = cartService.isProductInCart(user.getId(), productId);
            Optional<PetOwnerCart> cartItem = cartService.getCartItemByProduct(user.getId(), productId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Product check completed",
                "data", Map.of(
                    "inCart", inCart,
                    "cartItem", cartItem.orElse(null)
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}
