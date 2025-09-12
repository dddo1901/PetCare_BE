package TechWiz.petOwner.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import TechWiz.auths.models.User;
import TechWiz.petOwner.dto.CreateOrderRequest;
import TechWiz.petOwner.dto.OrderResponse;
import TechWiz.petOwner.services.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pet-owner/orders")
@PreAuthorize("hasRole('PET_OWNER')")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Create new order
    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderRequest request, 
                                       Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            OrderResponse order = orderService.createOrder(request, user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Order created successfully",
                "data", order
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get orders by customer
    @GetMapping
    public ResponseEntity<?> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            
            if (size > 0) {
                Pageable pageable = PageRequest.of(page, size);
                Page<OrderResponse> orders = orderService.getOrdersByCustomer(user.getId(), pageable);
                
                return ResponseEntity.ok().body(java.util.Map.of(
                    "success", true,
                    "message", "Orders retrieved successfully",
                    "data", orders.getContent(),
                    "pagination", java.util.Map.of(
                        "currentPage", orders.getNumber(),
                        "totalPages", orders.getTotalPages(),
                        "totalElements", orders.getTotalElements(),
                        "pageSize", orders.getSize()
                    )
                ));
            } else {
                List<OrderResponse> orders = orderService.getOrdersByCustomer(user.getId());
                return ResponseEntity.ok().body(java.util.Map.of(
                    "success", true,
                    "message", "Orders retrieved successfully",
                    "data", orders
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get order by ID
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable Long orderId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Optional<OrderResponse> order = orderService.getOrderById(orderId, user.getId());
            
            if (order.isPresent()) {
                return ResponseEntity.ok().body(java.util.Map.of(
                    "success", true,
                    "message", "Order retrieved successfully",
                    "data", order.get()
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get order by order number
    @GetMapping("/order-number/{orderNumber}")
    public ResponseEntity<?> getOrderByOrderNumber(@PathVariable String orderNumber, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Optional<OrderResponse> order = orderService.getOrderByOrderNumber(orderNumber, user.getId());
            
            if (order.isPresent()) {
                return ResponseEntity.ok().body(java.util.Map.of(
                    "success", true,
                    "message", "Order retrieved successfully",
                    "data", order.get()
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Update order status
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId,
                                             @RequestParam String status,
                                             Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            OrderResponse order = orderService.updateOrderStatus(orderId, status, user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Order status updated successfully",
                "data", order
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Cancel order
    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId,
                                       @RequestParam(required = false) String reason,
                                       Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            OrderResponse order = orderService.cancelOrder(orderId, reason, user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Order cancelled successfully",
                "data", order
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get orders by status
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getOrdersByStatus(@PathVariable String status, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            List<OrderResponse> orders = orderService.getOrdersByStatus(user.getId(), status);
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Orders retrieved successfully",
                "data", orders
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get recent orders
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentOrders(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            List<OrderResponse> orders = orderService.getRecentOrders(user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Recent orders retrieved successfully",
                "data", orders
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get orders by date range
    @GetMapping("/date-range")
    public ResponseEntity<?> getOrdersByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            List<OrderResponse> orders = orderService.getOrdersByDateRange(user.getId(), start, end);
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Orders retrieved successfully",
                "data", orders
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Search orders
    @GetMapping("/search")
    public ResponseEntity<?> searchOrders(@RequestParam String keyword, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            List<OrderResponse> orders = orderService.searchOrders(user.getId(), keyword);
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Search completed successfully",
                "data", orders
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get active orders
    @GetMapping("/active")
    public ResponseEntity<?> getActiveOrders(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            List<OrderResponse> orders = orderService.getActiveOrders(user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Active orders retrieved successfully",
                "data", orders
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get order statistics
    @GetMapping("/statistics")
    public ResponseEntity<?> getOrderStatistics(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Map<String, Object> statistics = orderService.getOrderStatistics(user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Order statistics retrieved successfully",
                "data", statistics
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Track order
    @GetMapping("/track/{orderNumber}")
    public ResponseEntity<?> trackOrder(@PathVariable String orderNumber, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Map<String, Object> trackingInfo = orderService.trackOrder(orderNumber, user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Order tracking information retrieved successfully",
                "data", trackingInfo
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}
