package TechWiz.petOwner.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePetRequest {
    
    @NotBlank(message = "Pet name is required")
    private String name;
    
    @NotBlank(message = "Pet type is required")
    private String type; // DOG, CAT, BIRD, etc.
    
    private String breed;
    
    @NotNull(message = "Pet age is required")
    @Positive(message = "Pet age must be positive")
    private Integer age;
    
    @Positive(message = "Pet weight must be positive")
    private Double weight;
    
    private String gender; // MALE, FEMALE, UNKNOWN
    
    private String color;
    
    private LocalDate dateOfBirth;
    
    private String microchipId;
    
    private List<String> photos;
}
