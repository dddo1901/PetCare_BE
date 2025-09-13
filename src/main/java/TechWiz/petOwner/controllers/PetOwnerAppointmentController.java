package TechWiz.petOwner.controllers;

import TechWiz.auths.models.User;
import TechWiz.petOwner.dto.PetOwnerAppointmentRequest;
import TechWiz.petOwner.dto.PetOwnerAppointmentResponse;
import TechWiz.petOwner.services.PetOwnerAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pet-owner/appointments")
@PreAuthorize("hasRole('PET_OWNER')")
@CrossOrigin(origins = "*")
public class PetOwnerAppointmentController {

    @Autowired
    private PetOwnerAppointmentService appointmentService;

    @GetMapping
    public ResponseEntity<?> getAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String status,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            
            if (status != null && !status.isEmpty()) {
                List<PetOwnerAppointmentResponse> appointments = appointmentService.getAppointmentsByStatus(user.getId(), status);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Appointments retrieved successfully",
                    "data", appointments
                ));
            } else if (size > 0) {
                Pageable pageable = PageRequest.of(page, size);
                Page<PetOwnerAppointmentResponse> appointments = appointmentService.getAppointmentsByOwner(user.getId(), pageable);
                
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
                List<PetOwnerAppointmentResponse> appointments = appointmentService.getAppointmentsByOwner(user.getId());
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
            List<PetOwnerAppointmentResponse> appointments = appointmentService.getUpcomingAppointments(user.getId());
            
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

    @GetMapping("/history")
    public ResponseEntity<?> getPastAppointments(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            List<PetOwnerAppointmentResponse> appointments = appointmentService.getPastAppointments(user.getId());
            
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

    @PostMapping
    public ResponseEntity<?> createAppointment(@Valid @RequestBody PetOwnerAppointmentRequest request, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            PetOwnerAppointmentResponse appointment = appointmentService.createAppointment(request, user.getId());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Appointment created successfully",
                "data", appointment
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/{appointmentId}")
    public ResponseEntity<?> updateAppointment(@PathVariable Long appointmentId, 
                                              @Valid @RequestBody PetOwnerAppointmentRequest request, 
                                              Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            PetOwnerAppointmentResponse appointment = appointmentService.updateAppointment(appointmentId, request, user.getId());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Appointment updated successfully",
                "data", appointment
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long appointmentId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            boolean deleted = appointmentService.deleteAppointment(appointmentId, user.getId());
            
            if (deleted) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Appointment deleted successfully"
                ));
            } else {
                return ResponseEntity.notFound().build();
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
            
            Map<String, Object> stats = Map.of(
                "total", appointmentService.getAppointmentCountByStatus(user.getId(), "PENDING") + 
                        appointmentService.getAppointmentCountByStatus(user.getId(), "CONFIRMED") + 
                        appointmentService.getAppointmentCountByStatus(user.getId(), "COMPLETED"),
                "pending", appointmentService.getAppointmentCountByStatus(user.getId(), "PENDING"),
                "confirmed", appointmentService.getAppointmentCountByStatus(user.getId(), "CONFIRMED"),
                "completed", appointmentService.getAppointmentCountByStatus(user.getId(), "COMPLETED")
            );
            
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
    @PreAuthorize("hasRole('PET_OWNER')")
    public ResponseEntity<?> checkAppointmentConflicts(@RequestBody Map<String, Object> request) {
        try {
            Long vetId = Long.valueOf(request.get("vetId").toString());
            String appointmentDateTimeStr = request.get("appointmentDateTime").toString();
            LocalDateTime appointmentDateTime = LocalDateTime.parse(appointmentDateTimeStr);
            
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
