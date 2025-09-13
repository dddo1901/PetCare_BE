package TechWiz.petOwner.repositories;

import TechWiz.petOwner.models.PetOwnerCartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PetOwnerCartItemRepository extends JpaRepository<PetOwnerCartItem, Long> {
    
    // Find cart items by cart ID
    List<PetOwnerCartItem> findByCartId(Long cartId);
    
    // Find cart items by cart ID with pagination
    Page<PetOwnerCartItem> findByCartId(Long cartId, Pageable pageable);
    
    // Find cart item by cart ID and product ID
    Optional<PetOwnerCartItem> findByCartIdAndProductId(Long cartId, Long productId);
    
    // Find cart items by cart ID and product ID list
    List<PetOwnerCartItem> findByCartIdAndProductIdIn(Long cartId, List<Long> productIds);
    
    // Count cart items by cart ID
    @Query("SELECT COUNT(ci) FROM PetOwnerCartItem ci WHERE ci.cartId = :cartId")
    Long countByCartId(@Param("cartId") Long cartId);
    
    // Calculate total price by cart ID
    @Query("SELECT SUM(ci.totalPrice) FROM PetOwnerCartItem ci WHERE ci.cartId = :cartId")
    Double getTotalPriceByCartId(@Param("cartId") Long cartId);
    
    // Calculate total quantity by cart ID
    @Query("SELECT SUM(ci.quantity) FROM PetOwnerCartItem ci WHERE ci.cartId = :cartId")
    Long getTotalQuantityByCartId(@Param("cartId") Long cartId);
    
    // Delete cart items by cart ID
    void deleteByCartId(Long cartId);
    
    // Delete cart item by cart ID and product ID
    void deleteByCartIdAndProductId(Long cartId, Long productId);
    
    // Delete cart items by cart ID and product ID list
    void deleteByCartIdAndProductIdIn(Long cartId, List<Long> productIds);
}
