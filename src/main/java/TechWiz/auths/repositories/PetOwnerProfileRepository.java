package TechWiz.auths.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import TechWiz.auths.models.PetOwnerProfile;
import TechWiz.auths.models.User;

import java.util.Optional;

@Repository
public interface PetOwnerProfileRepository extends JpaRepository<PetOwnerProfile, Long> {
    
    Optional<PetOwnerProfile> findByUser(User user);
    
    Optional<PetOwnerProfile> findByUserId(Long userId);
    
    Boolean existsByUser(User user);
}
