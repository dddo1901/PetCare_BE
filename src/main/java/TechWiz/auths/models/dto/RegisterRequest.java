package TechWiz.auths.models.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;
import lombok.Data;
import TechWiz.auths.models.Role;

import java.time.LocalTime;
import java.util.List;

@Data
@ValidRoleBasedData
public class RegisterRequest {
    
    // Basic user information
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9+\\-\\s]{10,15}$", message = "Phone number should be valid")
    private String phoneNumber;
    
    private Role role;
    
    private String address;
    
    // Common profile fields
    private String profileImageUrl;
    private String bio;
    
    // =========================
    // PET OWNER specific fields
    // =========================
    private String emergencyContactName;
    private String emergencyContactPhone;
    private Boolean allowAccountSharing = false;
    
    // =========================
    // VETERINARIAN specific fields
    // =========================
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
    
    private Double consultationFee;
    private Boolean isAvailableForEmergency = false;
    
    // =========================
    // SHELTER specific fields
    // =========================
    private String shelterName;
    private String contactPersonName;
    
    private String registrationNumber;
    private String website;
    private String description;
    
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;
    
    private Integer currentOccupancy = 0;
    private List<String> images;
    private Boolean acceptsDonations = false;
    private String operatingHours;
}
