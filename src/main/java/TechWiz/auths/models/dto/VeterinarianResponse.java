package TechWiz.auths.models.dto;

import lombok.Data;
import java.util.List;

@Data
public class VeterinarianResponse {
    
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private String licenseNumber;
    private Integer experienceYears;
    private List<String> specializations;
    private String clinicName;
    private String clinicAddress;
    private String availableFromTime;
    private String availableToTime;
    private List<String> availableDays;
    private String profileImageUrl;
    private String bio;
    private Double consultationFee;
    private Boolean isAvailableForEmergency;
    private Boolean isProfileComplete;
    
    // Computed fields for frontend
    private String specialty; // Primary specialization for display
    private String location; // Clinic name + address for display
    private String avatar; // Profile image URL for display
    private String workingHours; // Formatted working hours for display
    private String displayText; // Full display text for dropdown
}
