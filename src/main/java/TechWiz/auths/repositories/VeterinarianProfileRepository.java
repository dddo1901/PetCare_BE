package TechWiz.auths.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import TechWiz.auths.models.VeterinarianProfile;
import TechWiz.auths.models.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface VeterinarianProfileRepository extends JpaRepository<VeterinarianProfile, Long> {
    
    Optional<VeterinarianProfile> findByUser(User user);
    
    Optional<VeterinarianProfile> findByUserId(Long userId);
    
    Boolean existsByUser(User user);
    
    List<VeterinarianProfile> findByIsProfileCompleteTrue();
    
    List<VeterinarianProfile> findByIsAvailableForEmergencyTrue();
    
    List<VeterinarianProfile> findBySpecializationsContaining(String specialization);
}
