package TechWiz.veterinarian.dto;

import jakarta.validation.constraints.*;
import java.util.List;
import java.util.Map;

public class VetProfileUpdateRequest {
    
    @NotBlank(message = "License number is required")
    private String licenseNumber;
    
    @NotBlank(message = "Clinic name is required")
    private String clinic;
    
    @NotBlank(message = "Clinic address is required")
    private String clinicAddress;
    
    private List<String> specialties;
    
    private Map<String, Object> education;
    
    private Map<String, Object> experience;
    
    @Min(value = 0, message = "Years of experience must be non-negative")
    private Integer yearsExperience;
    
    private Map<String, Object> workingHours;
    
    @DecimalMin(value = "0.0", message = "Consultation fee must be non-negative")
    private Double consultationFee;
    
    private Boolean emergencyAvailable;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    // Constructors
    public VetProfileUpdateRequest() {}
    
    public VetProfileUpdateRequest(String licenseNumber, String clinic, String clinicAddress) {
        this.licenseNumber = licenseNumber;
        this.clinic = clinic;
        this.clinicAddress = clinicAddress;
    }
    
    // Getters and setters
    public String getLicenseNumber() {
        return licenseNumber;
    }
    
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
    
    public String getClinic() {
        return clinic;
    }
    
    public void setClinic(String clinic) {
        this.clinic = clinic;
    }
    
    public String getClinicAddress() {
        return clinicAddress;
    }
    
    public void setClinicAddress(String clinicAddress) {
        this.clinicAddress = clinicAddress;
    }
    
    public List<String> getSpecialties() {
        return specialties;
    }
    
    public void setSpecialties(List<String> specialties) {
        this.specialties = specialties;
    }
    
    public Map<String, Object> getEducation() {
        return education;
    }
    
    public void setEducation(Map<String, Object> education) {
        this.education = education;
    }
    
    public Map<String, Object> getExperience() {
        return experience;
    }
    
    public void setExperience(Map<String, Object> experience) {
        this.experience = experience;
    }
    
    public Integer getYearsExperience() {
        return yearsExperience;
    }
    
    public void setYearsExperience(Integer yearsExperience) {
        this.yearsExperience = yearsExperience;
    }
    
    public Map<String, Object> getWorkingHours() {
        return workingHours;
    }
    
    public void setWorkingHours(Map<String, Object> workingHours) {
        this.workingHours = workingHours;
    }
    
    public Double getConsultationFee() {
        return consultationFee;
    }
    
    public void setConsultationFee(Double consultationFee) {
        this.consultationFee = consultationFee;
    }
    
    public Boolean getEmergencyAvailable() {
        return emergencyAvailable;
    }
    
    public void setEmergencyAvailable(Boolean emergencyAvailable) {
        this.emergencyAvailable = emergencyAvailable;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
