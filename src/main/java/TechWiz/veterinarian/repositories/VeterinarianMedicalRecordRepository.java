package TechWiz.veterinarian.repositories;

import TechWiz.veterinarian.models.VeterinarianMedicalRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VeterinarianMedicalRecordRepository extends JpaRepository<VeterinarianMedicalRecord, Long> {
    
    // Find medical records by vet ID
    List<VeterinarianMedicalRecord> findByVetIdOrderByRecordDateDesc(Long vetId);
    
    // Find medical records by vet ID with pagination
    Page<VeterinarianMedicalRecord> findByVetIdOrderByRecordDateDesc(Long vetId, Pageable pageable);
    
    // Find medical records by pet ID
    List<VeterinarianMedicalRecord> findByPetIdOrderByRecordDateDesc(Long petId);
    
    // Find medical records by vet ID and pet ID
    List<VeterinarianMedicalRecord> findByVetIdAndPetIdOrderByRecordDateDesc(Long vetId, Long petId);
    
    // Find medical records by appointment ID
    List<VeterinarianMedicalRecord> findByAppointmentIdOrderByRecordDateDesc(Long appointmentId);
    
    // Find medical records by type
    List<VeterinarianMedicalRecord> findByVetIdAndTypeOrderByRecordDateDesc(Long vetId, VeterinarianMedicalRecord.RecordType type);
    
    // Find recent medical records for a vet
    @Query("SELECT r FROM VeterinarianMedicalRecord r WHERE r.vetId = :vetId AND r.recordDate >= :since ORDER BY r.recordDate DESC")
    List<VeterinarianMedicalRecord> findRecentMedicalRecords(@Param("vetId") Long vetId, @Param("since") LocalDateTime since);
    
    // Count medical records by type for a vet
    Long countByVetIdAndType(Long vetId, VeterinarianMedicalRecord.RecordType type);
    
    // Find medical records in date range
    @Query("SELECT r FROM VeterinarianMedicalRecord r WHERE r.vetId = :vetId AND r.recordDate BETWEEN :startDate AND :endDate ORDER BY r.recordDate DESC")
    List<VeterinarianMedicalRecord> findMedicalRecordsInDateRange(@Param("vetId") Long vetId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Calculate total revenue for a vet
    @Query("SELECT COALESCE(SUM(r.treatmentCost), 0) FROM VeterinarianMedicalRecord r WHERE r.vetId = :vetId")
    Double calculateTotalRevenueByVetId(@Param("vetId") Long vetId);
    
    // Calculate monthly revenue for a vet
    @Query("SELECT COALESCE(SUM(r.treatmentCost), 0) FROM VeterinarianMedicalRecord r WHERE r.vetId = :vetId AND YEAR(r.recordDate) = :year AND MONTH(r.recordDate) = :month")
    Double calculateMonthlyRevenueByVetId(@Param("vetId") Long vetId, @Param("year") int year, @Param("month") int month);
    
    // Find medical records by diagnosis (for search)
    @Query("SELECT r FROM VeterinarianMedicalRecord r WHERE r.vetId = :vetId AND LOWER(r.diagnosis) LIKE LOWER(CONCAT('%', :diagnosis, '%')) ORDER BY r.recordDate DESC")
    List<VeterinarianMedicalRecord> findByVetIdAndDiagnosisContainingIgnoreCase(@Param("vetId") Long vetId, @Param("diagnosis") String diagnosis);
}
