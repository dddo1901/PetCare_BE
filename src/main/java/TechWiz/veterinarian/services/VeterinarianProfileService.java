package TechWiz.veterinarian.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import TechWiz.auths.models.User;
import TechWiz.auths.repositories.UserRepository;
import TechWiz.veterinarian.dto.VetProfileResponse;
import TechWiz.veterinarian.dto.VetProfileUpdateRequest;
import TechWiz.veterinarian.models.VeterinarianProfile;
import TechWiz.veterinarian.repositories.VeterinarianProfileRepository;

@Service
@Transactional
public class VeterinarianProfileService {
    
    @Autowired
    private VeterinarianProfileRepository veterinarianProfileRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // Get veterinarian profile by user ID
    public Optional<VetProfileResponse> getVeterinarianProfile(Long userId) {
        Optional<VeterinarianProfile> profile = veterinarianProfileRepository.findByUserId(userId);
        return profile.map(this::convertToResponse);
    }
    
    // Create or update veterinarian profile
    public VetProfileResponse createOrUpdateProfile(Long userId, VetProfileUpdateRequest request) {
        Optional<VeterinarianProfile> existingProfile = veterinarianProfileRepository.findByUserId(userId);
        
        VeterinarianProfile profile;
        if (existingProfile.isPresent()) {
            profile = existingProfile.get();
            updateProfileFromRequest(profile, request);
        } else {
            profile = createNewProfile(userId, request);
        }
        
        VeterinarianProfile savedProfile = veterinarianProfileRepository.save(profile);
        return convertToResponse(savedProfile);
    }
    
    // Update veterinarian profile
    public Optional<VetProfileResponse> updateProfile(Long userId, VetProfileUpdateRequest request) {
        Optional<VeterinarianProfile> profileOpt = veterinarianProfileRepository.findByUserId(userId);
        if (profileOpt.isPresent()) {
            VeterinarianProfile profile = profileOpt.get();
            updateProfileFromRequest(profile, request);
            VeterinarianProfile savedProfile = veterinarianProfileRepository.save(profile);
            return Optional.of(convertToResponse(savedProfile));
        }
        return Optional.empty();
    }
    
    // Get all available veterinarians
    public List<VetProfileResponse> getAvailableVeterinarians() {
        List<VeterinarianProfile> profiles = veterinarianProfileRepository.findByIsAvailableTrueAndIsVerifiedTrue();
        return profiles.stream().map(this::convertToResponse).toList();
    }
    
    // Search veterinarians by keyword
    public List<VetProfileResponse> searchVeterinarians(String keyword) {
        List<VeterinarianProfile> profiles = veterinarianProfileRepository.searchVeterinarians(keyword);
        return profiles.stream().map(this::convertToResponse).toList();
    }
    
    // Find veterinarians by specialty
    public List<VetProfileResponse> getVeterinariansBySpecialty(String specialty) {
        List<VeterinarianProfile> profiles = veterinarianProfileRepository.findBySpecialty(specialty);
        return profiles.stream().map(this::convertToResponse).toList();
    }
    
    // Find veterinarians by clinic
    public List<VetProfileResponse> getVeterinariansByClinic(String clinic) {
        List<VeterinarianProfile> profiles = veterinarianProfileRepository.findByClinicContainingIgnoreCaseAndIsAvailableTrueAndIsVerifiedTrue(clinic);
        return profiles.stream().map(this::convertToResponse).toList();
    }
    
    // Get top rated veterinarians
    public List<VetProfileResponse> getTopRatedVeterinarians() {
        List<VeterinarianProfile> profiles = veterinarianProfileRepository.findByIsAvailableTrueAndIsVerifiedTrueOrderByRatingDescTotalReviewsDesc();
        return profiles.stream().map(this::convertToResponse).toList();
    }
    
    // Find veterinarians by rating range
    public List<VetProfileResponse> getVeterinariansByRating(Double minRating, Integer minReviews) {
        List<VeterinarianProfile> profiles = veterinarianProfileRepository.findByRatingRange(minRating, minReviews);
        return profiles.stream().map(this::convertToResponse).toList();
    }
    
