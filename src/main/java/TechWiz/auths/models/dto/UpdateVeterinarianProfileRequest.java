package TechWiz.auths.models.dto;

import jakarta.validation.constraints.Min;
import java.time.LocalTime;
import java.util.List;
import lombok.Data;

@Data
public class UpdateVeterinarianProfileRequest {
    private String address;
    private String licenseNumber;
    
    @Min(value = 0, message = "Experience years must be positive")
    private Integer experienceYears;
    
    private List<String> specializations;
    private String clinicName;
    private String clinicAddress;
    
    // Available time slots
    private LocalTime availableFromTime;
    private LocalTime availableToTime;
    private List<String> availableDays; // ["MONDAY", "TUESDAY", ...]
    
    private String profileImageUrl;
    private String bio;
    private Double consultationFee;
    private Boolean isAvailableForEmergency = false;
}