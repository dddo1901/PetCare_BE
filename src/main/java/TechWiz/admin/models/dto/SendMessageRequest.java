package TechWiz.admin.models.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendMessageRequest {
    
    private Long receiverId; // null for broadcast messages
    
    @NotBlank(message = "Message is required")
    private String message;
}
