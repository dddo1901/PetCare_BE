package TechWiz.petOwner.models.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    
    @NotBlank(message = "Shipping address is required")
    private String shippingAddress; // JSON string with address details
    
    private String paymentMethod; // COD, Credit Card, etc.
    private String notes;
    
    // Items will be taken from user's cart
}
