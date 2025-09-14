package TechWiz.petOwner.repositories;

import TechWiz.petOwner.models.PetOwnerHealthRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PetOwnerHealthRecordRepository extends JpaRepository<PetOwnerHealthRecord, Long> {
    
    // Find health records by pet ID and owner ID
    List<PetOwnerHealthRecord> findByPetIdAndOwnerId(Long petId, Long ownerId);
    
    // Find health records by pet ID and owner ID with pagination
    Page<PetOwnerHealthRecord> findByPetIdAndOwnerId(Long petId, Long ownerId, Pageable pageable);
    
    // Find health records by owner ID
    List<PetOwnerHealthRecord> findByOwnerId(Long ownerId);
    
    // Find health records by owner ID with pagination
    Page<PetOwnerHealthRecord> findByOwnerId(Long ownerId, Pageable pageable);
    
    // Find health records by pet ID
    List<PetOwnerHealthRecord> findByPetId(Long petId);
    
    // Find health records by type
    List<PetOwnerHealthRecord> findByType(String type);
    
    // Find health records by pet ID and type
    List<PetOwnerHealthRecord> findByPetIdAndType(Long petId, String type);
    
    // Find health records by owner ID and type
    List<PetOwnerHealthRecord> findByOwnerIdAndType(Long ownerId, String type);
    
    // Find health records by date range
    List<PetOwnerHealthRecord> findByRecordDateBetween(LocalDateTime start, LocalDateTime end);
    
    // Find health records by pet ID and date range
    List<PetOwnerHealthRecord> findByPetIdAndRecordDateBetween(Long petId, LocalDateTime start, LocalDateTime end);
    
    // Find health records by owner ID and date range
    List<PetOwnerHealthRecord> findByOwnerIdAndRecordDateBetween(Long ownerId, LocalDateTime start, LocalDateTime end);
    
    // Count health records by pet ID
    @Query("SELECT COUNT(h) FROM PetOwnerHealthRecord h WHERE h.petId = :petId")
    Long countByPetId(@Param("petId") Long petId);
    
    // Count health records by owner ID
    @Query("SELECT COUNT(h) FROM PetOwnerHealthRecord h WHERE h.ownerId = :ownerId")
    Long countByOwnerId(@Param("ownerId") Long ownerId);
    
    // Count health records by pet ID and type
    @Query("SELECT COUNT(h) FROM PetOwnerHealthRecord h WHERE h.petId = :petId AND h.type = :type")
    Long countByPetIdAndType(@Param("petId") Long petId, @Param("type") String type);
    
    // Find top 5 health records by owner ID ordered by record date
    @Query("SELECT h FROM PetOwnerHealthRecord h WHERE h.ownerId = :ownerId ORDER BY h.recordDate DESC")
    List<PetOwnerHealthRecord> findTop5ByOwnerIdOrderByRecordDateDesc(@Param("ownerId") Long ownerId);
}
