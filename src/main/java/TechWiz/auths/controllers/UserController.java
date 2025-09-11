package TechWiz.auths.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import TechWiz.auths.models.dto.ApiResponse;
import TechWiz.auths.models.dto.UserProfileResponse;
import TechWiz.auths.services.ProfileService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private ProfileService profileService;

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

    // Removed inner class - using the one from dto package
}
