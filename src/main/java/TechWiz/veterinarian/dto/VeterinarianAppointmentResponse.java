package TechWiz.veterinarian.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VeterinarianAppointmentResponse {
    
    private Long id;
    private Long vetId;
    private Long ownerId;
    private Long petId;
    private String status;
    private String type;
    private String reason;
    private LocalDateTime appointmentDateTime;
    private String vetNotes;
    private String ownerNotes;
    private String location;
    private Double consultationFee;
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
        private String address;
        private String imageUrl;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VetInfo {
        private Long id;
        private String name;
        private String clinicName;
        private String clinicAddress;
        private String phoneNumber;
        private String specializations;
        private String imageUrl;
    }
}
