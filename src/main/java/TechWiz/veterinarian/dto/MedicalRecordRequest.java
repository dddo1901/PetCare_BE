package TechWiz.veterinarian.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class MedicalRecordRequest {
    
    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;
    
    @NotNull(message = "Veterinarian ID is required")
    private Long veterinarianId;
    
    @NotNull(message = "Pet owner ID is required")
    private Long petOwnerId;
    
    @NotNull(message = "Pet ID is required")
    private Long petId;
    
    @NotBlank(message = "Symptoms are required")
    @Size(max = 2000, message = "Symptoms cannot exceed 2000 characters")
    private String symptoms;
    
    @NotBlank(message = "Diagnosis is required")
    @Size(max = 1000, message = "Diagnosis cannot exceed 1000 characters")
    private String diagnosis;
    
    @NotBlank(message = "Treatment is required")
    @Size(max = 2000, message = "Treatment cannot exceed 2000 characters")
    private String treatment;
    
    @Size(max = 2000, message = "Medications cannot exceed 2000 characters")
    private String medications;
    
    private List<TestResult> testResults;
    
    @Size(max = 2000, message = "Notes cannot exceed 2000 characters")
    private String notes;
    
    private Boolean followUpRequired = false;
    
    private LocalDateTime followUpDate;
    
    @Size(max = 1000, message = "Follow-up notes cannot exceed 1000 characters")
    private String followUpNotes;
    
    // Constructors
    public MedicalRecordRequest() {}
    
    // Getters and setters
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
    
    public String getFollowUpNotes() {
        return followUpNotes;
    }
    
    public void setFollowUpNotes(String followUpNotes) {
        this.followUpNotes = followUpNotes;
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
