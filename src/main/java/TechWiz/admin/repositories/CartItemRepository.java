package TechWiz.admin.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import TechWiz.admin.models.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    // Find cart items by user
    List<CartItem> findByUserIdOrderByAddedAtDesc(Long userId);
    
    // Find specific cart item by user and product
    CartItem findByUserIdAndProductId(Long userId, Long productId);
    
    // Check if user has product in cart
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    
    // Count items in user's cart
    Long countByUserId(Long userId);
    
    // Calculate total cart value for user
    @Query("SELECT SUM(c.quantity * c.product.price) FROM CartItem c WHERE c.userId = :userId")
    Double calculateCartTotal(@Param("userId") Long userId);
    
    // Count total quantity in user's cart
    @Query("SELECT SUM(c.quantity) FROM CartItem c WHERE c.userId = :userId")
    Integer calculateTotalQuantity(@Param("userId") Long userId);
    
    // Remove old cart items (cleanup utility)
    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem c WHERE c.updatedAt < :cutoffDate")
    int deleteOldCartItems(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // Find cart items by product ID (for product management)
    List<CartItem> findByProductId(Long productId);
    
    // Update quantity for existing cart item
    @Modifying
    @Transactional
    @Query("UPDATE CartItem c SET c.quantity = :quantity, c.updatedAt = :updatedAt " +
           "WHERE c.userId = :userId AND c.productId = :productId")
    int updateCartItemQuantity(@Param("userId") Long userId, @Param("productId") Long productId, 
                              @Param("quantity") Integer quantity, @Param("updatedAt") LocalDateTime updatedAt);
    
    // Delete cart item by user and product
    @Modifying
    @Transactional
    void deleteByUserIdAndProductId(Long userId, Long productId);
    
    // Delete all cart items for user
    @Modifying
    @Transactional
    void deleteByUserId(Long userId);
    
    // Find cart items for products that are inactive or unavailable
    @Query("SELECT c FROM CartItem c JOIN c.product p WHERE " +
           "(p.isActive = false OR p.isAvailable = false) " +
           "AND c.userId = :userId")
    List<CartItem> findUnavailableCartItems(@Param("userId") Long userId);
}
