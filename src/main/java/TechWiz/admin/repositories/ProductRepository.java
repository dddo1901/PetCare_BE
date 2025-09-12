package TechWiz.admin.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import TechWiz.admin.models.Product;
import TechWiz.admin.models.ProductCategory;
import TechWiz.admin.models.PetType;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Find active products for public display
    Page<Product> findByIsActiveTrueAndIsAvailableTrueOrderByCreatedAtDesc(Pageable pageable);
    
    // Find products by category (active only)
    Page<Product> findByCategoryAndIsActiveTrueAndIsAvailableTrueOrderByCreatedAtDesc(
        ProductCategory category, Pageable pageable);
    
    // Find products by pet type (active only)
    Page<Product> findByTargetPetTypeAndIsActiveTrueAndIsAvailableTrueOrderByCreatedAtDesc(
        PetType targetPetType, Pageable pageable);
    
    // Find products by category and pet type (active only)
    Page<Product> findByCategoryAndTargetPetTypeAndIsActiveTrueAndIsAvailableTrueOrderByCreatedAtDesc(
        ProductCategory category, PetType targetPetType, Pageable pageable);
    
    // Search products by name or description (active only)
    @Query("SELECT p FROM Product p WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "p.isActive = true AND p.isAvailable = true " +
           "ORDER BY p.createdAt DESC")
    Page<Product> searchActiveProducts(@Param("keyword") String keyword, Pageable pageable);
    
    // Admin queries - all products including inactive
    Page<Product> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    // Find products by creator
    Page<Product> findByCreatedByOrderByCreatedAtDesc(Long createdBy, Pageable pageable);
    
    // Search all products for admin
    @Query("SELECT p FROM Product p WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY p.createdAt DESC")
    Page<Product> searchAllProducts(@Param("keyword") String keyword, Pageable pageable);
    
    // Count products by category
    Long countByCategoryAndIsActiveTrueAndIsAvailableTrue(ProductCategory category);
    
    // Count products by pet type
    Long countByTargetPetTypeAndIsActiveTrueAndIsAvailableTrue(PetType targetPetType);
    
    // Find low stock products
    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= :threshold AND p.isActive = true " +
           "ORDER BY p.stockQuantity ASC")
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);
    
    // Find out of stock products
    List<Product> findByStockQuantityAndIsActiveTrueOrderByUpdatedAtDesc(Integer stockQuantity);
}
