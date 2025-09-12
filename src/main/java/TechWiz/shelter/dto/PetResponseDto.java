package TechWiz.shelter.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import TechWiz.shelter.models.Pet;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetResponseDto {
    
    private Long id;
    private String name;
    private Pet.PetType type;
    private String breed;
    private Integer ageInMonths;
    private Pet.Gender gender;
    private Pet.Size size;
    private String color;
    private BigDecimal weight;
    private String imageUrl;
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
    private String specialNeeds;
    private BigDecimal adoptionFee;
    private String personality;
    private String requirements;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Shelter info
    private ShelterBasicInfoDto shelter;
    
    // Statistics
    private Long totalInquiries;
    private Long pendingInquiries;
}
