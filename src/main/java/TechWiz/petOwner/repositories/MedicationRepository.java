package TechWiz.petOwner.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import TechWiz.petOwner.models.Medication;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long> {
    
    // Find medications by pet
    List<Medication> findByPetIdOrderByCreatedAtDesc(Long petId);
    
    // Find active medications for pet (those that are still active status)
    @Query("SELECT m FROM Medication m WHERE m.pet.id = :petId AND " +
           "m.status = 'Active' " +
           "ORDER BY m.createdAt DESC")
    List<Medication> findActiveMedicationsByPet(@Param("petId") Long petId);
    
    // Find medications by pet and medication name
    List<Medication> findByPetIdAndNameContainingIgnoreCaseOrderByCreatedAtDesc(Long petId, String name);
    
    // Find medications by status
    List<Medication> findByPetIdAndStatusOrderByCreatedAtDesc(Long petId, String status);
    
    // Find medications by owner (through pet)
    @Query("SELECT m FROM Medication m JOIN m.pet p WHERE p.ownerId = :ownerId " +
           "ORDER BY m.createdAt DESC")
    List<Medication> findByOwnerIdOrderByCreatedAtDesc(@Param("ownerId") Long ownerId);
    
    // Find active medications by owner
    @Query("SELECT m FROM Medication m JOIN m.pet p WHERE p.ownerId = :ownerId AND " +
           "m.status = 'Active' " +
           "ORDER BY m.createdAt DESC")
    List<Medication> findActiveMedicationsByOwner(@Param("ownerId") Long ownerId);
    
    // Find medications by prescribed by (vet name)
    List<Medication> findByPetIdAndPrescribedByContainingIgnoreCaseOrderByCreatedAtDesc(Long petId, String prescribedBy);
    
    // Find medications due for next dose
    @Query("SELECT m FROM Medication m WHERE m.pet.id = :petId AND " +
           "m.nextDue IS NOT NULL AND m.nextDue <= :date AND " +
           "m.status = 'Active'")
    List<Medication> findMedicationsDueForDose(@Param("petId") Long petId, @Param("date") LocalDate date);
    
    // Find overdue medications
    @Query("SELECT m FROM Medication m WHERE m.pet.id = :petId AND " +
           "m.nextDue IS NOT NULL AND m.nextDue < CURRENT_DATE AND " +
           "m.status = 'Active'")
    List<Medication> findOverdueMedications(@Param("petId") Long petId);
    
    // Search medications by name or type
    @Query("SELECT m FROM Medication m WHERE m.pet.id = :petId AND " +
           "(LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.type) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.instructions) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY m.createdAt DESC")
    List<Medication> searchMedicationsByPet(@Param("petId") Long petId, @Param("keyword") String keyword);
    
    // Find medications by date range (using lastGiven and nextDue)
    @Query("SELECT m FROM Medication m WHERE m.pet.id = :petId AND " +
           "((m.lastGiven IS NOT NULL AND m.lastGiven BETWEEN :startDate AND :endDate) OR " +
           "(m.nextDue IS NOT NULL AND m.nextDue BETWEEN :startDate AND :endDate)) " +
           "ORDER BY m.createdAt DESC")
    List<Medication> findByPetIdAndDateRange(@Param("petId") Long petId, 
                                           @Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);
    
    // Count active medications by pet
    @Query("SELECT COUNT(m) FROM Medication m WHERE m.pet.id = :petId AND " +
           "m.status = 'Active'")
    long countActiveMedicationsByPet(@Param("petId") Long petId);
    
    // Check if medication belongs to owner's pet
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM Medication m " +
           "JOIN m.pet p WHERE m.id = :medicationId AND p.ownerId = :ownerId")
    boolean existsByIdAndOwnerId(@Param("medicationId") Long medicationId, @Param("ownerId") Long ownerId);
    
    // Find medication by ID ensuring it belongs to owner
    @Query("SELECT m FROM Medication m JOIN m.pet p WHERE m.id = :medicationId AND p.ownerId = :ownerId")
    Optional<Medication> findByIdAndOwnerId(@Param("medicationId") Long medicationId, @Param("ownerId") Long ownerId);
    
    // Find medications by type
    List<Medication> findByPetIdAndTypeContainingIgnoreCaseOrderByCreatedAtDesc(Long petId, String type);
    
    // Find medications that have next due date coming up
    @Query("SELECT m FROM Medication m WHERE m.pet.id = :petId AND " +
           "m.nextDue IS NOT NULL AND m.nextDue BETWEEN CURRENT_DATE AND :futureDate AND " +
           "m.status = 'Active'")
    List<Medication> findUpcomingMedications(@Param("petId") Long petId, @Param("futureDate") LocalDate futureDate);
}
