package TechWiz.auths.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import TechWiz.auths.configs.JwtUtils;
import TechWiz.auths.models.dto.ApiResponse;
import TechWiz.auths.models.dto.UpdatePetOwnerProfileRequest;
import TechWiz.auths.models.dto.UpdateShelterProfileRequest;
import TechWiz.auths.models.dto.UpdateVeterinarianProfileRequest;
import TechWiz.auths.models.dto.UserProfileResponse;
import TechWiz.auths.services.ProfileService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "http://localhost:3000")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private JwtUtils jwtUtils;

    private String getEmailFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String jwt = bearerToken.substring(7);
            return jwtUtils.getEmailFromJwtToken(jwt);
        }
        return null;
    }

    @PutMapping("/pet-owner")
    public ResponseEntity<ApiResponse> updatePetOwnerProfile(
            HttpServletRequest request,
            @RequestBody UpdatePetOwnerProfileRequest requestBody) {
        
        try {
            String email = getEmailFromRequest(request);
            System.out.println("DEBUG: updatePetOwnerProfile called for email: " + email);
            System.out.println("DEBUG: Request data: " + requestBody);
            
            // Validate input
            if (email == null || email.trim().isEmpty()) {
                System.out.println("ERROR: Email is null or empty - authentication failed");
                return ResponseEntity.status(401)
                    .body(ApiResponse.error("Authentication failed: Invalid token or email not found"));
            }
            
            if (requestBody == null) {
                System.out.println("ERROR: Request body is null");
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Request body cannot be null"));
            }
            
            UserProfileResponse.PetOwnerProfileData updatedProfile = 
                    profileService.updatePetOwnerProfile(email, requestBody);
            
            if (updatedProfile != null) {
                System.out.println("DEBUG: Profile updated successfully for email: " + email);
                return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updatedProfile));
            } else {
                System.out.println("ERROR: Profile update returned null for email: " + email);
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to update pet owner profile: Profile not found or update failed"));
            }
            
        } catch (IllegalArgumentException e) {
            System.err.println("ERROR: Invalid argument in updatePetOwnerProfile: " + e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Invalid input: " + e.getMessage()));
                
        } catch (SecurityException e) {
            System.err.println("ERROR: Security error in updatePetOwnerProfile: " + e.getMessage());
            return ResponseEntity.status(403)
                .body(ApiResponse.error("Access denied: " + e.getMessage()));
                
        } catch (RuntimeException e) {
            System.err.println("ERROR: Runtime exception in updatePetOwnerProfile: " + e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Update failed: " + e.getMessage()));
                
        } catch (Exception e) {
            System.err.println("ERROR: Unexpected error in updatePetOwnerProfile: " + e.getMessage());
            return ResponseEntity.status(500)
                .body(ApiResponse.error("Internal server error: " + e.getMessage()));
        }
    }

    @PutMapping("/veterinarian")
    public ResponseEntity<ApiResponse> updateVeterinarianProfile(
            @AuthenticationPrincipal String email,
            @RequestBody UpdateVeterinarianProfileRequest request) {
        
        UserProfileResponse.VeterinarianProfileData updatedProfile = 
                profileService.updateVeterinarianProfile(email, request);
        
        if (updatedProfile != null) {
            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updatedProfile));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update veterinarian profile"));
        }
    }

    @PutMapping("/shelter")
    public ResponseEntity<ApiResponse> updateShelterProfile(
            @AuthenticationPrincipal String email,
            @RequestBody UpdateShelterProfileRequest request) {
        
        UserProfileResponse.ShelterProfileData updatedProfile = 
                profileService.updateShelterProfile(email, request);
        
        if (updatedProfile != null) {
            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updatedProfile));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update shelter profile"));
        }
    }

    @GetMapping("/pet-owner")
    public ResponseEntity<ApiResponse> getPetOwnerProfile(HttpServletRequest request) {
        try {
            String email = getEmailFromRequest(request);
            System.out.println("DEBUG: getPetOwnerProfile called for email: " + email);
            
            if (email == null || email.trim().isEmpty()) {
                System.out.println("ERROR: Authentication failed - email is null or empty");
                return ResponseEntity.status(401)
                    .body(ApiResponse.error("Authentication failed: Invalid token or email not found"));
            }
            
            UserProfileResponse.PetOwnerProfileData profile = 
                    profileService.getPetOwnerProfile(email);
            
            if (profile != null) {
                System.out.println("DEBUG: Pet owner profile retrieved successfully for email: " + email);
                return ResponseEntity.ok(ApiResponse.success("Pet owner profile retrieved", profile));
            } else {
                System.out.println("ERROR: No pet owner profile found for email: " + email);
                return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get pet owner profile"));
            }
        } catch (Exception e) {
            System.err.println("ERROR: Exception in getPetOwnerProfile: " + e.getMessage());
            return ResponseEntity.status(500)
                .body(ApiResponse.error("Internal server error: " + e.getMessage()));
        }
    }

    @GetMapping("/veterinarian")
    public ResponseEntity<ApiResponse> getVeterinarianProfile(@AuthenticationPrincipal String email) {
        UserProfileResponse.VeterinarianProfileData profile = 
                profileService.getVeterinarianProfile(email);
        
        if (profile != null) {
            return ResponseEntity.ok(ApiResponse.success("Veterinarian profile retrieved", profile));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get veterinarian profile"));
        }
    }

    @GetMapping("/shelter")
    public ResponseEntity<ApiResponse> getShelterProfile(@AuthenticationPrincipal String email) {
        System.out.println("DEBUG: getShelterProfile called for email: " + email);
        
        UserProfileResponse.ShelterProfileData profile = 
                profileService.getShelterProfile(email);
        
        if (profile != null) {
            System.out.println("DEBUG: Shelter profile found with ID: " + profile.getProfileId());
            return ResponseEntity.ok(ApiResponse.success("Shelter profile retrieved", profile));
        } else {
            System.out.println("DEBUG: No shelter profile found for email: " + email);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get shelter profile"));
        }
    }
}