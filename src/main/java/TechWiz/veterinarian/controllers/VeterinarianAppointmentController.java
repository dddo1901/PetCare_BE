package TechWiz.veterinarian.controllers;

import TechWiz.auths.models.User;
import TechWiz.veterinarian.dto.VeterinarianAppointmentResponse;
import TechWiz.veterinarian.services.VeterinarianAppointmentService;
import TechWiz.veterinarian.services.VeterinarianProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/veterinarian/appointments")
@PreAuthorize("hasRole('VETERINARIAN')")
@CrossOrigin(origins = "*")
public class VeterinarianAppointmentController {
    
    @Autowired
    private VeterinarianAppointmentService appointmentService;
    
    @Autowired
    private VeterinarianProfileService veterinarianProfileService;
    
    @GetMapping
    public ResponseEntity<?> getAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String status,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Long vetId = veterinarianProfileService.getVetIdByUserId(user.getId());
            
            if (status != null && !status.isEmpty()) {
                List<VeterinarianAppointmentResponse> appointments = appointmentService.getAppointmentsByStatus(vetId, status);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Appointments retrieved successfully",
                    "data", appointments
                ));
            } else if (size > 0) {
                Pageable pageable = PageRequest.of(page, size);
                Page<VeterinarianAppointmentResponse> appointments = appointmentService.getAppointmentsByVet(vetId, pageable);
                
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Appointments retrieved successfully",
                    "data", appointments.getContent(),
                    "pagination", Map.of(
                        "currentPage", appointments.getNumber(),
                        "totalPages", appointments.getTotalPages(),
                        "totalElements", appointments.getTotalElements(),
                        "pageSize", appointments.getSize()
                    )
                ));
            } else {
                List<VeterinarianAppointmentResponse> appointments = appointmentService.getAppointmentsByVet(vetId);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Appointments retrieved successfully",
                    "data", appointments
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/upcoming")
    public ResponseEntity<?> getUpcomingAppointments(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Long vetId = veterinarianProfileService.getVetIdByUserId(user.getId());
            List<VeterinarianAppointmentResponse> appointments = appointmentService.getUpcomingAppointments(vetId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Upcoming appointments retrieved successfully",
                "data", appointments
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/today")
    public ResponseEntity<?> getTodayAppointments(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Long vetId = veterinarianProfileService.getVetIdByUserId(user.getId());
            List<VeterinarianAppointmentResponse> appointments = appointmentService.getTodayAppointments(vetId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Today's appointments retrieved successfully",
                "data", appointments
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/history")
    public ResponseEntity<?> getPastAppointments(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Long vetId = veterinarianProfileService.getVetIdByUserId(user.getId());
            List<VeterinarianAppointmentResponse> appointments = appointmentService.getPastAppointments(vetId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Appointment history retrieved successfully",
                "data", appointments
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @PutMapping("/{appointmentId}/status")
    public ResponseEntity<?> updateAppointmentStatus(@PathVariable Long appointmentId, 
                                                   @RequestBody Map<String, String> request, 
                                                   Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Long vetId = veterinarianProfileService.getVetIdByUserId(user.getId());
            String status = request.get("status");
            
            // Use specific methods for approve/reject to send notifications
            boolean success = false;
            String message = "";
            
            if ("CONFIRMED".equals(status)) {
                success = appointmentService.approveAppointment(appointmentId);
                message = success ? "Appointment approved successfully. Notification and email sent to owner." : "Failed to approve appointment";
            } else if ("CANCELLED".equals(status)) {
                success = appointmentService.rejectAppointment(appointmentId);
                message = success ? "Appointment rejected successfully. Notification and email sent to owner." : "Failed to reject appointment";
            } else {
                // For other statuses, use the original method
                VeterinarianAppointmentResponse appointment = appointmentService.updateAppointmentStatus(appointmentId, status, vetId);
                success = appointment != null;
                message = success ? "Appointment status updated successfully" : "Failed to update appointment status";
            }
            
            if (success) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", message
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", message
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @PutMapping("/{appointmentId}/notes")
    public ResponseEntity<?> addVetNotes(@PathVariable Long appointmentId, 
                                        @RequestBody Map<String, String> request, 
                                        Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Long vetId = veterinarianProfileService.getVetIdByUserId(user.getId());
            String vetNotes = request.get("vetNotes");
            
            VeterinarianAppointmentResponse appointment = appointmentService.addVetNotes(appointmentId, vetNotes, vetId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Vet notes added successfully",
                "data", appointment
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @PutMapping("/{appointmentId}/reschedule")
    public ResponseEntity<?> rescheduleAppointment(@PathVariable Long appointmentId, 
                                                  @RequestBody Map<String, String> request, 
                                                  Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Long vetId = veterinarianProfileService.getVetIdByUserId(user.getId());
            String newDateTime = request.get("appointmentDateTime");
            
            boolean success = appointmentService.rescheduleAppointment(appointmentId, newDateTime);
            String message = success ? "Appointment rescheduled successfully. Notification and email sent to owner." : "Failed to reschedule appointment";
            
            if (success) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", message
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", message
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<?> getAppointmentStatistics(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Long vetId = veterinarianProfileService.getVetIdByUserId(user.getId());
            Map<String, Long> stats = appointmentService.getAppointmentStatistics(vetId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Appointment statistics retrieved successfully",
                "data", stats
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/check-conflicts")
    public ResponseEntity<?> checkAppointmentConflicts(@RequestBody Map<String, Object> request, 
                                                      Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Long vetId = veterinarianProfileService.getVetIdByUserId(user.getId());
            LocalDateTime appointmentDateTime = LocalDateTime.parse(request.get("appointmentDateTime").toString());
            
            Map<String, Object> result = appointmentService.checkAppointmentConflicts(vetId, appointmentDateTime);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Conflict check completed",
                "data", result
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}
