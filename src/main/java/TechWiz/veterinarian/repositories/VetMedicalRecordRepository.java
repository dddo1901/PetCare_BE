package TechWiz.veterinarian.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import TechWiz.veterinarian.models.VetMedicalRecord;

@Repository
public interface VetMedicalRecordRepository extends JpaRepository<VetMedicalRecord, Long> {
    
    // Find medical records by appointment ID
    Optional<VetMedicalRecord> findByAppointmentId(Long appointmentId);
    
    // Find medical records by veterinarian ID
    List<VetMedicalRecord> findByVeterinarianIdOrderByCreatedAtDesc(Long veterinarianId);
    
    // Find medical records by pet ID
    List<VetMedicalRecord> findByPetIdOrderByCreatedAtDesc(Long petId);
    
    // Find medical records by pet owner ID
    List<VetMedicalRecord> findByPetOwnerIdOrderByCreatedAtDesc(Long petOwnerId);
    
    // Find medical records by diagnosis
    @Query("SELECT mr FROM VetMedicalRecord mr WHERE LOWER(mr.diagnosis) LIKE LOWER(CONCAT('%', :diagnosis, '%')) " +
           "ORDER BY mr.createdAt DESC")
    List<VetMedicalRecord> findByDiagnosisContainingIgnoreCase(@Param("diagnosis") String diagnosis);
    
    // Find medical records by symptoms
    @Query("SELECT mr FROM VetMedicalRecord mr WHERE LOWER(mr.symptoms) LIKE LOWER(CONCAT('%', :symptom, '%')) " +
           "ORDER BY mr.createdAt DESC")
    List<VetMedicalRecord> findBySymptomsContainingIgnoreCase(@Param("symptom") String symptom);
    
    // Find records with follow-up required
    List<VetMedicalRecord> findByFollowUpRequiredTrueAndFollowUpDateAfterOrderByFollowUpDateAsc(LocalDateTime now);
    
    // Find records by pet with date range
    @Query("SELECT mr FROM VetMedicalRecord mr WHERE mr.petId = :petId AND " +
           "mr.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY mr.createdAt DESC")
    List<VetMedicalRecord> findByPetAndDateRange(@Param("petId") Long petId, 
                                                @Param("startDate") LocalDateTime startDate, 
                                                @Param("endDate") LocalDateTime endDate);
    
    // Find records by veterinarian with date range
    @Query("SELECT mr FROM VetMedicalRecord mr WHERE mr.veterinarianId = :vetId AND " +
           "mr.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY mr.createdAt DESC")
    List<VetMedicalRecord> findByVeterinarianAndDateRange(@Param("vetId") Long veterinarianId, 
                                                         @Param("startDate") LocalDateTime startDate, 
                                                         @Param("endDate") LocalDateTime endDate);
    
    // Search medical records by keyword
    @Query("SELECT mr FROM VetMedicalRecord mr WHERE " +
           "LOWER(mr.symptoms) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(mr.diagnosis) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(mr.treatment) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(mr.medications) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(mr.notes) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "ORDER BY mr.createdAt DESC")
    List<VetMedicalRecord> searchMedicalRecords(@Param("keyword") String keyword);
    
    // Find recent records for pet (last 6 months)
    @Query("SELECT mr FROM VetMedicalRecord mr WHERE mr.petId = :petId AND " +
           "mr.createdAt >= :sixMonthsAgo " +
           "ORDER BY mr.createdAt DESC")
    List<VetMedicalRecord> findRecentRecordsByPet(@Param("petId") Long petId, @Param("sixMonthsAgo") LocalDateTime sixMonthsAgo);
    
    // Get pet's medical history summary
    @Query("SELECT mr.diagnosis, COUNT(mr) FROM VetMedicalRecord mr WHERE mr.petId = :petId " +
           "GROUP BY mr.diagnosis ORDER BY COUNT(mr) DESC")
    List<Object[]> getPetDiagnosisHistory(@Param("petId") Long petId);
    
    // Find records with specific test results
    @Query("SELECT mr FROM VetMedicalRecord mr WHERE " +
           "JSON_CONTAINS(mr.testResults, JSON_OBJECT('testName', :testName))")
    List<VetMedicalRecord> findByTestResultsContaining(@Param("testName") String testName);
    
    // Find records by medication
    @Query("SELECT mr FROM VetMedicalRecord mr WHERE LOWER(mr.medications) LIKE LOWER(CONCAT('%', :medication, '%')) " +
           "ORDER BY mr.createdAt DESC")
    List<VetMedicalRecord> findByMedicationsContainingIgnoreCase(@Param("medication") String medication);
    
    // Count records by veterinarian
    long countByVeterinarianId(Long veterinarianId);
    
    // Count records by pet
    long countByPetId(Long petId);
    
    // Find overdue follow-ups
    @Query("SELECT mr FROM VetMedicalRecord mr WHERE mr.followUpRequired = true AND " +
           "mr.followUpDate < :now AND mr.followUpCompleted = false " +
           "ORDER BY mr.followUpDate ASC")
    List<VetMedicalRecord> findOverdueFollowUps(@Param("now") LocalDateTime now);
    
    // Find upcoming follow-ups for veterinarian
    @Query("SELECT mr FROM VetMedicalRecord mr WHERE mr.veterinarianId = :vetId AND " +
           "mr.followUpRequired = true AND mr.followUpDate BETWEEN :now AND :futureDate AND " +
           "mr.followUpCompleted = false " +
           "ORDER BY mr.followUpDate ASC")
    List<VetMedicalRecord> findUpcomingFollowUps(@Param("vetId") Long veterinarianId, 
                                                @Param("now") LocalDateTime now, 
                                                @Param("futureDate") LocalDateTime futureDate);
    
    // Get veterinarian's medical record statistics
    @Query("SELECT COUNT(mr), " +
           "COUNT(CASE WHEN mr.followUpRequired = true THEN 1 END), " +
           "COUNT(CASE WHEN mr.followUpRequired = true AND mr.followUpCompleted = false THEN 1 END) " +
           "FROM VetMedicalRecord mr WHERE mr.veterinarianId = :vetId")
    Object[] getVeterinarianRecordStats(@Param("vetId") Long veterinarianId);
    
    // Find records by treatment type
    @Query("SELECT mr FROM VetMedicalRecord mr WHERE LOWER(mr.treatment) LIKE LOWER(CONCAT('%', :treatment, '%')) " +
           "ORDER BY mr.createdAt DESC")
    List<VetMedicalRecord> findByTreatmentContainingIgnoreCase(@Param("treatment") String treatment);
    
    // Check if appointment has medical record
    boolean existsByAppointmentId(Long appointmentId);
    
    // Find records that need follow-up reminders
    @Query("SELECT mr FROM VetMedicalRecord mr WHERE mr.followUpRequired = true AND " +
           "mr.followUpDate BETWEEN :reminderStart AND :reminderEnd AND " +
           "mr.followUpCompleted = false " +
           "ORDER BY mr.followUpDate ASC")
    List<VetMedicalRecord> findRecordsNeedingFollowUpReminder(@Param("reminderStart") LocalDateTime reminderStart,
                                                             @Param("reminderEnd") LocalDateTime reminderEnd);
}
