package TechWiz.shelter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdoptionInquiryRequestDto {
    
    @NotNull(message = "Pet ID is required")
    private Long petId;
    
    @NotBlank(message = "Adopter name is required")
    @Size(max = 100, message = "Adopter name must not exceed 100 characters")
    private String adopterName;
    
    @NotBlank(message = "Adopter email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String adopterEmail;
    
    @NotBlank(message = "Adopter phone is required")
    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]{7,20}$", message = "Invalid phone number format")
    private String adopterPhone;
    
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String adopterAddress;
    
    @NotBlank(message = "Message is required")
    @Size(max = 2000, message = "Message must not exceed 2000 characters")
    private String message;
    
    @Size(max = 2000, message = "Living situation must not exceed 2000 characters")
    private String livingSituation;
    
    @Size(max = 2000, message = "Pet experience must not exceed 2000 characters")
    private String petExperience;
    
    private Boolean hasYard;
    private Boolean hasOtherPets;
    private Boolean hasChildren;
}
