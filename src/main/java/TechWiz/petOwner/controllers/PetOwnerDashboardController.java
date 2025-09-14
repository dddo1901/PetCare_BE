package TechWiz.petOwner.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import TechWiz.petOwner.services.PetOwnerDashboardService;

import java.util.Map;

@RestController
@RequestMapping("/api/pet-owner/dashboard")
@CrossOrigin(origins = "*")
public class PetOwnerDashboardController {

    @Autowired
    private PetOwnerDashboardService dashboardService;

    @GetMapping("/stats")
    // @PreAuthorize("hasRole('PET_OWNER')")
    public ResponseEntity<?> getDashboardStats(Authentication authentication, HttpServletRequest request) {
        try {
            Long ownerId = getOwnerIdFromRequest(request);
            Map<String, Object> stats = dashboardService.getDashboardStats(ownerId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Dashboard statistics retrieved successfully",
                "data", stats
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/recent-activities")
    // @PreAuthorize("hasRole('PET_OWNER')")
    public ResponseEntity<?> getRecentActivities(Authentication authentication, HttpServletRequest request) {
        try {
            Long ownerId = getOwnerIdFromRequest(request);
            Map<String, Object> activities = dashboardService.getRecentActivities(ownerId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Recent activities retrieved successfully",
                "data", activities
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/analytics")
    // @PreAuthorize("hasRole('PET_OWNER')")
    public ResponseEntity<?> getAnalytics(Authentication authentication, HttpServletRequest request) {
        try {
            Long ownerId = getOwnerIdFromRequest(request);
            Map<String, Object> analytics = dashboardService.getAnalytics(ownerId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Analytics data retrieved successfully",
                "data", analytics
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    private Long getOwnerIdFromRequest(HttpServletRequest request) {
        try {
            // Get user ID from request attributes set by JWT filter
            Object userIdObj = request.getAttribute("userId");
            if (userIdObj != null) {
                return Long.valueOf(userIdObj.toString());
            } else {
                return 3L; // Fallback for testing
            }
        } catch (Exception e) {
            return 3L; // Fallback for testing
        }
    }

    private Long getOwnerIdFromAuth(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            System.out.println("No authentication found, using fallback owner ID");
            return 3L; // Fallback for testing
        }
        
        try {
            // Get user ID from authentication principal
            Object principal = authentication.getPrincipal();
            System.out.println("Authentication principal: " + principal);
            System.out.println("Principal class: " + principal.getClass().getName());
            
            if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                // If it's UserDetails, we need to get the user ID differently
                // For now, let's use a different approach
                return 3L; // Fallback for testing
            } else {
                // Try to extract user ID from principal
                String principalStr = principal.toString();
                System.out.println("Principal string: " + principalStr);
                return Long.valueOf(principalStr);
            }
        } catch (Exception e) {
            System.out.println("Error extracting owner ID from authentication: " + e.getMessage());
            return 3L; // Fallback for testing
        }
    }
}
