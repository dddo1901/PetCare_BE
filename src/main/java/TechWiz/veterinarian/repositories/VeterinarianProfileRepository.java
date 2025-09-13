package TechWiz.veterinarian.repositories;

import TechWiz.auths.models.VeterinarianProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VeterinarianProfileRepository extends JpaRepository<VeterinarianProfile, Long> {
    
    // Find veterinarian profile by user ID
    Optional<VeterinarianProfile> findByUserId(Long userId);
    
    // Check if veterinarian profile exists for user
    boolean existsByUserId(Long userId);
}
