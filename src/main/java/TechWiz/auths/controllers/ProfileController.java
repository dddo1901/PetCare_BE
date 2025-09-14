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

import TechWiz.auths.models.dto.ApiResponse;
import TechWiz.auths.models.dto.UpdatePetOwnerProfileRequest;
import TechWiz.auths.models.dto.UpdateShelterProfileRequest;
import TechWiz.auths.models.dto.UpdateVeterinarianProfileRequest;
import TechWiz.auths.models.dto.UserProfileResponse;
import TechWiz.auths.services.ProfileService;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "http://localhost:3000")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @PutMapping("/pet-owner")
    public ResponseEntity<ApiResponse> updatePetOwnerProfile(
            @AuthenticationPrincipal String email,
            @RequestBody UpdatePetOwnerProfileRequest request) {
        
        UserProfileResponse.PetOwnerProfileData updatedProfile = 
                profileService.updatePetOwnerProfile(email, request);
        
        if (updatedProfile != null) {
            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updatedProfile));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update pet owner profile"));
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
    public ResponseEntity<ApiResponse> getPetOwnerProfile(@AuthenticationPrincipal String email) {
        UserProfileResponse.PetOwnerProfileData profile = 
                profileService.getPetOwnerProfile(email);
        
        if (profile != null) {
            return ResponseEntity.ok(ApiResponse.success("Pet owner profile retrieved", profile));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get pet owner profile"));
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