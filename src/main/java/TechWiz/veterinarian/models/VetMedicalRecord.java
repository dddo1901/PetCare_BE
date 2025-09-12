package TechWiz.veterinarian.models;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.*;

@Entity
@Table(name = "vet_medical_records")
public class VetMedicalRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "appointment_id", nullable = false)
    private Long appointmentId;
    
    @Column(name = "veterinarian_id", nullable = false)
    private Long veterinarianId;
    
    @Column(name = "pet_owner_id", nullable = false)
    private Long petOwnerId;
    
    @Column(name = "pet_id", nullable = false)
    private Long petId;
    
    @Column(columnDefinition = "TEXT")
    private String symptoms;
    
    @Column(columnDefinition = "TEXT")
    private String diagnosis;
    
    @Column(columnDefinition = "TEXT")
    private String treatment;
    
    @Column(columnDefinition = "TEXT")
    private String medications;
    
    @Column(name = "test_results", columnDefinition = "JSON")
    private String testResultsJson;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "follow_up_required", nullable = false)
    private Boolean followUpRequired = false;
    
    @Column(name = "follow_up_date")
    private LocalDateTime followUpDate;
    
    @Column(name = "follow_up_completed", nullable = false)
    private Boolean followUpCompleted = false;
    
    @Column(name = "follow_up_completed_at")
    private LocalDateTime followUpCompletedAt;
    
    @Column(name = "follow_up_notes", columnDefinition = "TEXT")
    private String followUpNotes;
    
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
    public VetMedicalRecord() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getAppointmentId() {
        return appointmentId;
    }
    
    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
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
    
    public String getSymptoms() {
        return symptoms;
    }
    
    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }
    
    public String getDiagnosis() {
        return diagnosis;
    }
    
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }
    
    public String getTreatment() {
        return treatment;
    }
    
    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }
    
    public String getMedications() {
        return medications;
    }
    
    public void setMedications(String medications) {
        this.medications = medications;
    }
    
    // Helper methods for test results JSON handling
    public List<Object> getTestResults() {
        if (testResultsJson == null) return null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(testResultsJson, new TypeReference<List<Object>>() {});
        } catch (Exception e) {
            return null;
        }
    }
    
    public void setTestResults(List<Object> testResults) {
        if (testResults == null) {
            this.testResultsJson = null;
            return;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.testResultsJson = mapper.writeValueAsString(testResults);
        } catch (Exception e) {
            this.testResultsJson = null;
        }
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Boolean getFollowUpRequired() {
        return followUpRequired;
    }
    
    public void setFollowUpRequired(Boolean followUpRequired) {
        this.followUpRequired = followUpRequired;
    }
    
    public LocalDateTime getFollowUpDate() {
        return followUpDate;
    }
    
    public void setFollowUpDate(LocalDateTime followUpDate) {
        this.followUpDate = followUpDate;
    }
    
    public Boolean getFollowUpCompleted() {
        return followUpCompleted;
    }
    
    public void setFollowUpCompleted(Boolean followUpCompleted) {
        this.followUpCompleted = followUpCompleted;
    }
    
    public LocalDateTime getFollowUpCompletedAt() {
        return followUpCompletedAt;
    }
    
    public void setFollowUpCompletedAt(LocalDateTime followUpCompletedAt) {
        this.followUpCompletedAt = followUpCompletedAt;
    }
    
    public String getFollowUpNotes() {
        return followUpNotes;
    }
    
    public void setFollowUpNotes(String followUpNotes) {
        this.followUpNotes = followUpNotes;
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
