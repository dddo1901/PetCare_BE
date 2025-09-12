package TechWiz.veterinarian.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import TechWiz.auths.models.User;
import TechWiz.auths.repositories.UserRepository;
import TechWiz.veterinarian.dto.VetProfileResponse;
import TechWiz.veterinarian.dto.VetProfileUpdateRequest;
import TechWiz.veterinarian.services.VeterinarianProfileService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/veterinarian/profile")
public class VeterinarianProfileController {
    
    @Autowired
    private VeterinarianProfileService veterinarianProfileService;
    
    @Autowired
    private UserRepository userRepository;
    
    // Get current veterinarian's profile
    @GetMapping("/me")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> getMyProfile() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            Optional<VetProfileResponse> profile = veterinarianProfileService.getVeterinarianProfile(user.getId());
            if (profile.isPresent()) {
                return ResponseEntity.ok(new ApiResponse("success", "Profile retrieved successfully", profile.get()));
            } else {
                return ResponseEntity.ok(new ApiResponse("info", "Profile not found. Please complete your profile.", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve profile: " + e.getMessage(), null));
        }
    }
    
    // Create or update current veterinarian's profile
    @PostMapping("/me")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> createOrUpdateMyProfile(@Valid @RequestBody VetProfileUpdateRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Check if license number is already used by another veterinarian
            if (request.getLicenseNumber() != null && veterinarianProfileService.isLicenseNumberExists(request.getLicenseNumber())) {
                Optional<VetProfileResponse> existingProfile = veterinarianProfileService.getProfileByLicenseNumber(request.getLicenseNumber());
                if (existingProfile.isPresent() && !existingProfile.get().getUserId().equals(user.getId())) {
                    return ResponseEntity.badRequest()
                        .body(new ApiResponse("error", "License number is already registered", null));
                }
            }
            
            VetProfileResponse profile = veterinarianProfileService.createOrUpdateProfile(user.getId(), request);
            return ResponseEntity.ok(new ApiResponse("success", "Profile updated successfully", profile));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to update profile: " + e.getMessage(), null));
        }
    }
    
