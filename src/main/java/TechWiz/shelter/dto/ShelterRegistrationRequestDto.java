package TechWiz.shelter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShelterRegistrationRequestDto {
    
    @NotBlank(message = "Shelter name is required")
    @Size(max = 200, message = "Shelter name must not exceed 200 characters")
    private String shelterName;
    
    @NotBlank(message = "Contact person name is required")
    @Size(max = 100, message = "Contact person name must not exceed 100 characters")
    private String contactPersonName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]{7,20}$", message = "Invalid phone number format")
    private String phoneNumber;
    
    @NotBlank(message = "Address is required")
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @Size(max = 200, message = "Website must not exceed 200 characters")
    private String website;
    
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;
}
