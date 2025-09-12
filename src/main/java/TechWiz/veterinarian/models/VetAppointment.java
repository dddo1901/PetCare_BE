package TechWiz.veterinarian.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "vet_appointments")
public class VetAppointment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "veterinarian_id", nullable = false)
    private Long veterinarianId;
    
    @Column(name = "pet_owner_id", nullable = false)
    private Long petOwnerId;
    
    @Column(name = "pet_id", nullable = false)
    private Long petId;
    
    @Column(name = "appointment_datetime", nullable = false)
    private LocalDateTime appointmentDateTime;
    
    @Column(columnDefinition = "TEXT")
    private String reason;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AppointmentStatus status = AppointmentStatus.PENDING;
    
    @Column(name = "vet_notes", columnDefinition = "TEXT")
    private String vetNotes;
    
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;
    
    @Column(name = "original_appointment_id")
    private Long originalAppointmentId; // For tracking rescheduled appointments
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public VetAppointment() {}
    
    public VetAppointment(Long veterinarianId, Long petOwnerId, Long petId, LocalDateTime appointmentDateTime, String reason) {
        this.veterinarianId = veterinarianId;
        this.petOwnerId = petOwnerId;
        this.petId = petId;
        this.appointmentDateTime = appointmentDateTime;
        this.reason = reason;
    }
    
    // Getters and Setters
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
    
    public enum AppointmentStatus {
        PENDING,
        CONFIRMED,
        COMPLETED,
        CANCELLED,
        REJECTED,
        RESCHEDULED
    }
}
