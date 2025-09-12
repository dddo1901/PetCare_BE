package TechWiz.auths.models;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "AuthVeterinarianProfile")
@Table(name = "auth_veterinarian_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VeterinarianProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String address;

    @Column
    private String licenseNumber;

    @Column
    private Integer experienceYears;

    @Column(columnDefinition = "TEXT")
    private String specializations; // Store as comma-separated values

    @Column
    private String clinicName;

    @Column
    private String clinicAddress;

    @Column
    private LocalTime availableFromTime;

    @Column
    private LocalTime availableToTime;

    @Column(columnDefinition = "TEXT")
    private String availableDays; // Store as comma-separated values

    @Column
    private String profileImageUrl;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column
    private Double consultationFee;

    @Column(nullable = false)
    private Boolean isAvailableForEmergency = false;

    @Column(nullable = false)
    private Boolean isProfileComplete = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Utility methods for specializations
    public java.util.List<String> getSpecializationsList() {
        if (specializations == null || specializations.trim().isEmpty()) {
            return new java.util.ArrayList<>();
        }
        return java.util.Arrays.asList(specializations.split(","));
    }

    public void setSpecializationsList(java.util.List<String> specializationsList) {
        if (specializationsList == null || specializationsList.isEmpty()) {
            this.specializations = "";
        } else {
            this.specializations = String.join(",", specializationsList);
        }
    }

    // Utility methods for available days
    public java.util.List<String> getAvailableDaysList() {
        if (availableDays == null || availableDays.trim().isEmpty()) {
            return new java.util.ArrayList<>();
        }
        return java.util.Arrays.asList(availableDays.split(","));
    }

    public void setAvailableDaysList(java.util.List<String> availableDaysList) {
        if (availableDaysList == null || availableDaysList.isEmpty()) {
            this.availableDays = "";
        } else {
            this.availableDays = String.join(",", availableDaysList);
        }
    }
}
