package TechWiz.shelter.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// import TechWiz.shelter.models.Shelter; // Removed - using ShelterProfile instead

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShelterResponseDto {
    
    private Long id;
    private String shelterName;
    private String contactPersonName;
    private String email;
    private String phoneNumber;
    private String address;
    private String description;
    private String website;
    private String imageUrl;
    // private Shelter.ShelterStatus status; // Removed - define enum locally if needed
    private String status; // Using String instead of enum for now
    private Boolean isVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Statistics
    private Long totalPets;
    private Long availablePets;
    private Long pendingInquiries;
}
