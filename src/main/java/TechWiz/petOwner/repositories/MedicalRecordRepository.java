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

import TechWiz.petOwner.models.MedicalRecord;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    
    // Find medical records by pet
    List<MedicalRecord> findByPetIdOrderByVisitDateDesc(Long petId);
    
    Page<MedicalRecord> findByPetIdOrderByVisitDateDesc(Long petId, Pageable pageable);
    
    // Find medical records by pet and record type
    List<MedicalRecord> findByPetIdAndRecordTypeOrderByVisitDateDesc(Long petId, String recordType);
    
    // Find medical records by owner (through pet)
    @Query("SELECT mr FROM MedicalRecord mr JOIN mr.pet p WHERE p.ownerId = :ownerId " +
           "ORDER BY mr.visitDate DESC")
    List<MedicalRecord> findByOwnerIdOrderByVisitDateDesc(@Param("ownerId") Long ownerId);
    
    // Find recent medical records
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.pet.id = :petId AND mr.visitDate >= :fromDate " +
           "ORDER BY mr.visitDate DESC")
    List<MedicalRecord> findRecentRecords(@Param("petId") Long petId, @Param("fromDate") LocalDate fromDate);
    
    // Search medical records by diagnosis or treatment
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.pet.id = :petId AND " +
           "(LOWER(mr.diagnosis) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(mr.treatment) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(mr.notes) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY mr.visitDate DESC")
    List<MedicalRecord> searchRecordsByPet(@Param("petId") Long petId, @Param("keyword") String keyword);
    
    // Find records by veterinarian
    List<MedicalRecord> findByVeterinarianIdOrderByVisitDateDesc(Long veterinarianId);
    
    // Count records by pet
    long countByPetId(Long petId);
    
    // Find records by date range
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.pet.id = :petId AND " +
           "mr.visitDate BETWEEN :startDate AND :endDate ORDER BY mr.visitDate DESC")
    List<MedicalRecord> findByPetIdAndDateRange(@Param("petId") Long petId, 
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);
    
    // Check if record belongs to owner's pet
    @Query("SELECT CASE WHEN COUNT(mr) > 0 THEN true ELSE false END FROM MedicalRecord mr " +
           "JOIN mr.pet p WHERE mr.id = :recordId AND p.ownerId = :ownerId")
    boolean existsByIdAndOwnerId(@Param("recordId") Long recordId, @Param("ownerId") Long ownerId);
    
    // Find record by ID ensuring it belongs to owner
    @Query("SELECT mr FROM MedicalRecord mr JOIN mr.pet p WHERE mr.id = :recordId AND p.ownerId = :ownerId")
    Optional<MedicalRecord> findByIdAndOwnerId(@Param("recordId") Long recordId, @Param("ownerId") Long ownerId);
}
