package TechWiz.veterinarian.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import TechWiz.auths.models.User;
import TechWiz.auths.repositories.UserRepository;
import TechWiz.veterinarian.dto.AppointmentResponse;
import TechWiz.veterinarian.dto.AppointmentUpdateRequest;
import TechWiz.veterinarian.models.VetAppointment.AppointmentStatus;
import TechWiz.veterinarian.services.VetAppointmentService;
import TechWiz.veterinarian.services.VeterinarianProfileService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/veterinarian/appointments")
public class VetAppointmentController {
    
    @Autowired
    private VetAppointmentService appointmentService;
    
    @Autowired
    private VeterinarianProfileService veterinarianProfileService;
    
    @Autowired
    private UserRepository userRepository;
    
    // Get current veterinarian's appointments
    @GetMapping("/me")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> getMyAppointments() {
        try {
            Long veterinarianId = getCurrentVeterinarianId();
            List<AppointmentResponse> appointments = appointmentService.getVeterinarianAppointments(veterinarianId);
            return ResponseEntity.ok(new ApiResponse("success", "Appointments retrieved successfully", appointments));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve appointments: " + e.getMessage(), null));
        }
    }
    
    // Get appointments by status
    @GetMapping("/me/status/{status}")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> getMyAppointmentsByStatus(@PathVariable AppointmentStatus status) {
        try {
            Long veterinarianId = getCurrentVeterinarianId();
            List<AppointmentResponse> appointments = appointmentService.getVeterinarianAppointmentsByStatus(veterinarianId, status);
            return ResponseEntity.ok(new ApiResponse("success", "Appointments retrieved successfully", appointments));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve appointments: " + e.getMessage(), null));
        }
    }
    
    // Get pending appointments
    @GetMapping("/me/pending")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> getMyPendingAppointments() {
        try {
            Long veterinarianId = getCurrentVeterinarianId();
            List<AppointmentResponse> appointments = appointmentService.getPendingAppointments(veterinarianId);
            return ResponseEntity.ok(new ApiResponse("success", "Pending appointments retrieved successfully", appointments));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve appointments: " + e.getMessage(), null));
        }
    }
    
    // Get upcoming appointments
    @GetMapping("/me/upcoming")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> getMyUpcomingAppointments() {
        try {
            Long veterinarianId = getCurrentVeterinarianId();
            List<AppointmentResponse> appointments = appointmentService.getUpcomingAppointments(veterinarianId);
            return ResponseEntity.ok(new ApiResponse("success", "Upcoming appointments retrieved successfully", appointments));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve appointments: " + e.getMessage(), null));
        }
    }
    
    // Get past appointments
    @GetMapping("/me/past")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> getMyPastAppointments() {
        try {
            Long veterinarianId = getCurrentVeterinarianId();
            List<AppointmentResponse> appointments = appointmentService.getPastAppointments(veterinarianId);
            return ResponseEntity.ok(new ApiResponse("success", "Past appointments retrieved successfully", appointments));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve appointments: " + e.getMessage(), null));
        }
    }
    
    // Get today's appointments
    @GetMapping("/me/today")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> getTodayAppointments() {
        try {
            Long veterinarianId = getCurrentVeterinarianId();
            List<AppointmentResponse> appointments = appointmentService.getTodayAppointments(veterinarianId);
            return ResponseEntity.ok(new ApiResponse("success", "Today's appointments retrieved successfully", appointments));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve appointments: " + e.getMessage(), null));
        }
    }
    
