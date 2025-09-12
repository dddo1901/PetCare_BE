package TechWiz.petOwner.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import TechWiz.petOwner.dto.CreateOrderRequest;
import TechWiz.petOwner.dto.OrderResponse;
import TechWiz.petOwner.models.Order;
import TechWiz.petOwner.models.OrderItem;
import TechWiz.petOwner.repositories.OrderRepository;
import TechWiz.petOwner.repositories.OrderItemRepository;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;

    // Create new order
    public OrderResponse createOrder(CreateOrderRequest request, Long customerId) {
        // Create order
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomerId(customerId);
        order.setStatus("PENDING");
        order.setPaymentMethod(request.getPaymentMethod());
        order.setShippingAddress(request.getShippingAddress());
        order.setContactPhone(request.getContactPhone());
        order.setSpecialInstructions(request.getSpecialInstructions());
        
        Order savedOrder = orderRepository.save(order);
        
        // Create order items
        double totalAmount = 0.0;
        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getOrderItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(savedOrder.getId());
            orderItem.setProductId(itemRequest.getProductId());
            orderItem.setProductName(itemRequest.getProductName());
            orderItem.setPrice(itemRequest.getPrice());
            orderItem.setQuantity(itemRequest.getQuantity());
            
            orderItemRepository.save(orderItem);
            
            totalAmount += itemRequest.getPrice() * itemRequest.getQuantity();
        }
        
        // Update order total amount
        savedOrder.setTotalAmount(totalAmount);
        savedOrder = orderRepository.save(savedOrder);
        
        return convertToOrderResponse(savedOrder);
    }

    // Get orders by customer
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomer(Long customerId) {
        List<Order> orders = orderRepository.findByCustomerIdOrderByOrderDateDesc(customerId);
        return orders.stream().map(this::convertToOrderResponse).toList();
    }

    // Get orders by customer with pagination
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByCustomer(Long customerId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByCustomerIdOrderByOrderDateDesc(customerId, pageable);
        return orders.map(this::convertToOrderResponse);
    }

    // Get order by ID (with customer verification)
    @Transactional(readOnly = true)
    public Optional<OrderResponse> getOrderById(Long orderId, Long customerId) {
        Optional<Order> order = orderRepository.findByIdAndCustomerId(orderId, customerId);
        return order.map(this::convertToOrderResponse);
    }

    // Get order by order number
    @Transactional(readOnly = true)
    public Optional<OrderResponse> getOrderByOrderNumber(String orderNumber, Long customerId) {
        Optional<Order> order = orderRepository.findByOrderNumberAndCustomerId(orderNumber, customerId);
        return order.map(this::convertToOrderResponse);
    }

    // Update order status
    public OrderResponse updateOrderStatus(Long orderId, String status, Long customerId) {
        Order order = orderRepository.findByIdAndCustomerId(orderId, customerId)
            .orElseThrow(() -> new RuntimeException("Order not found or doesn't belong to customer"));
        
        // Validate status transition
        if (!isValidStatusTransition(order.getStatus(), status)) {
            throw new RuntimeException("Invalid status transition from " + order.getStatus() + " to " + status);
        }
        
        order.setStatus(status);
        
        // Set delivery date if status is DELIVERED
        if ("DELIVERED".equals(status)) {
            order.setDeliveryDate(LocalDateTime.now());
        }
        
        Order savedOrder = orderRepository.save(order);
        return convertToOrderResponse(savedOrder);
    }

    // Cancel order
    public OrderResponse cancelOrder(Long orderId, String reason, Long customerId) {
        Order order = orderRepository.findByIdAndCustomerId(orderId, customerId)
            .orElseThrow(() -> new RuntimeException("Order not found or doesn't belong to customer"));
        
        // Check if order can be cancelled
        if ("DELIVERED".equals(order.getStatus()) || "CANCELLED".equals(order.getStatus())) {
            throw new RuntimeException("Cannot cancel order with status: " + order.getStatus());
        }
        
        order.setStatus("CANCELLED");
        order.setCancellationReason(reason);
        
        Order savedOrder = orderRepository.save(order);
        return convertToOrderResponse(savedOrder);
    }

    // Get orders by status
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(Long customerId, String status) {
        List<Order> orders = orderRepository.findByCustomerIdAndStatusOrderByOrderDateDesc(customerId, status);
        return orders.stream().map(this::convertToOrderResponse).toList();
    }

    // Get recent orders (last 30 days)
    @Transactional(readOnly = true)
    public List<OrderResponse> getRecentOrders(Long customerId) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Order> orders = orderRepository.findRecentOrdersByCustomer(customerId, thirtyDaysAgo);
        return orders.stream().map(this::convertToOrderResponse).toList();
    }

    // Get orders by date range
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByDateRange(Long customerId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = orderRepository.findByCustomerIdAndDateRange(customerId, startDate, endDate);
        return orders.stream().map(this::convertToOrderResponse).toList();
    }

    // Search orders
    @Transactional(readOnly = true)
    public List<OrderResponse> searchOrders(Long customerId, String keyword) {
        List<Order> orders = orderRepository.searchOrdersByCustomer(customerId, keyword);
        return orders.stream().map(this::convertToOrderResponse).toList();
    }

    // Get active orders (pending, processing, shipped)
    @Transactional(readOnly = true)
    public List<OrderResponse> getActiveOrders(Long customerId) {
        List<Order> orders = orderRepository.findActiveOrdersByCustomer(customerId);
        return orders.stream().map(this::convertToOrderResponse).toList();
    }

    // Get order statistics
    @Transactional(readOnly = true)
    public java.util.Map<String, Object> getOrderStatistics(Long customerId) {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        
        stats.put("totalOrders", orderRepository.countByCustomerIdAndStatus(customerId, "DELIVERED"));
        stats.put("pendingOrders", orderRepository.countByCustomerIdAndStatus(customerId, "PENDING"));
        stats.put("processingOrders", orderRepository.countByCustomerIdAndStatus(customerId, "PROCESSING"));
        stats.put("shippedOrders", orderRepository.countByCustomerIdAndStatus(customerId, "SHIPPED"));
        stats.put("cancelledOrders", orderRepository.countByCustomerIdAndStatus(customerId, "CANCELLED"));
        
        Double totalSpending = orderRepository.getTotalSpendingByCustomer(customerId);
        stats.put("totalSpending", totalSpending != null ? totalSpending : 0.0);
        
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        Double monthlySpending = orderRepository.getMonthlySpendingByCustomer(customerId, monthStart);
        stats.put("monthlySpending", monthlySpending != null ? monthlySpending : 0.0);
        
        return stats;
    }

    // Track order
    @Transactional(readOnly = true)
    public java.util.Map<String, Object> trackOrder(String orderNumber, Long customerId) {
        Order order = orderRepository.findByOrderNumberAndCustomerId(orderNumber, customerId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        java.util.Map<String, Object> trackingInfo = new java.util.HashMap<>();
        trackingInfo.put("orderNumber", order.getOrderNumber());
        trackingInfo.put("status", order.getStatus());
        trackingInfo.put("orderDate", order.getOrderDate());
        trackingInfo.put("estimatedDelivery", order.getEstimatedDelivery());
        trackingInfo.put("actualDelivery", order.getDeliveryDate());
        trackingInfo.put("trackingNumber", order.getTrackingNumber());
        
        // Get order items
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
        trackingInfo.put("orderItems", orderItems);
        
        return trackingInfo;
    }

    // Check if order belongs to customer
    @Transactional(readOnly = true)
    public boolean isOrderOwnedBy(Long orderId, Long customerId) {
        return orderRepository.existsByIdAndCustomerId(orderId, customerId);
    }

    // Helper methods
    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        // Define valid status transitions
        return switch (currentStatus) {
            case "PENDING" -> List.of("PROCESSING", "CANCELLED").contains(newStatus);
            case "PROCESSING" -> List.of("SHIPPED", "CANCELLED").contains(newStatus);
            case "SHIPPED" -> List.of("DELIVERED", "CANCELLED").contains(newStatus);
            case "DELIVERED", "CANCELLED" -> false; // Final states
            default -> false;
        };
    }

    private OrderResponse convertToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setCustomerId(order.getCustomerId());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setShippingAddress(order.getShippingAddress());
        response.setContactPhone(order.getContactPhone());
        response.setSpecialInstructions(order.getSpecialInstructions());
        response.setOrderDate(order.getOrderDate());
        response.setDeliveryDate(order.getDeliveryDate());
        response.setEstimatedDelivery(order.getEstimatedDelivery());
        response.setTrackingNumber(order.getTrackingNumber());
        response.setCancellationReason(order.getCancellationReason());
        
        // Get order items
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
        response.setOrderItems(orderItems);
        
        return response;
    }
}
