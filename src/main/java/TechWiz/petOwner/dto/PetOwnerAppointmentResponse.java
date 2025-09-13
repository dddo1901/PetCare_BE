package TechWiz.petOwner.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PetOwnerAppointmentResponse {
    
    private Long id;
    private Long ownerId;
    private Long petId;
    private Long vetId;
    private String status;
    private String type;
    private String reason;
    private String date; // Formatted date for frontend
    private String time; // Formatted time for frontend
    private LocalDateTime appointmentDateTime;
    private String vetNotes;
    private String ownerNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Nested objects for frontend
    private PetInfo pet;
    private VetInfo vet;
    private OwnerInfo owner;
    
    @Data
    public static class PetInfo {
        private Long id;
        private String name;
        private String breed;
        private String age;
        private String image;
    }
    
    @Data
    public static class VetInfo {
        private Long id;
        private String name;
        private String specialty;
        private String location;
        private String phone;
        private String avatar;
    }
    
    @Data
    public static class OwnerInfo {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private String avatar;
    }
}
