package TechWiz.veterinarian.dto;

import java.time.LocalDateTime;
import TechWiz.veterinarian.models.VetAppointment.AppointmentStatus;

public class AppointmentResponse {
    
    private Long id;
    private Long veterinarianId;
    private Long petOwnerId;
    private Long petId;
    private LocalDateTime appointmentDateTime;
    private String reason;
    private AppointmentStatus status;
    private String vetNotes;
    private String rejectionReason;
    private Long originalAppointmentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public AppointmentResponse() {}
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getVeterinarianId() {
        return veterinarianId;
    }
    
    public void setVeterinarianId(Long veterinarianId) {
        this.veterinarianId = veterinarianId;
    }
    
    public Long getPetOwnerId() {
        return petOwnerId;
    }
    
    public void setPetOwnerId(Long petOwnerId) {
        this.petOwnerId = petOwnerId;
    }
    
    public Long getPetId() {
        return petId;
    }
    
    public void setPetId(Long petId) {
        this.petId = petId;
    }
    
    public LocalDateTime getAppointmentDateTime() {
        return appointmentDateTime;
    }
    
    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public AppointmentStatus getStatus() {
        return status;
    }
    
    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }
    
    public String getVetNotes() {
        return vetNotes;
    }
    
    public void setVetNotes(String vetNotes) {
        this.vetNotes = vetNotes;
    }
    
    public String getRejectionReason() {
        return rejectionReason;
    }
    
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
    
    public Long getOriginalAppointmentId() {
        return originalAppointmentId;
    }
    
    public void setOriginalAppointmentId(Long originalAppointmentId) {
        this.originalAppointmentId = originalAppointmentId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
