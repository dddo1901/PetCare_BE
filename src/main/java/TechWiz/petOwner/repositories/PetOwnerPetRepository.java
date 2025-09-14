package TechWiz.petOwner.repositories;

import TechWiz.petOwner.models.PetOwnerPet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PetOwnerPetRepository extends JpaRepository<PetOwnerPet, Long> {
    
    // Find pets by owner ID
    List<PetOwnerPet> findByOwnerId(Long ownerId);
    
    // Find pets by owner ID with pagination
    Page<PetOwnerPet> findByOwnerId(Long ownerId, Pageable pageable);
    
    // Search pets by owner ID and keyword (name or breed)
    @Query("SELECT p FROM PetOwnerPet p WHERE p.ownerId = :ownerId AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.breed) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<PetOwnerPet> findByOwnerIdAndKeyword(@Param("ownerId") Long ownerId, @Param("keyword") String keyword);
    
    // Find pets by owner ID and type
    List<PetOwnerPet> findByOwnerIdAndType(Long ownerId, PetOwnerPet.PetType type);
    
    // Find pets by owner ID and health status
    List<PetOwnerPet> findByOwnerIdAndHealthStatus(Long ownerId, PetOwnerPet.HealthStatus healthStatus);
    
    // Count pets by owner ID
    @Query("SELECT COUNT(p) FROM PetOwnerPet p WHERE p.ownerId = :ownerId")
    Long countByOwnerId(@Param("ownerId") Long ownerId);
    
    // Count pets by owner ID and type
    @Query("SELECT COUNT(p) FROM PetOwnerPet p WHERE p.ownerId = :ownerId AND p.type = :type")
    Long countByOwnerIdAndType(@Param("ownerId") Long ownerId, @Param("type") PetOwnerPet.PetType type);
    
    // Find pets by owner ID and health status with pagination
    Page<PetOwnerPet> findByOwnerIdAndHealthStatus(Long ownerId, PetOwnerPet.HealthStatus healthStatus, Pageable pageable);
    
    // Find pets by owner ID and type with pagination
    Page<PetOwnerPet> findByOwnerIdAndType(Long ownerId, PetOwnerPet.PetType type, Pageable pageable);
    
    // Find top 5 pets by owner ID ordered by updated date
    @Query("SELECT p FROM PetOwnerPet p WHERE p.ownerId = :ownerId ORDER BY p.updatedAt DESC")
    List<PetOwnerPet> findTop5ByOwnerIdOrderByUpdatedAtDesc(@Param("ownerId") Long ownerId);
}
