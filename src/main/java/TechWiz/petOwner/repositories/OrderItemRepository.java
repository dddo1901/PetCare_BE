package TechWiz.petOwner.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import TechWiz.petOwner.models.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    // Find order items by order
    List<OrderItem> findByOrderId(Long orderId);
    
    // Find order items by product
    List<OrderItem> findByProductId(Long productId);
    
    // Find order items by order and product
    List<OrderItem> findByOrderIdAndProductId(Long orderId, Long productId);
    
    // Calculate total quantity for a product in an order
    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi WHERE oi.orderId = :orderId AND oi.productId = :productId")
    Integer getTotalQuantityByOrderAndProduct(@Param("orderId") Long orderId, @Param("productId") Long productId);
    
    // Calculate total items in an order
    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi WHERE oi.orderId = :orderId")
    Integer getTotalItemsByOrder(@Param("orderId") Long orderId);
    
    // Calculate total amount for an order
    @Query("SELECT COALESCE(SUM(oi.quantity * oi.price), 0) FROM OrderItem oi WHERE oi.orderId = :orderId")
    Double getTotalAmountByOrder(@Param("orderId") Long orderId);
    
    // Find most popular products (by quantity sold)
    @Query("SELECT oi.productId, SUM(oi.quantity) as totalSold FROM OrderItem oi " +
           "JOIN Order o ON oi.orderId = o.id " +
           "WHERE o.status = 'DELIVERED' " +
           "GROUP BY oi.productId ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findMostPopularProducts();
    
    // Find customer's purchase history for a specific product
    @Query("SELECT oi FROM OrderItem oi JOIN Order o ON oi.orderId = o.id " +
           "WHERE o.customerId = :customerId AND oi.productId = :productId " +
           "ORDER BY o.orderDate DESC")
    List<OrderItem> findCustomerProductHistory(@Param("customerId") Long customerId, @Param("productId") Long productId);
    
    // Count unique products ordered by customer
    @Query("SELECT COUNT(DISTINCT oi.productId) FROM OrderItem oi JOIN Order o ON oi.orderId = o.id " +
           "WHERE o.customerId = :customerId")
    Long countUniqueProductsByCustomer(@Param("customerId") Long customerId);
    
    // Find customer's frequently bought products
    @Query("SELECT oi.productId, COUNT(DISTINCT oi.orderId) as orderCount, SUM(oi.quantity) as totalQuantity " +
           "FROM OrderItem oi JOIN Order o ON oi.orderId = o.id " +
           "WHERE o.customerId = :customerId AND o.status = 'DELIVERED' " +
           "GROUP BY oi.productId " +
           "HAVING COUNT(DISTINCT oi.orderId) >= :minOrders " +
           "ORDER BY COUNT(DISTINCT oi.orderId) DESC, SUM(oi.quantity) DESC")
    List<Object[]> findFrequentlyBoughtProducts(@Param("customerId") Long customerId, @Param("minOrders") Long minOrders);
    
    // Get revenue by product
    @Query("SELECT oi.productId, SUM(oi.quantity * oi.price) as revenue " +
           "FROM OrderItem oi JOIN Order o ON oi.orderId = o.id " +
           "WHERE o.status = 'DELIVERED' " +
           "GROUP BY oi.productId ORDER BY SUM(oi.quantity * oi.price) DESC")
    List<Object[]> getRevenueByProduct();
}
