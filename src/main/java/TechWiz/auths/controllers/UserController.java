package TechWiz.auths.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import TechWiz.auths.models.dto.ApiResponse;
import TechWiz.auths.models.dto.UserProfileResponse;
import TechWiz.auths.services.AuthService;
import TechWiz.auths.services.ProfileService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private AuthService authService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getUserProfile(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("User not authenticated"));
            }

            UserProfileResponse userProfile = profileService.getUserProfileWithRoleData(userId);
            
            if (userProfile == null) {
                return ResponseEntity.ok(ApiResponse.error("User not found"));
            }

            return ResponseEntity.ok(ApiResponse.success("User profile retrieved successfully", userProfile));

        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("Failed to get user profile: " + e.getMessage()));
        }
    }

    @PutMapping("/avatar")
    public ResponseEntity<ApiResponse> updateUserAvatar(
            HttpServletRequest request, 
            @RequestBody UpdateAvatarRequest updateRequest) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            
            if (userId == null) {
                return ResponseEntity.ok(ApiResponse.error("User not authenticated"));
            }

            // Get user email from userId
            var userOptional = authService.findById(userId);
            if (userOptional.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("User not found"));
            }

            String email = userOptional.get().getEmail();
            var updatedUser = authService.updateUserAvatar(email, updateRequest.getProfileImageUrl());

            return ResponseEntity.ok(ApiResponse.success("Avatar updated successfully", updatedUser));

        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("Failed to update avatar: " + e.getMessage()));
        }
    }

    // Request DTO
    public static class UpdateAvatarRequest {
        private String profileImageUrl;

        public String getProfileImageUrl() {
            return profileImageUrl;
        }

        public void setProfileImageUrl(String profileImageUrl) {
            this.profileImageUrl = profileImageUrl;
        }
    }

    // Removed inner class - using the one from dto package
}
