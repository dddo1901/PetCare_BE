package TechWiz.veterinarian.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VeterinarianMedicalRecordResponse {
    
    private Long id;
    private Long vetId;
    private Long petId;
    private Long ownerId;
    private Long appointmentId;
    private String type;
    private String diagnosis;
    private String treatment;
    private String medication;
    private String notes;
    private String followUpInstructions;
    private Double treatmentCost;
    private LocalDateTime recordDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Pet information
    private PetInfo pet;
    
    // Owner information
    private OwnerInfo owner;
    
    // Vet information
    private VetInfo vet;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PetInfo {
        private Long id;
        private String name;
        private String breed;
        private String age;
        private String gender;
        private String imageUrl;
        private String species;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OwnerInfo {
        private Long id;
        private String name;
        private String email;
        private String phoneNumber;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VetInfo {
        private Long id;
        private String name;
        private String clinicName;
        private String specializations;
    }
}
