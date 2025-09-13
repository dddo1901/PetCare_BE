package TechWiz.petOwner.repositories;

import TechWiz.petOwner.models.PetOwnerNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetOwnerNotificationRepository extends JpaRepository<PetOwnerNotification, Long> {
    
    List<PetOwnerNotification> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);
    
    List<PetOwnerNotification> findByOwnerIdAndIsReadOrderByCreatedAtDesc(Long ownerId, Boolean isRead);
    
    Long countByOwnerIdAndIsRead(Long ownerId, Boolean isRead);
}
