package TechWiz.veterinarian.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import TechWiz.veterinarian.models.VeterinarianProfile;

@Repository
public interface VeterinarianProfileRepository extends JpaRepository<VeterinarianProfile, Long> {
    
    // Find veterinarian profile by user ID
    Optional<VeterinarianProfile> findByUserId(Long userId);
    
    // Find by license number
    Optional<VeterinarianProfile> findByLicenseNumber(String licenseNumber);
    
    // Find available veterinarians
    List<VeterinarianProfile> findByIsAvailableTrueAndIsVerifiedTrue();
    
    // Find by specialties (using JSON search)
    @Query("SELECT v FROM VetVeterinarianProfile v WHERE v.isAvailable = true AND v.isVerified = true AND " +
           "LOWER(v.specialties) LIKE LOWER(CONCAT('%', :specialty, '%'))")
    List<VeterinarianProfile> findBySpecialty(@Param("specialty") String specialty);
    
    // Find by clinic
    List<VeterinarianProfile> findByClinicContainingIgnoreCaseAndIsAvailableTrueAndIsVerifiedTrue(String clinic);
    
    // Find top rated veterinarians
    List<VeterinarianProfile> findByIsAvailableTrueAndIsVerifiedTrueOrderByRatingDescTotalReviewsDesc();
    
    // Search veterinarians by name, clinic, or specialty
    @Query("SELECT v FROM VetVeterinarianProfile v JOIN v.user u WHERE " +
           "v.isAvailable = true AND v.isVerified = true AND " +
           "(LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(v.clinic) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(v.specialties) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<VeterinarianProfile> searchVeterinarians(@Param("keyword") String keyword);
    
    // Get veterinarians by rating range
    @Query("SELECT v FROM VetVeterinarianProfile v WHERE v.isAvailable = true AND v.isVerified = true AND " +
           "v.rating >= :minRating AND v.totalReviews >= :minReviews " +
           "ORDER BY v.rating DESC, v.totalReviews DESC")
    List<VeterinarianProfile> findByRatingRange(@Param("minRating") Double minRating, @Param("minReviews") Integer minReviews);
    
    // Get veterinarians with consultation fee range
    @Query("SELECT v FROM VetVeterinarianProfile v WHERE v.isAvailable = true AND v.isVerified = true AND " +
           "v.consultationFee BETWEEN :minFee AND :maxFee " +
           "ORDER BY v.consultationFee ASC")
    List<VeterinarianProfile> findByConsultationFeeRange(@Param("minFee") Double minFee, @Param("maxFee") Double maxFee);
    
    // Count total veterinarians
    long countByIsVerifiedTrue();
    
    // Count available veterinarians
    long countByIsAvailableTrueAndIsVerifiedTrue();
    
    // Check if license number exists
    boolean existsByLicenseNumber(String licenseNumber);
    
    // Find veterinarians by years of experience
    @Query("SELECT v FROM VetVeterinarianProfile v WHERE v.isAvailable = true AND v.isVerified = true AND " +
           "v.yearsExperience >= :minYears ORDER BY v.yearsExperience DESC")
    List<VeterinarianProfile> findByMinYearsExperience(@Param("minYears") Integer minYears);
}
