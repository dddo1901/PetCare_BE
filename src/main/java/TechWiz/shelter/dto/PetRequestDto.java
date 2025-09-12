package TechWiz.shelter.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import TechWiz.shelter.models.Pet;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetRequestDto {
    
    @NotBlank(message = "Pet name is required")
    @Size(max = 100, message = "Pet name must not exceed 100 characters")
    private String name;
    
    @NotNull(message = "Pet type is required")
    private Pet.PetType type;
    
    @NotBlank(message = "Breed is required")
    @Size(max = 100, message = "Breed must not exceed 100 characters")
    private String breed;
    
    @NotNull(message = "Age in months is required")
    @Positive(message = "Age must be positive")
    private Integer ageInMonths;
    
    @NotNull(message = "Gender is required")
    private Pet.Gender gender;
    
    @NotNull(message = "Size is required")
    private Pet.Size size;
    
    @Size(max = 50, message = "Color must not exceed 50 characters")
    private String color;
    
    private BigDecimal weight;
    
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;
    
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;
    
    private Pet.AdoptionStatus adoptionStatus;
    private Pet.HealthStatus healthStatus;
    private Boolean vaccinated;
    private Boolean spayedNeutered;
    private Boolean microchipped;
    private Boolean houseTrained;
    private Boolean goodWithKids;
    private Boolean goodWithPets;
    private Pet.EnergyLevel energyLevel;
    
    @Size(max = 500, message = "Special needs must not exceed 500 characters")
    private String specialNeeds;
    
    private BigDecimal adoptionFee;
    
    @Size(max = 1000, message = "Personality must not exceed 1000 characters")
    private String personality;
    
    @Size(max = 1000, message = "Requirements must not exceed 1000 characters")
    private String requirements;
}
