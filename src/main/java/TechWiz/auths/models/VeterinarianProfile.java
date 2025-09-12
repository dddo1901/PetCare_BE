package TechWiz.auths.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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

    @ElementCollection
    @CollectionTable(name = "vet_specializations", joinColumns = @JoinColumn(name = "vet_profile_id"))
    @Column(name = "specialization")
    private List<String> specializations;

    @Column
    private String clinicName;

    @Column
    private String clinicAddress;

    @Column
    private LocalTime availableFromTime;

    @Column
    private LocalTime availableToTime;

    @ElementCollection
    @CollectionTable(name = "vet_available_days", joinColumns = @JoinColumn(name = "vet_profile_id"))
    @Column(name = "day")
    private List<String> availableDays;

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
}
