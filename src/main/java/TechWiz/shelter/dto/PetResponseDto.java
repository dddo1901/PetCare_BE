package TechWiz.shelter.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import TechWiz.shelter.models.ShelterPet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetResponseDto {
    
    private Long id;
    private String name;
    private ShelterPet.PetType type;
    private String breed;
    private Integer ageInMonths;
    private ShelterPet.Gender gender;
    private ShelterPet.Size size;
    private String color;
    private BigDecimal weight;
    private String imageUrl;
    private String description;
    private ShelterPet.AdoptionStatus adoptionStatus;
    private ShelterPet.HealthStatus healthStatus;
    private Boolean vaccinated;
    private Boolean spayedNeutered;
    private Boolean microchipped;
    private Boolean houseTrained;
    private Boolean goodWithKids;
    private Boolean goodWithPets;
    private ShelterPet.EnergyLevel energyLevel;
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