    // Update current veterinarian's profile
    @PutMapping("/me")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> updateMyProfile(@Valid @RequestBody VetProfileUpdateRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Check if license number is already used by another veterinarian
            if (request.getLicenseNumber() != null && veterinarianProfileService.isLicenseNumberExists(request.getLicenseNumber())) {
                Optional<VetProfileResponse> existingProfile = veterinarianProfileService.getProfileByLicenseNumber(request.getLicenseNumber());
                if (existingProfile.isPresent() && !existingProfile.get().getUserId().equals(user.getId())) {
                    return ResponseEntity.badRequest()
                        .body(new ApiResponse("error", "License number is already registered", null));
                }
            }
            
            Optional<VetProfileResponse> profile = veterinarianProfileService.updateProfile(user.getId(), request);
            if (profile.isPresent()) {
                return ResponseEntity.ok(new ApiResponse("success", "Profile updated successfully", profile.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to update profile: " + e.getMessage(), null));
        }
    }
    
    // Update availability status
    @PutMapping("/me/availability")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> updateAvailabilityStatus(@RequestParam boolean isAvailable) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            boolean updated = veterinarianProfileService.updateAvailabilityStatus(user.getId(), isAvailable);
            if (updated) {
                String status = isAvailable ? "available" : "unavailable";
                return ResponseEntity.ok(new ApiResponse("success", "Status updated to " + status, null));
            } else {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse("error", "Profile not found", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to update availability: " + e.getMessage(), null));
        }
    }
    
    // Get veterinarian profile by ID (public endpoint)
    @GetMapping("/{profileId}")
    public ResponseEntity<?> getVeterinarianProfileById(@PathVariable Long profileId) {
        try {
            Optional<VetProfileResponse> profile = veterinarianProfileService.getVeterinarianProfile(profileId);
            if (profile.isPresent()) {
                return ResponseEntity.ok(new ApiResponse("success", "Profile retrieved successfully", profile.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve profile: " + e.getMessage(), null));
        }
    }
    
    // Get all available veterinarians
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableVeterinarians() {
        try {
            List<VetProfileResponse> profiles = veterinarianProfileService.getAvailableVeterinarians();
            return ResponseEntity.ok(new ApiResponse("success", "Available veterinarians retrieved successfully", profiles));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve veterinarians: " + e.getMessage(), null));
        }
    }
    
    // Search veterinarians
    @GetMapping("/search")
    public ResponseEntity<?> searchVeterinarians(@RequestParam String keyword) {
        try {
            List<VetProfileResponse> profiles = veterinarianProfileService.searchVeterinarians(keyword);
            return ResponseEntity.ok(new ApiResponse("success", "Search completed successfully", profiles));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Search failed: " + e.getMessage(), null));
        }
    }
    
    // Get veterinarians by specialty
    @GetMapping("/specialty/{specialty}")
    public ResponseEntity<?> getVeterinariansBySpecialty(@PathVariable String specialty) {
        try {
            List<VetProfileResponse> profiles = veterinarianProfileService.getVeterinariansBySpecialty(specialty);
            return ResponseEntity.ok(new ApiResponse("success", "Veterinarians retrieved successfully", profiles));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve veterinarians: " + e.getMessage(), null));
        }
    }
    
    // Get veterinarians by clinic
    @GetMapping("/clinic")
    public ResponseEntity<?> getVeterinariansByClinic(@RequestParam String clinic) {
        try {
            List<VetProfileResponse> profiles = veterinarianProfileService.getVeterinariansByClinic(clinic);
            return ResponseEntity.ok(new ApiResponse("success", "Veterinarians retrieved successfully", profiles));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve veterinarians: " + e.getMessage(), null));
        }
    }
    
    // Get top rated veterinarians
    @GetMapping("/top-rated")
    public ResponseEntity<?> getTopRatedVeterinarians() {
        try {
            List<VetProfileResponse> profiles = veterinarianProfileService.getTopRatedVeterinarians();
            return ResponseEntity.ok(new ApiResponse("success", "Top rated veterinarians retrieved successfully", profiles));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve veterinarians: " + e.getMessage(), null));
        }
    }
    
    // Get veterinarians by rating range
    @GetMapping("/rating")
    public ResponseEntity<?> getVeterinariansByRating(
            @RequestParam(defaultValue = "0.0") Double minRating,
            @RequestParam(defaultValue = "0") Integer minReviews) {
        try {
            List<VetProfileResponse> profiles = veterinarianProfileService.getVeterinariansByRating(minRating, minReviews);
            return ResponseEntity.ok(new ApiResponse("success", "Veterinarians retrieved successfully", profiles));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve veterinarians: " + e.getMessage(), null));
        }
    }
    
    // Get veterinarians by consultation fee range
    @GetMapping("/fee-range")
    public ResponseEntity<?> getVeterinariansByFeeRange(
            @RequestParam Double minFee,
            @RequestParam Double maxFee) {
        try {
            List<VetProfileResponse> profiles = veterinarianProfileService.getVeterinariansByFeeRange(minFee, maxFee);
            return ResponseEntity.ok(new ApiResponse("success", "Veterinarians retrieved successfully", profiles));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve veterinarians: " + e.getMessage(), null));
        }
    }
    
    // Get veterinarians by experience
    @GetMapping("/experience")
    public ResponseEntity<?> getVeterinariansByExperience(@RequestParam Integer minYears) {
        try {
            List<VetProfileResponse> profiles = veterinarianProfileService.getVeterinariansByExperience(minYears);
            return ResponseEntity.ok(new ApiResponse("success", "Veterinarians retrieved successfully", profiles));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve veterinarians: " + e.getMessage(), null));
        }
    }
    
    // Get veterinarian statistics
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getVeterinarianStats() {
        try {
            VeterinarianProfileService.VeterinarianStats stats = veterinarianProfileService.getVeterinarianStats();
            return ResponseEntity.ok(new ApiResponse("success", "Statistics retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve statistics: " + e.getMessage(), null));
        }
    }
    
    // Update verification status (Admin only)
    @PutMapping("/{profileId}/verification")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateVerificationStatus(
            @PathVariable Long profileId,
            @RequestParam boolean isVerified) {
        try {
            boolean updated = veterinarianProfileService.updateVerificationStatus(profileId, isVerified);
            if (updated) {
                String status = isVerified ? "verified" : "unverified";
                return ResponseEntity.ok(new ApiResponse("success", "Profile " + status + " successfully", null));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to update verification status: " + e.getMessage(), null));
        }
    }
    
    // Delete profile (Admin only)
    @DeleteMapping("/{profileId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProfile(@PathVariable Long profileId) {
        try {
            boolean deleted = veterinarianProfileService.deleteProfile(profileId);
            if (deleted) {
                return ResponseEntity.ok(new ApiResponse("success", "Profile deleted successfully", null));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to delete profile: " + e.getMessage(), null));
        }
    }
    
    // API Response class
    public static class ApiResponse {
        private String status;
        private String message;
        private Object data;
        
        public ApiResponse(String status, String message, Object data) {
            this.status = status;
            this.message = message;
            this.data = data;
        }
        
        // Getters and setters
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
    }
}
