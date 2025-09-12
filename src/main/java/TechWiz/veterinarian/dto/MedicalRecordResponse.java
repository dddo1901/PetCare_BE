package TechWiz.veterinarian.dto;

import java.time.LocalDateTime;
import java.util.List;

public class MedicalRecordResponse {
    
    private Long id;
    private Long appointmentId;
    private Long veterinarianId;
    private Long petOwnerId;
    private Long petId;
    private String symptoms;
    private String diagnosis;
    private String treatment;
    private String medications;
    private List<TestResult> testResults;
    private String notes;
    private Boolean followUpRequired;
    private LocalDateTime followUpDate;
    private Boolean followUpCompleted;
    private LocalDateTime followUpCompletedAt;
    private String followUpNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public MedicalRecordResponse() {}
    
    // Getters and setters
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
    
    public List<TestResult> getTestResults() {
        return testResults;
    }
    
    public void setTestResults(List<TestResult> testResults) {
        this.testResults = testResults;
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
    
    // Nested classes
    public static class TestResult {
        private String testName;
        private String result;
        private String unit;
        private String normalRange;
        private String notes;
        
        // Constructors
        public TestResult() {}
        
        public TestResult(String testName, String result) {
            this.testName = testName;
            this.result = result;
        }
        
        // Getters and setters
        public String getTestName() {
            return testName;
        }
        
        public void setTestName(String testName) {
            this.testName = testName;
        }
        
        public String getResult() {
            return result;
        }
        
        public void setResult(String result) {
            this.result = result;
        }
        
        public String getUnit() {
            return unit;
        }
        
        public void setUnit(String unit) {
            this.unit = unit;
        }
        
        public String getNormalRange() {
            return normalRange;
        }
        
        public void setNormalRange(String normalRange) {
            this.normalRange = normalRange;
        }
        
        public String getNotes() {
            return notes;
        }
        
        public void setNotes(String notes) {
            this.notes = notes;
        }
    }
}
