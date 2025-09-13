package TechWiz.petOwner.services;

import TechWiz.petOwner.dto.PetOwnerCartItemRequest;
import TechWiz.petOwner.models.PetOwnerCart;
import TechWiz.petOwner.repositories.PetOwnerCartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PetOwnerCartService {
    
    @Autowired
    private PetOwnerCartRepository cartRepository;
    
    public PetOwnerCart addToCart(PetOwnerCartItemRequest request, Long ownerId) {
        // Check if item already exists in cart
        Optional<PetOwnerCart> existingItem = cartRepository.findByOwnerIdAndProductId(ownerId, request.getProductId());
        
        if (existingItem.isPresent()) {
            // Update quantity
            PetOwnerCart cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            cartItem.setTotalPrice(cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            return cartRepository.save(cartItem);
        } else {
            // Create new cart item
            PetOwnerCart cartItem = new PetOwnerCart();
            cartItem.setOwnerId(ownerId);
            cartItem.setProductId(request.getProductId());
            cartItem.setQuantity(request.getQuantity());
            cartItem.setUnitPrice(request.getUnitPrice());
            cartItem.setTotalPrice(request.getTotalPrice());
            return cartRepository.save(cartItem);
        }
    }
    
    public List<PetOwnerCart> getCartByOwner(Long ownerId) {
        return cartRepository.findByOwnerId(ownerId);
    }
    
    public Page<PetOwnerCart> getCartByOwner(Long ownerId, Pageable pageable) {
        return cartRepository.findByOwnerId(ownerId, pageable);
    }
    
    public PetOwnerCart updateCartItem(Long cartItemId, PetOwnerCartItemRequest request, Long ownerId) {
        PetOwnerCart cartItem = cartRepository.findById(cartItemId)
                .filter(c -> c.getOwnerId().equals(ownerId))
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        cartItem.setQuantity(request.getQuantity());
        cartItem.setUnitPrice(request.getUnitPrice());
        cartItem.setTotalPrice(request.getTotalPrice());
        
        return cartRepository.save(cartItem);
    }
    
    public boolean removeFromCart(Long cartItemId, Long ownerId) {
        Optional<PetOwnerCart> cartItem = cartRepository.findById(cartItemId)
                .filter(c -> c.getOwnerId().equals(ownerId));
        
        if (cartItem.isPresent()) {
            cartRepository.delete(cartItem.get());
            return true;
        }
        return false;
    }
    
    public boolean removeProductFromCart(Long productId, Long ownerId) {
        Optional<PetOwnerCart> cartItem = cartRepository.findByOwnerIdAndProductId(ownerId, productId);
        
        if (cartItem.isPresent()) {
            cartRepository.delete(cartItem.get());
            return true;
        }
        return false;
    }
    
    public void clearCart(Long ownerId) {
        cartRepository.deleteByOwnerId(ownerId);
    }
    
    public void removeMultipleProducts(Long ownerId, List<Long> productIds) {
        cartRepository.deleteByOwnerIdAndProductIdIn(ownerId, productIds);
    }
    
    public Long getCartItemCount(Long ownerId) {
        return cartRepository.countByOwnerId(ownerId);
    }
    
    public Double getCartTotal(Long ownerId) {
        Double total = cartRepository.getTotalPriceByOwnerId(ownerId);
        return total != null ? total : 0.0;
    }
    
    public Long getCartTotalQuantity(Long ownerId) {
        Long total = cartRepository.getTotalQuantityByOwnerId(ownerId);
        return total != null ? total : 0L;
    }
    
    public boolean isProductInCart(Long ownerId, Long productId) {
        return cartRepository.findByOwnerIdAndProductId(ownerId, productId).isPresent();
    }
    
    public Optional<PetOwnerCart> getCartItemByProduct(Long ownerId, Long productId) {
        return cartRepository.findByOwnerIdAndProductId(ownerId, productId);
    }
}
