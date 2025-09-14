package TechWiz.petOwner.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PetOwnerPetResponse {
    
    private Long id;
    private String name;
    private String type;
    private String breed;
    private String age; // Converted from ageInMonths for frontend
    private String gender;
    private String color;
    private BigDecimal weight;
    private String microchip;
    private String imageUrl;
    private String[] photos; // Array of photo URLs
    private String description;
    private String healthStatus;
    private Boolean vaccinated;
    private Boolean spayedNeutered;
    private Boolean microchipped;
    private Boolean houseTrained;
    private Boolean goodWithKids;
    private Boolean goodWithPets;
    private String energyLevel;
    private String specialNeeds;
    private String personality;
    private String requirements;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long ownerId;
}
