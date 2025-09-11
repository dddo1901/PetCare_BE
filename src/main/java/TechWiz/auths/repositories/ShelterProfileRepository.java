package TechWiz.auths.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import TechWiz.auths.models.ShelterProfile;
import TechWiz.auths.models.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShelterProfileRepository extends JpaRepository<ShelterProfile, Long> {
    
    Optional<ShelterProfile> findByUser(User user);
    
    Optional<ShelterProfile> findByUserId(Long userId);
    
    Boolean existsByUser(User user);
    
    List<ShelterProfile> findByIsVerifiedTrue();
    
    List<ShelterProfile> findByAcceptsDonationsTrue();
    
    Optional<ShelterProfile> findByShelterName(String shelterName);
}