    // Get appointments by date range
    @GetMapping("/me/date-range")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> getMyAppointmentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            Long veterinarianId = getCurrentVeterinarianId();
            List<AppointmentResponse> appointments = appointmentService.getAppointmentsByDateRange(veterinarianId, startDate, endDate);
            return ResponseEntity.ok(new ApiResponse("success", "Appointments retrieved successfully", appointments));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve appointments: " + e.getMessage(), null));
        }
    }
    
    // Get appointment by ID
    @GetMapping("/{appointmentId}")
    @PreAuthorize("hasRole('VETERINARIAN') or hasRole('PET_OWNER')")
    public ResponseEntity<?> getAppointmentById(@PathVariable Long appointmentId) {
        try {
            Optional<AppointmentResponse> appointment = appointmentService.getAppointmentById(appointmentId);
            if (appointment.isPresent()) {
                return ResponseEntity.ok(new ApiResponse("success", "Appointment retrieved successfully", appointment.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve appointment: " + e.getMessage(), null));
        }
    }
    
    // Update appointment status
    @PutMapping("/{appointmentId}")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> updateAppointment(@PathVariable Long appointmentId, @Valid @RequestBody AppointmentUpdateRequest request) {
        try {
            Optional<AppointmentResponse> appointment = appointmentService.updateAppointmentStatus(appointmentId, request);
            if (appointment.isPresent()) {
                return ResponseEntity.ok(new ApiResponse("success", "Appointment updated successfully", appointment.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to update appointment: " + e.getMessage(), null));
        }
    }
    
    // Confirm appointment
    @PostMapping("/{appointmentId}/confirm")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> confirmAppointment(@PathVariable Long appointmentId, @RequestParam(required = false) String vetNotes) {
        try {
            Optional<AppointmentResponse> appointment = appointmentService.confirmAppointment(appointmentId, vetNotes);
            if (appointment.isPresent()) {
                return ResponseEntity.ok(new ApiResponse("success", "Appointment confirmed successfully", appointment.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to confirm appointment: " + e.getMessage(), null));
        }
    }
    
    // Reject appointment
    @PostMapping("/{appointmentId}/reject")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> rejectAppointment(@PathVariable Long appointmentId, @RequestParam String rejectionReason) {
        try {
            Optional<AppointmentResponse> appointment = appointmentService.rejectAppointment(appointmentId, rejectionReason);
            if (appointment.isPresent()) {
                return ResponseEntity.ok(new ApiResponse("success", "Appointment rejected successfully", appointment.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to reject appointment: " + e.getMessage(), null));
        }
    }
    
    // Reschedule appointment
    @PostMapping("/{appointmentId}/reschedule")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> rescheduleAppointment(
            @PathVariable Long appointmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newDateTime,
            @RequestParam(required = false) String vetNotes) {
        try {
            Optional<AppointmentResponse> appointment = appointmentService.rescheduleAppointment(appointmentId, newDateTime, vetNotes);
            if (appointment.isPresent()) {
                return ResponseEntity.ok(new ApiResponse("success", "Appointment rescheduled successfully", appointment.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Time slot is not available")) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse("error", "Time slot is not available", null));
            }
            throw e;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to reschedule appointment: " + e.getMessage(), null));
        }
    }
    
    // Complete appointment
    @PostMapping("/{appointmentId}/complete")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> completeAppointment(@PathVariable Long appointmentId, @RequestParam(required = false) String vetNotes) {
        try {
            Optional<AppointmentResponse> appointment = appointmentService.completeAppointment(appointmentId, vetNotes);
            if (appointment.isPresent()) {
                return ResponseEntity.ok(new ApiResponse("success", "Appointment completed successfully", appointment.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to complete appointment: " + e.getMessage(), null));
        }
    }
    
    // Cancel appointment
    @PostMapping("/{appointmentId}/cancel")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long appointmentId, @RequestParam String cancellationReason) {
        try {
            Optional<AppointmentResponse> appointment = appointmentService.cancelAppointment(appointmentId, cancellationReason);
            if (appointment.isPresent()) {
                return ResponseEntity.ok(new ApiResponse("success", "Appointment cancelled successfully", appointment.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to cancel appointment: " + e.getMessage(), null));
        }
    }
    
    // Check time slot availability
    @GetMapping("/me/availability")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> checkTimeSlotAvailability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime) {
        try {
            Long veterinarianId = getCurrentVeterinarianId();
            boolean isAvailable = appointmentService.isTimeSlotAvailable(veterinarianId, dateTime);
            
            return ResponseEntity.ok(new ApiResponse("success", "Availability checked successfully", 
                new TimeSlotAvailability(dateTime, isAvailable)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to check availability: " + e.getMessage(), null));
        }
    }
    
    // Get completed appointments without medical records
    @GetMapping("/me/completed-without-records")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> getCompletedAppointmentsWithoutMedicalRecords() {
        try {
            Long veterinarianId = getCurrentVeterinarianId();
            List<AppointmentResponse> appointments = appointmentService.getCompletedAppointmentsWithoutMedicalRecords(veterinarianId);
            return ResponseEntity.ok(new ApiResponse("success", "Appointments retrieved successfully", appointments));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve appointments: " + e.getMessage(), null));
        }
    }
    
    // Get appointment statistics
    @GetMapping("/me/stats")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> getMyAppointmentStats() {
        try {
            Long veterinarianId = getCurrentVeterinarianId();
            VetAppointmentService.AppointmentStats stats = appointmentService.getAppointmentStats(veterinarianId);
            return ResponseEntity.ok(new ApiResponse("success", "Statistics retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve statistics: " + e.getMessage(), null));
        }
    }
    
    // Get rescheduled appointments
    @GetMapping("/me/rescheduled")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> getMyRescheduledAppointments() {
        try {
            Long veterinarianId = getCurrentVeterinarianId();
            List<AppointmentResponse> appointments = appointmentService.getRescheduledAppointments(veterinarianId);
            return ResponseEntity.ok(new ApiResponse("success", "Rescheduled appointments retrieved successfully", appointments));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve appointments: " + e.getMessage(), null));
        }
    }
    
    // Get appointment count by date range
    @GetMapping("/me/count")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> getMyAppointmentCount(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            Long veterinarianId = getCurrentVeterinarianId();
            long count = appointmentService.getAppointmentCount(veterinarianId, startDate, endDate);
            return ResponseEntity.ok(new ApiResponse("success", "Appointment count retrieved successfully", 
                new AppointmentCount(startDate, endDate, count)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve appointment count: " + e.getMessage(), null));
        }
    }
    
    // Helper method to get current veterinarian ID
    private Long getCurrentVeterinarianId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return user.getId();
    }
    
    // Helper classes
    public static class TimeSlotAvailability {
        private LocalDateTime dateTime;
        private boolean available;
        
        public TimeSlotAvailability(LocalDateTime dateTime, boolean available) {
            this.dateTime = dateTime;
            this.available = available;
        }
        
        // Getters and setters
        public LocalDateTime getDateTime() { return dateTime; }
        public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
        
        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }
    }
    
    public static class AppointmentCount {
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private long count;
        
        public AppointmentCount(LocalDateTime startDate, LocalDateTime endDate, long count) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.count = count;
        }
        
        // Getters and setters
        public LocalDateTime getStartDate() { return startDate; }
        public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
        
        public LocalDateTime getEndDate() { return endDate; }
        public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
        
        public long getCount() { return count; }
        public void setCount(long count) { this.count = count; }
    }
    
    // API Response class
    public static class ApiResponse {
        private String status;
        private String message;
        private Object data;
        
        public ApiResponse(String status, String message, Object data) {
            this.status = status;
            this.message = message;
            this.data = data;
        }
        
        // Getters and setters
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
    }
}
