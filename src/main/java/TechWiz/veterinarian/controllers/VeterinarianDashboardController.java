package TechWiz.veterinarian.controllers;

import TechWiz.auths.models.User;
import TechWiz.veterinarian.dto.VeterinarianDashboardResponse;
import TechWiz.veterinarian.services.VeterinarianDashboardService;
import TechWiz.veterinarian.services.VeterinarianProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/veterinarian/dashboard")
@PreAuthorize("hasRole('VETERINARIAN')")
@CrossOrigin(origins = "*")
public class VeterinarianDashboardController {
    
    @Autowired
    private VeterinarianDashboardService dashboardService;
    
    @Autowired
    private VeterinarianProfileService veterinarianProfileService;
    
    @GetMapping
    public ResponseEntity<?> getDashboardData(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Long vetId = veterinarianProfileService.getVetIdByUserId(user.getId());
            VeterinarianDashboardResponse dashboardData = dashboardService.getDashboardData(vetId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Dashboard data retrieved successfully",
                "data", dashboardData
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Long vetId = veterinarianProfileService.getVetIdByUserId(user.getId());
            VeterinarianDashboardResponse dashboardData = dashboardService.getDashboardData(vetId);
            
            // Extract only statistics
            Map<String, Object> statistics = Map.of(
                "appointments", Map.of(
                    "total", dashboardData.getTotalAppointments(),
                    "pending", dashboardData.getPendingAppointments(),
                    "confirmed", dashboardData.getConfirmedAppointments(),
                    "completed", dashboardData.getCompletedAppointments(),
                    "cancelled", dashboardData.getCancelledAppointments(),
                    "today", dashboardData.getTodayAppointments(),
                    "upcoming", dashboardData.getUpcomingAppointments()
                ),
                "medicalRecords", Map.of(
                    "total", dashboardData.getTotalMedicalRecords()
                ),
                "financial", Map.of(
                    "totalRevenue", dashboardData.getTotalRevenue(),
                    "monthlyRevenue", dashboardData.getMonthlyRevenue(),
                    "averageConsultationFee", dashboardData.getAverageConsultationFee()
                )
            );
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Statistics retrieved successfully",
                "data", statistics
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}
