package TechWiz.petOwner.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import TechWiz.auths.models.User;
import TechWiz.petOwner.dto.CreateAppointmentRequest;
import TechWiz.petOwner.models.Appointment;
import TechWiz.petOwner.services.AppointmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pet-owner/appointments")
@PreAuthorize("hasRole('PET_OWNER')")
@CrossOrigin(origins = "*")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    // Create new appointment
    @PostMapping
    public ResponseEntity<?> createAppointment(@Valid @RequestBody CreateAppointmentRequest request, 
                                             Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Appointment appointment = appointmentService.createAppointment(request, user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Appointment scheduled successfully",
                "data", appointment
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get appointments by owner
    @GetMapping
    public ResponseEntity<?> getAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            
            if (size > 0) {
                Pageable pageable = PageRequest.of(page, size);
                Page<Appointment> appointments = appointmentService.getAppointmentsByOwner(user.getId(), pageable);
                
                return ResponseEntity.ok().body(java.util.Map.of(
                    "success", true,
                    "message", "Appointments retrieved successfully",
                    "data", appointments.getContent(),
                    "pagination", java.util.Map.of(
                        "currentPage", appointments.getNumber(),
                        "totalPages", appointments.getTotalPages(),
                        "totalElements", appointments.getTotalElements(),
                        "pageSize", appointments.getSize()
                    )
                ));
            } else {
                List<Appointment> appointments = appointmentService.getAppointmentsByOwner(user.getId());
                return ResponseEntity.ok().body(java.util.Map.of(
                    "success", true,
                    "message", "Appointments retrieved successfully",
                    "data", appointments
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get appointment by ID
    @GetMapping("/{appointmentId}")
    public ResponseEntity<?> getAppointmentById(@PathVariable Long appointmentId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Optional<Appointment> appointment = appointmentService.getAppointmentById(appointmentId, user.getId());
            
            if (appointment.isPresent()) {
                return ResponseEntity.ok().body(java.util.Map.of(
                    "success", true,
                    "message", "Appointment retrieved successfully",
                    "data", appointment.get()
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get appointments by pet
    @GetMapping("/pet/{petId}")
    public ResponseEntity<?> getAppointmentsByPet(@PathVariable Long petId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            List<Appointment> appointments = appointmentService.getAppointmentsByPet(petId, user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Pet appointments retrieved successfully",
                "data", appointments
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Update appointment
    @PutMapping("/{appointmentId}")
    public ResponseEntity<?> updateAppointment(@PathVariable Long appointmentId,
                                             @Valid @RequestBody CreateAppointmentRequest request,
                                             Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Appointment appointment = appointmentService.updateAppointment(appointmentId, request, user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Appointment updated successfully",
                "data", appointment
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Cancel appointment
    @PatchMapping("/{appointmentId}/cancel")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long appointmentId,
                                             @RequestParam(required = false) String reason,
                                             Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Appointment appointment = appointmentService.cancelAppointment(appointmentId, reason, user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Appointment cancelled successfully",
                "data", appointment
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Reschedule appointment
    @PatchMapping("/{appointmentId}/reschedule")
    public ResponseEntity<?> rescheduleAppointment(@PathVariable Long appointmentId,
                                                  @RequestParam String newDateTime,
                                                  Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            LocalDateTime newDate = LocalDateTime.parse(newDateTime);
            Appointment appointment = appointmentService.rescheduleAppointment(appointmentId, newDate, user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Appointment rescheduled successfully",
                "data", appointment
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get upcoming appointments
    @GetMapping("/upcoming")
    public ResponseEntity<?> getUpcomingAppointments(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            List<Appointment> appointments = appointmentService.getUpcomingAppointments(user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Upcoming appointments retrieved successfully",
                "data", appointments
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get past appointments
    @GetMapping("/history")
    public ResponseEntity<?> getPastAppointments(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            List<Appointment> appointments = appointmentService.getPastAppointments(user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Appointment history retrieved successfully",
                "data", appointments
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get appointments by status
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getAppointmentsByStatus(@PathVariable String status, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            List<Appointment> appointments = appointmentService.getAppointmentsByStatus(user.getId(), status);
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Appointments retrieved successfully",
                "data", appointments
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get appointments in date range
    @GetMapping("/date-range")
    public ResponseEntity<?> getAppointmentsInDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            List<Appointment> appointments = appointmentService.getAppointmentsInDateRange(user.getId(), start, end);
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Appointments retrieved successfully",
                "data", appointments
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Confirm appointment
    @PatchMapping("/{appointmentId}/confirm")
    public ResponseEntity<?> confirmAppointment(@PathVariable Long appointmentId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Appointment appointment = appointmentService.confirmAppointment(appointmentId, user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Appointment confirmed successfully",
                "data", appointment
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get available time slots
    @GetMapping("/available-slots")
    public ResponseEntity<?> getAvailableTimeSlots(
            @RequestParam Long veterinarianId,
            @RequestParam String date) {
        try {
            LocalDateTime targetDate = LocalDate.parse(date).atStartOfDay();
            List<LocalDateTime> availableSlots = appointmentService.getAvailableTimeSlots(veterinarianId, targetDate);
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Available time slots retrieved successfully",
                "data", availableSlots
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get appointment statistics
    @GetMapping("/statistics")
    public ResponseEntity<?> getAppointmentStatistics(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            
            java.util.Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("scheduled", appointmentService.countAppointmentsByStatus(user.getId(), "SCHEDULED"));
            stats.put("confirmed", appointmentService.countAppointmentsByStatus(user.getId(), "CONFIRMED"));
            stats.put("completed", appointmentService.countAppointmentsByStatus(user.getId(), "COMPLETED"));
            stats.put("cancelled", appointmentService.countAppointmentsByStatus(user.getId(), "CANCELLED"));
            stats.put("upcoming", appointmentService.getUpcomingAppointments(user.getId()).size());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Appointment statistics retrieved successfully",
                "data", stats
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}
