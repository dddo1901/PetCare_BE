package TechWiz.petOwner.repositories;

import TechWiz.petOwner.models.PetOwnerCart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PetOwnerCartRepository extends JpaRepository<PetOwnerCart, Long> {
    
    // Find cart items by owner ID
    List<PetOwnerCart> findByOwnerId(Long ownerId);
    
    // Find cart items by owner ID with pagination
    Page<PetOwnerCart> findByOwnerId(Long ownerId, Pageable pageable);
    
    // Find cart item by owner ID and product ID
    Optional<PetOwnerCart> findByOwnerIdAndProductId(Long ownerId, Long productId);
    
    // Find cart items by owner ID and product ID list
    List<PetOwnerCart> findByOwnerIdAndProductIdIn(Long ownerId, List<Long> productIds);
    
    // Count cart items by owner ID
    @Query("SELECT COUNT(c) FROM PetOwnerCart c WHERE c.ownerId = :ownerId")
    Long countByOwnerId(@Param("ownerId") Long ownerId);
    
    // Calculate total price by owner ID
    @Query("SELECT SUM(c.totalPrice) FROM PetOwnerCart c WHERE c.ownerId = :ownerId")
    Double getTotalPriceByOwnerId(@Param("ownerId") Long ownerId);
    
    // Calculate total quantity by owner ID
    @Query("SELECT SUM(c.quantity) FROM PetOwnerCart c WHERE c.ownerId = :ownerId")
    Long getTotalQuantityByOwnerId(@Param("ownerId") Long ownerId);
    
    // Delete cart items by owner ID
    void deleteByOwnerId(Long ownerId);
    
    // Delete cart item by owner ID and product ID
    void deleteByOwnerIdAndProductId(Long ownerId, Long productId);
    
    // Delete cart items by owner ID and product ID list
    void deleteByOwnerIdAndProductIdIn(Long ownerId, List<Long> productIds);
}