    // Find veterinarians by consultation fee range
    public List<VetProfileResponse> getVeterinariansByFeeRange(Double minFee, Double maxFee) {
        List<VeterinarianProfile> profiles = veterinarianProfileRepository.findByConsultationFeeRange(minFee, maxFee);
        return profiles.stream().map(this::convertToResponse).toList();
    }
    
    // Find veterinarians by experience
    public List<VetProfileResponse> getVeterinariansByExperience(Integer minYears) {
        List<VeterinarianProfile> profiles = veterinarianProfileRepository.findByMinYearsExperience(minYears);
        return profiles.stream().map(this::convertToResponse).toList();
    }
    
    // Update availability status
    public boolean updateAvailabilityStatus(Long userId, boolean isAvailable) {
        Optional<VeterinarianProfile> profileOpt = veterinarianProfileRepository.findByUserId(userId);
        if (profileOpt.isPresent()) {
            VeterinarianProfile profile = profileOpt.get();
            profile.setIsAvailable(isAvailable);
            veterinarianProfileRepository.save(profile);
            return true;
        }
        return false;
    }
    
    // Update verification status (admin only)
    public boolean updateVerificationStatus(Long profileId, boolean isVerified) {
        Optional<VeterinarianProfile> profileOpt = veterinarianProfileRepository.findById(profileId);
        if (profileOpt.isPresent()) {
            VeterinarianProfile profile = profileOpt.get();
            profile.setIsVerified(isVerified);
            veterinarianProfileRepository.save(profile);
            return true;
        }
        return false;
    }
    
    // Update rating
    public boolean updateRating(Long userId, Double newRating, Integer newReviewCount) {
        Optional<VeterinarianProfile> profileOpt = veterinarianProfileRepository.findByUserId(userId);
        if (profileOpt.isPresent()) {
            VeterinarianProfile profile = profileOpt.get();
            profile.setRating(newRating);
            profile.setTotalReviews(newReviewCount);
            veterinarianProfileRepository.save(profile);
            return true;
        }
        return false;
    }
    
    // Check if license number exists
    public boolean isLicenseNumberExists(String licenseNumber) {
        return veterinarianProfileRepository.existsByLicenseNumber(licenseNumber);
    }
    
    // Get profile by license number
    public Optional<VetProfileResponse> getProfileByLicenseNumber(String licenseNumber) {
        Optional<VeterinarianProfile> profile = veterinarianProfileRepository.findByLicenseNumber(licenseNumber);
        return profile.map(this::convertToResponse);
    }
    
    // Get veterinarian statistics
    public VeterinarianStats getVeterinarianStats() {
        long totalVets = veterinarianProfileRepository.countByIsVerifiedTrue();
        long availableVets = veterinarianProfileRepository.countByIsAvailableTrueAndIsVerifiedTrue();
        return new VeterinarianStats(totalVets, availableVets);
    }
    
    // Delete veterinarian profile
    public boolean deleteProfile(Long userId) {
        Optional<VeterinarianProfile> profileOpt = veterinarianProfileRepository.findByUserId(userId);
        if (profileOpt.isPresent()) {
            veterinarianProfileRepository.delete(profileOpt.get());
            return true;
        }
        return false;
    }
    
