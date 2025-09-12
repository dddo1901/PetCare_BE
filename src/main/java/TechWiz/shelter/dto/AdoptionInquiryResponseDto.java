package TechWiz.shelter.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import TechWiz.shelter.models.AdoptionInquiry;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdoptionInquiryResponseDto {
    
    private Long id;
    private String adopterName;
    private String adopterEmail;
    private String adopterPhone;
    private String adopterAddress;
    private String message;
    private String livingSituation;
    private String petExperience;
    private Boolean hasYard;
    private Boolean hasOtherPets;
    private Boolean hasChildren;
    private AdoptionInquiry.InquiryStatus status;
    private String shelterResponse;
    private LocalDateTime respondedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Pet basic info
    private PetBasicInfoDto pet;
    
    // Shelter basic info
    private ShelterBasicInfoDto shelter;
}
