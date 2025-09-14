package TechWiz.auths.models.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import TechWiz.auths.models.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    // Basic user info
    private Long userId;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String profileImageUrl; // Avatar from users table
    private Role role;
    private Boolean isActive;
    private Boolean isEmailVerified;
    private LocalDateTime createdAt;
    
    // Profile info based on role
    private Object profile;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PetOwnerProfileData {
        private Long profileId;
        private String address;
        private String emergencyContactName;
        private String emergencyContactPhone;
        private String profileImageUrl;
        private String bio;
        private Boolean allowAccountSharing;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VeterinarianProfileData {
        private Long profileId;
        private String address;
        private String licenseNumber;
        private Integer experienceYears;
        private List<String> specializations;
        private String clinicName;
        private String clinicAddress;
        private LocalTime availableFromTime;
        private LocalTime availableToTime;
        private List<String> availableDays;
        private String profileImageUrl;
        private String bio;
        private Double consultationFee;
        private Boolean isAvailableForEmergency;
        private Boolean isProfileComplete;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShelterProfileData {
        private Long profileId;
        private String address;
        private String shelterName;
        private String contactPersonName;
        private String registrationNumber;
        private String website;
        private String description;
        private Integer capacity;
        private Integer currentOccupancy;
        private String profileImageUrl;
        private List<String> images;
        private Boolean isVerified;
        private Boolean acceptsDonations;
        private String operatingHours;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
