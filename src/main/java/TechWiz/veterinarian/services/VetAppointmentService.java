package TechWiz.veterinarian.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import TechWiz.veterinarian.dto.AppointmentResponse;
import TechWiz.veterinarian.dto.AppointmentUpdateRequest;
import TechWiz.veterinarian.models.VetAppointment;
import TechWiz.veterinarian.models.VetAppointment.AppointmentStatus;
import TechWiz.veterinarian.repositories.VetAppointmentRepository;

@Service
@Transactional
public class VetAppointmentService {
    
    @Autowired
    private VetAppointmentRepository appointmentRepository;
    
    // Get appointments for veterinarian
    public List<AppointmentResponse> getVeterinarianAppointments(Long veterinarianId) {
        List<VetAppointment> appointments = appointmentRepository.findByVeterinarianIdOrderByAppointmentDateTimeDesc(veterinarianId);
        return appointments.stream().map(this::convertToResponse).toList();
    }
    
    // Get appointments by status for veterinarian
    public List<AppointmentResponse> getVeterinarianAppointmentsByStatus(Long veterinarianId, AppointmentStatus status) {
        List<VetAppointment> appointments = appointmentRepository.findByVeterinarianIdAndStatusOrderByAppointmentDateTimeDesc(veterinarianId, status);
        return appointments.stream().map(this::convertToResponse).toList();
    }
    
    // Get pending appointments for veterinarian
    public List<AppointmentResponse> getPendingAppointments(Long veterinarianId) {
        List<AppointmentStatus> pendingStatuses = Arrays.asList(AppointmentStatus.PENDING, AppointmentStatus.CONFIRMED);
        List<VetAppointment> appointments = appointmentRepository.findByVeterinarianIdAndStatusInOrderByAppointmentDateTimeAsc(veterinarianId, pendingStatuses);
        return appointments.stream().map(this::convertToResponse).toList();
    }
    
    // Get upcoming appointments for veterinarian
    public List<AppointmentResponse> getUpcomingAppointments(Long veterinarianId) {
        List<VetAppointment> appointments = appointmentRepository.findUpcomingAppointments(veterinarianId, LocalDateTime.now());
        return appointments.stream().map(this::convertToResponse).toList();
    }
    
    // Get past appointments for veterinarian
    public List<AppointmentResponse> getPastAppointments(Long veterinarianId) {
        List<VetAppointment> appointments = appointmentRepository.findPastAppointments(veterinarianId, LocalDateTime.now());
        return appointments.stream().map(this::convertToResponse).toList();
    }
    
    // Get today's appointments for veterinarian
    public List<AppointmentResponse> getTodayAppointments(Long veterinarianId) {
        List<VetAppointment> appointments = appointmentRepository.findByVeterinarianAndDate(veterinarianId, LocalDate.now());
        return appointments.stream().map(this::convertToResponse).toList();
    }
    
    // Get appointments by date range
    public List<AppointmentResponse> getAppointmentsByDateRange(Long veterinarianId, LocalDateTime startDate, LocalDateTime endDate) {
        List<VetAppointment> appointments = appointmentRepository.findByVeterinarianAndDateRange(veterinarianId, startDate, endDate);
        return appointments.stream().map(this::convertToResponse).toList();
    }
    
    // Get appointment by ID
    public Optional<AppointmentResponse> getAppointmentById(Long appointmentId) {
        Optional<VetAppointment> appointment = appointmentRepository.findById(appointmentId);
        return appointment.map(this::convertToResponse);
    }
    
