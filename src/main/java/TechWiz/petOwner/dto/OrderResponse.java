package TechWiz.petOwner.dto;

import java.time.LocalDateTime;
import java.util.List;

import TechWiz.petOwner.models.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    
    private Long id;
    
    private String orderNumber;
    
    private Long customerId;
    
    private String status;
    
    private Double totalAmount;
    
    private String paymentMethod;
    
    private String shippingAddress;
    
    private String contactPhone;
    
    private String specialInstructions;
    
    private LocalDateTime orderDate;
    
    private LocalDateTime deliveryDate;
    
    private LocalDateTime estimatedDelivery;
    
    private String trackingNumber;
    
    private String cancellationReason;
    
    private List<OrderItem> orderItems;
}
