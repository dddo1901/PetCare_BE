package TechWiz.petOwner.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import TechWiz.petOwner.models.Pet;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    
    // Find pets by owner
    List<Pet> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);
    
    Page<Pet> findByOwnerIdOrderByCreatedAtDesc(Long ownerId, Pageable pageable);
    
    // Find pets by owner and type
    List<Pet> findByOwnerIdAndTypeOrderByCreatedAtDesc(Long ownerId, String type);
    
    // Search pets by owner with name or breed
    @Query("SELECT p FROM PetOwnerPet p WHERE p.ownerId = :ownerId AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.breed) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY p.createdAt DESC")
    List<Pet> searchPetsByOwner(@Param("ownerId") Long ownerId, @Param("keyword") String keyword);
    
    // Find pets by health status
    List<Pet> findByOwnerIdAndHealthStatusOrderByCreatedAtDesc(Long ownerId, String healthStatus);
    
    // Find pets needing vaccination
    @Query("SELECT p FROM PetOwnerPet p WHERE p.ownerId = :ownerId AND p.nextVaccination <= :date " +
           "ORDER BY p.nextVaccination ASC")
    List<Pet> findPetsNeedingVaccination(@Param("ownerId") Long ownerId, @Param("date") LocalDate date);
    
    // Find pets needing checkup (last checkup > 6 months ago)
    @Query("SELECT p FROM PetOwnerPet p WHERE p.ownerId = :ownerId AND " +
           "(p.lastCheckup IS NULL OR p.lastCheckup < :sixMonthsAgo) " +
           "ORDER BY p.lastCheckup ASC")
    List<Pet> findPetsNeedingCheckup(@Param("ownerId") Long ownerId, @Param("sixMonthsAgo") LocalDate sixMonthsAgo);
    
    // Count pets by owner
    long countByOwnerId(Long ownerId);
    
    // Count pets by owner and type
    long countByOwnerIdAndType(Long ownerId, String type);
    
    // Check if pet belongs to owner
    boolean existsByIdAndOwnerId(Long petId, Long ownerId);
    
    // Find pet by ID and owner (security check)
    Optional<Pet> findByIdAndOwnerId(Long petId, Long ownerId);
}