    // Update appointment status
    public Optional<AppointmentResponse> updateAppointmentStatus(Long appointmentId, AppointmentUpdateRequest request) {
        Optional<VetAppointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isPresent()) {
            VetAppointment appointment = appointmentOpt.get();
            
            if (request.getStatus() != null) {
                appointment.setStatus(request.getStatus());
            }
            if (request.getVetNotes() != null) {
                appointment.setVetNotes(request.getVetNotes());
            }
            if (request.getRejectionReason() != null) {
                appointment.setRejectionReason(request.getRejectionReason());
            }
            
            appointment.setUpdatedAt(LocalDateTime.now());
            VetAppointment savedAppointment = appointmentRepository.save(appointment);
            return Optional.of(convertToResponse(savedAppointment));
        }
        return Optional.empty();
    }
    
    // Confirm appointment
    public Optional<AppointmentResponse> confirmAppointment(Long appointmentId, String vetNotes) {
        Optional<VetAppointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isPresent()) {
            VetAppointment appointment = appointmentOpt.get();
            appointment.setStatus(AppointmentStatus.CONFIRMED);
            if (vetNotes != null && !vetNotes.trim().isEmpty()) {
                appointment.setVetNotes(vetNotes);
            }
            appointment.setUpdatedAt(LocalDateTime.now());
            VetAppointment savedAppointment = appointmentRepository.save(appointment);
            return Optional.of(convertToResponse(savedAppointment));
        }
        return Optional.empty();
    }
    
    // Reject appointment
    public Optional<AppointmentResponse> rejectAppointment(Long appointmentId, String rejectionReason) {
        Optional<VetAppointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isPresent()) {
            VetAppointment appointment = appointmentOpt.get();
            appointment.setStatus(AppointmentStatus.REJECTED);
            appointment.setRejectionReason(rejectionReason);
            appointment.setUpdatedAt(LocalDateTime.now());
            VetAppointment savedAppointment = appointmentRepository.save(appointment);
            return Optional.of(convertToResponse(savedAppointment));
        }
        return Optional.empty();
    }
    
    // Reschedule appointment
    public Optional<AppointmentResponse> rescheduleAppointment(Long appointmentId, LocalDateTime newDateTime, String vetNotes) {
        Optional<VetAppointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isPresent()) {
            VetAppointment appointment = appointmentOpt.get();
            
            // Check if new time slot is available
            boolean isTimeSlotAvailable = !appointmentRepository.existsByVeterinarianAndDateTimeExcludingId(
                appointment.getVeterinarianId(), newDateTime, appointmentId);
            
            if (!isTimeSlotAvailable) {
                throw new RuntimeException("Time slot is not available");
            }
            
            // Store original appointment ID for tracking
            if (appointment.getOriginalAppointmentId() == null) {
                appointment.setOriginalAppointmentId(appointmentId);
            }
            
            appointment.setAppointmentDateTime(newDateTime);
            appointment.setStatus(AppointmentStatus.RESCHEDULED);
            if (vetNotes != null && !vetNotes.trim().isEmpty()) {
                appointment.setVetNotes(vetNotes);
            }
            appointment.setUpdatedAt(LocalDateTime.now());
            
            VetAppointment savedAppointment = appointmentRepository.save(appointment);
            return Optional.of(convertToResponse(savedAppointment));
        }
        return Optional.empty();
    }
    
    // Complete appointment
    public Optional<AppointmentResponse> completeAppointment(Long appointmentId, String vetNotes) {
        Optional<VetAppointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isPresent()) {
            VetAppointment appointment = appointmentOpt.get();
            appointment.setStatus(AppointmentStatus.COMPLETED);
            if (vetNotes != null && !vetNotes.trim().isEmpty()) {
                appointment.setVetNotes(vetNotes);
            }
            appointment.setUpdatedAt(LocalDateTime.now());
            VetAppointment savedAppointment = appointmentRepository.save(appointment);
            return Optional.of(convertToResponse(savedAppointment));
        }
        return Optional.empty();
    }
    
    // Cancel appointment
    public Optional<AppointmentResponse> cancelAppointment(Long appointmentId, String cancellationReason) {
        Optional<VetAppointment> appointmentOpt = appointmentRepository.findById(appointmentId);
        if (appointmentOpt.isPresent()) {
            VetAppointment appointment = appointmentOpt.get();
            appointment.setStatus(AppointmentStatus.CANCELLED);
            appointment.setRejectionReason(cancellationReason);
            appointment.setUpdatedAt(LocalDateTime.now());
            VetAppointment savedAppointment = appointmentRepository.save(appointment);
            return Optional.of(convertToResponse(savedAppointment));
        }
        return Optional.empty();
    }
    
    // Check if time slot is available
    public boolean isTimeSlotAvailable(Long veterinarianId, LocalDateTime dateTime) {
        return !appointmentRepository.existsByVeterinarianAndDateTime(veterinarianId, dateTime);
    }
    
    // Get completed appointments without medical records
    public List<AppointmentResponse> getCompletedAppointmentsWithoutMedicalRecords(Long veterinarianId) {
        List<VetAppointment> appointments = appointmentRepository.findCompletedAppointmentsWithoutMedicalRecords(veterinarianId);
        return appointments.stream().map(this::convertToResponse).toList();
    }
    
    // Get appointment statistics for veterinarian
    public AppointmentStats getAppointmentStats(Long veterinarianId) {
        List<Object[]> stats = appointmentRepository.getAppointmentStatsByVeterinarian(veterinarianId);
        AppointmentStats appointmentStats = new AppointmentStats();
        
        for (Object[] stat : stats) {
            AppointmentStatus status = (AppointmentStatus) stat[0];
            Long count = (Long) stat[1];
            
            switch (status) {
                case PENDING -> appointmentStats.setPendingCount(count);
                case CONFIRMED -> appointmentStats.setConfirmedCount(count);
                case COMPLETED -> appointmentStats.setCompletedCount(count);
                case REJECTED -> appointmentStats.setRejectedCount(count);
                case CANCELLED -> appointmentStats.setCancelledCount(count);
                case RESCHEDULED -> appointmentStats.setRescheduledCount(count);
            }
        }
        
        return appointmentStats;
    }
    
    // Auto-complete past confirmed appointments
    @Transactional
    public void autoCompleteAppointments() {
        // Mark appointments as completed if they're 2 hours past their scheduled time
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(2);
        List<VetAppointment> appointmentsToComplete = appointmentRepository.findAppointmentsToMarkCompleted(cutoffTime);
        
        for (VetAppointment appointment : appointmentsToComplete) {
            appointment.setStatus(AppointmentStatus.COMPLETED);
            appointment.setUpdatedAt(LocalDateTime.now());
            appointmentRepository.save(appointment);
        }
    }
    
    // Get appointments count by date range
    public long getAppointmentCount(Long veterinarianId, LocalDateTime startDate, LocalDateTime endDate) {
        return appointmentRepository.countByVeterinarianAndDateRange(veterinarianId, startDate, endDate);
    }
    
    // Get rescheduled appointments
    public List<AppointmentResponse> getRescheduledAppointments(Long veterinarianId) {
        List<VetAppointment> appointments = appointmentRepository.findByVeterinarianIdAndStatusOrderByAppointmentDateTimeDesc(
            veterinarianId, AppointmentStatus.RESCHEDULED);
        return appointments.stream().map(this::convertToResponse).toList();
    }
    
    // Helper method to convert entity to response
    private AppointmentResponse convertToResponse(VetAppointment appointment) {
        AppointmentResponse response = new AppointmentResponse();
        response.setId(appointment.getId());
        response.setVeterinarianId(appointment.getVeterinarianId());
        response.setPetOwnerId(appointment.getPetOwnerId());
        response.setPetId(appointment.getPetId());
        response.setAppointmentDateTime(appointment.getAppointmentDateTime());
        response.setReason(appointment.getReason());
        response.setStatus(appointment.getStatus());
        response.setVetNotes(appointment.getVetNotes());
        response.setRejectionReason(appointment.getRejectionReason());
        response.setOriginalAppointmentId(appointment.getOriginalAppointmentId());
        response.setCreatedAt(appointment.getCreatedAt());
        response.setUpdatedAt(appointment.getUpdatedAt());
        return response;
    }
    
    // Statistics class
    public static class AppointmentStats {
        private Long pendingCount = 0L;
        private Long confirmedCount = 0L;
        private Long completedCount = 0L;
        private Long rejectedCount = 0L;
        private Long cancelledCount = 0L;
        private Long rescheduledCount = 0L;
        
        // Getters and setters
        public Long getPendingCount() { return pendingCount; }
        public void setPendingCount(Long pendingCount) { this.pendingCount = pendingCount; }
        
        public Long getConfirmedCount() { return confirmedCount; }
        public void setConfirmedCount(Long confirmedCount) { this.confirmedCount = confirmedCount; }
        
        public Long getCompletedCount() { return completedCount; }
        public void setCompletedCount(Long completedCount) { this.completedCount = completedCount; }
        
        public Long getRejectedCount() { return rejectedCount; }
        public void setRejectedCount(Long rejectedCount) { this.rejectedCount = rejectedCount; }
        
        public Long getCancelledCount() { return cancelledCount; }
        public void setCancelledCount(Long cancelledCount) { this.cancelledCount = cancelledCount; }
        
        public Long getRescheduledCount() { return rescheduledCount; }
        public void setRescheduledCount(Long rescheduledCount) { this.rescheduledCount = rescheduledCount; }
        
        public Long getTotalCount() {
            return pendingCount + confirmedCount + completedCount + rejectedCount + cancelledCount + rescheduledCount;
        }
    }
}
