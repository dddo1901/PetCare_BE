package TechWiz.petOwner.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import TechWiz.petOwner.models.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Find orders by customer (pet owner)
    List<Order> findByCustomerIdOrderByOrderDateDesc(Long customerId);
    
    Page<Order> findByCustomerIdOrderByOrderDateDesc(Long customerId, Pageable pageable);
    
    // Find orders by status
    List<Order> findByCustomerIdAndStatusOrderByOrderDateDesc(Long customerId, String status);
    
    List<Order> findByStatusOrderByOrderDateDesc(String status);
    
    // Find recent orders (last 30 days)
    @Query("SELECT o FROM Order o WHERE o.customerId = :customerId AND " +
           "o.orderDate >= :thirtyDaysAgo ORDER BY o.orderDate DESC")
    List<Order> findRecentOrdersByCustomer(@Param("customerId") Long customerId, 
                                          @Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo);
    
    // Find orders by date range
    @Query("SELECT o FROM Order o WHERE o.customerId = :customerId AND " +
           "o.orderDate BETWEEN :startDate AND :endDate ORDER BY o.orderDate DESC")
    List<Order> findByCustomerIdAndDateRange(@Param("customerId") Long customerId,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);
    
    // Find orders by total amount range
    @Query("SELECT o FROM Order o WHERE o.customerId = :customerId AND " +
           "o.totalAmount BETWEEN :minAmount AND :maxAmount ORDER BY o.orderDate DESC")
    List<Order> findByCustomerIdAndAmountRange(@Param("customerId") Long customerId,
                                             @Param("minAmount") Double minAmount,
                                             @Param("maxAmount") Double maxAmount);
    
    // Search orders by order number
    @Query("SELECT o FROM Order o WHERE o.customerId = :customerId AND " +
           "LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY o.orderDate DESC")
    List<Order> searchOrdersByCustomer(@Param("customerId") Long customerId, @Param("keyword") String keyword);
    
    // Find order by order number
    Optional<Order> findByOrderNumber(String orderNumber);
    
    // Find order by order number and customer
    Optional<Order> findByOrderNumberAndCustomerId(String orderNumber, Long customerId);
    
    // Count orders by customer and status
    long countByCustomerIdAndStatus(Long customerId, String status);
    
    // Calculate total spending by customer
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.customerId = :customerId AND o.status = 'DELIVERED'")
    Double getTotalSpendingByCustomer(@Param("customerId") Long customerId);
    
    // Calculate monthly spending
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.customerId = :customerId AND " +
           "o.orderDate >= :monthStart AND o.status = 'DELIVERED'")
    Double getMonthlySpendingByCustomer(@Param("customerId") Long customerId, @Param("monthStart") LocalDateTime monthStart);
    
    // Find pending orders (for admin)
    @Query("SELECT o FROM Order o WHERE o.status IN ('PENDING', 'PROCESSING') ORDER BY o.orderDate ASC")
    List<Order> findPendingOrders();
    
    // Count total orders by status
    long countByStatus(String status);
    
    // Find orders requiring action (pending, processing, shipped)
    @Query("SELECT o FROM Order o WHERE o.customerId = :customerId AND " +
           "o.status IN ('PENDING', 'PROCESSING', 'SHIPPED') ORDER BY o.orderDate DESC")
    List<Order> findActiveOrdersByCustomer(@Param("customerId") Long customerId);
    
    // Check if order belongs to customer
    boolean existsByIdAndCustomerId(Long orderId, Long customerId);
    
    // Find order by ID and customer (security check)
    Optional<Order> findByIdAndCustomerId(Long orderId, Long customerId);
    
    // Find orders by shipping address (partial match)
    @Query("SELECT o FROM Order o WHERE o.customerId = :customerId AND " +
           "LOWER(o.shippingAddress) LIKE LOWER(CONCAT('%', :addressKeyword, '%')) " +
           "ORDER BY o.orderDate DESC")
    List<Order> findByCustomerIdAndShippingAddress(@Param("customerId") Long customerId, @Param("addressKeyword") String addressKeyword);
}
