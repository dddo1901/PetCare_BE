package TechWiz.veterinarian.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class VetProfileResponse {
    
    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String licenseNumber;
    private String clinic;
    private String clinicAddress;
    private List<String> specialties;
    private Map<String, Object> education;
    private Map<String, Object> experience;
    private Integer yearsExperience;
    private Map<String, Object> workingHours;
    private Double consultationFee;
    private Boolean emergencyAvailable;
    private Double rating;
    private Integer totalReviews;
    private Boolean isAvailable;
    private Boolean isVerified;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public VetProfileResponse() {}
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
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
    
    public Double getRating() {
        return rating;
    }
    
    public void setRating(Double rating) {
        this.rating = rating;
    }
    
    public Integer getTotalReviews() {
        return totalReviews;
    }
    
    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
    }
    
    public Boolean getIsAvailable() {
        return isAvailable;
    }
    
    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
    
    public Boolean getIsVerified() {
        return isVerified;
    }
    
    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
