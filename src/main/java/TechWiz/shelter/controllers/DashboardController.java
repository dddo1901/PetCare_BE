package TechWiz.shelter.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import TechWiz.auths.services.ProfileService;
import TechWiz.shelter.services.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;
    
    @Autowired
    private ProfileService profileService;

    @GetMapping("/shelter")
    public ResponseEntity<?> getShelterDashboard(@AuthenticationPrincipal String email) {
        try {
            System.out.println("DEBUG: getShelterDashboard called for email: " + email);
            
            // Get shelter profile to get shelter ID
            var shelterProfile = profileService.getShelterProfile(email);
            if (shelterProfile == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Shelter profile not found");
                return ResponseEntity.badRequest().body(response);
            }
            
            Long shelterId = shelterProfile.getProfileId();
            System.out.println("DEBUG: Getting dashboard data for shelterId: " + shelterId);
            
            // Get dashboard data
            var dashboardData = dashboardService.getShelterDashboardData(shelterId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", dashboardData);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("DEBUG: Error getting shelter dashboard: " + e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}