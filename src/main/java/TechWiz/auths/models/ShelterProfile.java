package TechWiz.auths.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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

@Entity
@Table(name = "shelter_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShelterProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String shelterName;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String contactPersonName;

    @Column
    private String registrationNumber;

    @Column
    private String website;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private Integer capacity;

    @Column
    private Integer currentOccupancy = 0;

    @ElementCollection
    @CollectionTable(name = "shelter_images", joinColumns = @JoinColumn(name = "shelter_profile_id"))
    @Column(name = "image_url")
    private java.util.List<String> images;

    @Column(nullable = false)
    private Boolean isVerified = false;

    @Column(nullable = false)
    private Boolean acceptsDonations = false;

    @Column
    private String operatingHours;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
