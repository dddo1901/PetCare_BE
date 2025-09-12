package TechWiz.auths.models.dto;

import TechWiz.auths.models.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private String token;
    private String tokenType = "Bearer";
    private Long userId;
    private String email;
    private String fullName;
    private Role role;
    private Boolean isEmailVerified;
    private String message;
    
    public AuthResponse(String token, Long userId, String email, String fullName, Role role, Boolean isEmailVerified) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.isEmailVerified = isEmailVerified;
    }
    
    // Simplified constructor for login response (only essential fields)
    public AuthResponse(String token, Long userId, String email, Role role) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.isEmailVerified = true; // If they can login, email is verified
    }
    
    public AuthResponse(String message) {
        this.message = message;
    }
}