    // Helper methods
    private VeterinarianProfile createNewProfile(Long userId, VetProfileUpdateRequest request) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        
        VeterinarianProfile profile = new VeterinarianProfile();
        profile.setUser(userOpt.get());
        updateProfileFromRequest(profile, request);
        return profile;
    }
    
    private void updateProfileFromRequest(VeterinarianProfile profile, VetProfileUpdateRequest request) {
        if (request.getLicenseNumber() != null) {
            profile.setLicenseNumber(request.getLicenseNumber());
        }
        if (request.getClinic() != null) {
            profile.setClinic(request.getClinic());
        }
        if (request.getClinicAddress() != null) {
            profile.setClinicAddress(request.getClinicAddress());
        }
        if (request.getSpecialties() != null) {
            // Convert List<String> to JSON string
            profile.setSpecialties(convertListToJson(request.getSpecialties()));
        }
        if (request.getEducation() != null) {
            // Convert Map to JSON string
            profile.setEducation(convertMapToJson(request.getEducation()));
        }
        if (request.getExperience() != null) {
            // Convert Map to JSON string
            profile.setExperience(convertMapToJson(request.getExperience()));
        }
        if (request.getYearsExperience() != null) {
            profile.setYearsExperience(request.getYearsExperience());
        }
        if (request.getWorkingHours() != null) {
            // Convert Map to JSON string
            profile.setWorkingHours(convertMapToJson(request.getWorkingHours()));
        }
        if (request.getConsultationFee() != null) {
            profile.setConsultationFee(BigDecimal.valueOf(request.getConsultationFee()));
        }
        if (request.getDescription() != null) {
            profile.setBio(request.getDescription());
        }
    }
    
    private VetProfileResponse convertToResponse(VeterinarianProfile profile) {
        VetProfileResponse response = new VetProfileResponse();
        response.setId(profile.getId());
        response.setUserId(profile.getUser().getId());
        response.setFullName(profile.getUser().getFullName());
        response.setEmail(profile.getUser().getEmail());
        response.setPhoneNumber(profile.getUser().getPhoneNumber());
        response.setLicenseNumber(profile.getLicenseNumber());
        response.setClinic(profile.getClinic());
        response.setClinicAddress(profile.getClinicAddress());
        response.setSpecialties(convertJsonToList(profile.getSpecialties()));
        response.setEducation(convertJsonToMap(profile.getEducation()));
        response.setExperience(convertJsonToMap(profile.getExperience()));
        response.setYearsExperience(profile.getYearsExperience());
        response.setWorkingHours(convertJsonToMap(profile.getWorkingHours()));
        response.setConsultationFee(profile.getConsultationFee() != null ? profile.getConsultationFee().doubleValue() : null);
        response.setEmergencyAvailable(true); // Default value since model doesn't have this field
        response.setRating(profile.getRating());
        response.setTotalReviews(profile.getTotalReviews());
        response.setIsAvailable(profile.getIsAvailable());
        response.setIsVerified(profile.getIsVerified());
        response.setDescription(profile.getBio());
        response.setCreatedAt(profile.getCreatedAt());
        response.setUpdatedAt(profile.getUpdatedAt());
        return response;
    }
    
    // Helper methods for JSON conversion
    private String convertListToJson(List<String> list) {
        try {
            return list != null ? objectMapper.writeValueAsString(list) : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    private String convertMapToJson(Map<String, Object> map) {
        try {
            return map != null ? objectMapper.writeValueAsString(map) : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    private List<String> convertJsonToList(String json) {
        try {
            return json != null ? objectMapper.readValue(json, new TypeReference<List<String>>() {}) : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    private Map<String, Object> convertJsonToMap(String json) {
        try {
            return json != null ? objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {}) : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    // Statistics class
    public static class VeterinarianStats {
        private long totalVeterinarians;
        private long availableVeterinarians;
        
        public VeterinarianStats(long totalVeterinarians, long availableVeterinarians) {
            this.totalVeterinarians = totalVeterinarians;
            this.availableVeterinarians = availableVeterinarians;
        }
        
        // Getters and setters
        public long getTotalVeterinarians() {
            return totalVeterinarians;
        }
        
        public void setTotalVeterinarians(long totalVeterinarians) {
            this.totalVeterinarians = totalVeterinarians;
        }
        
        public long getAvailableVeterinarians() {
            return availableVeterinarians;
        }
        
        public void setAvailableVeterinarians(long availableVeterinarians) {
            this.availableVeterinarians = availableVeterinarians;
        }
    }
}
