package TechWiz.veterinarian.repositories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import TechWiz.veterinarian.models.VetAppointment;
import TechWiz.veterinarian.models.VetAppointment.AppointmentStatus;

@Repository
public interface VetAppointmentRepository extends JpaRepository<VetAppointment, Long> {
    
    // Find appointments by veterinarian ID
    List<VetAppointment> findByVeterinarianIdOrderByAppointmentDateTimeDesc(Long veterinarianId);
    
    // Find appointments by pet owner ID
    List<VetAppointment> findByPetOwnerIdOrderByAppointmentDateTimeDesc(Long petOwnerId);
    
    // Find appointments by pet ID
    List<VetAppointment> findByPetIdOrderByAppointmentDateTimeDesc(Long petId);
    
    // Find appointments by status
    List<VetAppointment> findByStatusOrderByAppointmentDateTimeDesc(AppointmentStatus status);
    
    // Find appointments by veterinarian and status
    List<VetAppointment> findByVeterinarianIdAndStatusOrderByAppointmentDateTimeDesc(Long veterinarianId, AppointmentStatus status);
    
    // Find pending appointments for veterinarian
    List<VetAppointment> findByVeterinarianIdAndStatusInOrderByAppointmentDateTimeAsc(Long veterinarianId, List<AppointmentStatus> statuses);
    
    // Find appointments by date range
    @Query("SELECT a FROM VetAppointment a WHERE a.appointmentDateTime BETWEEN :startDate AND :endDate " +
           "ORDER BY a.appointmentDateTime ASC")
    List<VetAppointment> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find veterinarian appointments by date range
    @Query("SELECT a FROM VetAppointment a WHERE a.veterinarianId = :vetId AND " +
           "a.appointmentDateTime BETWEEN :startDate AND :endDate " +
           "ORDER BY a.appointmentDateTime ASC")
    List<VetAppointment> findByVeterinarianAndDateRange(@Param("vetId") Long veterinarianId, 
                                                        @Param("startDate") LocalDateTime startDate, 
                                                        @Param("endDate") LocalDateTime endDate);
    
    // Find today's appointments for veterinarian
    @Query("SELECT a FROM VetAppointment a WHERE a.veterinarianId = :vetId AND " +
           "DATE(a.appointmentDateTime) = :date " +
           "ORDER BY a.appointmentDateTime ASC")
    List<VetAppointment> findByVeterinarianAndDate(@Param("vetId") Long veterinarianId, @Param("date") LocalDate date);
    
    // Check if veterinarian has appointment at specific time
    @Query("SELECT COUNT(a) > 0 FROM VetAppointment a WHERE a.veterinarianId = :vetId AND " +
           "a.appointmentDateTime = :dateTime AND a.status IN ('PENDING', 'CONFIRMED')")
    boolean existsByVeterinarianAndDateTime(@Param("vetId") Long veterinarianId, @Param("dateTime") LocalDateTime dateTime);
    
    // Find upcoming appointments for veterinarian
    @Query("SELECT a FROM VetAppointment a WHERE a.veterinarianId = :vetId AND " +
           "a.appointmentDateTime > :now AND a.status IN ('PENDING', 'CONFIRMED') " +
           "ORDER BY a.appointmentDateTime ASC")
    List<VetAppointment> findUpcomingAppointments(@Param("vetId") Long veterinarianId, @Param("now") LocalDateTime now);
    
    // Find past appointments for veterinarian
    @Query("SELECT a FROM VetAppointment a WHERE a.veterinarianId = :vetId AND " +
           "a.appointmentDateTime < :now " +
           "ORDER BY a.appointmentDateTime DESC")
    List<VetAppointment> findPastAppointments(@Param("vetId") Long veterinarianId, @Param("now") LocalDateTime now);
    
    // Find completed appointments without medical records
    @Query("SELECT a FROM VetAppointment a WHERE a.veterinarianId = :vetId AND " +
           "a.status = 'COMPLETED' AND a.id NOT IN " +
           "(SELECT mr.appointmentId FROM VetMedicalRecord mr WHERE mr.appointmentId = a.id)")
    List<VetAppointment> findCompletedAppointmentsWithoutMedicalRecords(@Param("vetId") Long veterinarianId);
    
    // Get appointment statistics for veterinarian
    @Query("SELECT a.status, COUNT(a) FROM VetAppointment a WHERE a.veterinarianId = :vetId " +
           "GROUP BY a.status")
    List<Object[]> getAppointmentStatsByVeterinarian(@Param("vetId") Long veterinarianId);
    
    // Find appointments by pet owner and status
    List<VetAppointment> findByPetOwnerIdAndStatusOrderByAppointmentDateTimeDesc(Long petOwnerId, AppointmentStatus status);
    
    // Find appointments that need to be marked as completed (past confirmed appointments)
    @Query("SELECT a FROM VetAppointment a WHERE a.status = 'CONFIRMED' AND " +
           "a.appointmentDateTime < :cutoffTime")
    List<VetAppointment> findAppointmentsToMarkCompleted(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    // Count appointments by veterinarian and date range
    @Query("SELECT COUNT(a) FROM VetAppointment a WHERE a.veterinarianId = :vetId AND " +
           "a.appointmentDateTime BETWEEN :startDate AND :endDate")
    long countByVeterinarianAndDateRange(@Param("vetId") Long veterinarianId, 
                                        @Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
    
    // Find rescheduled appointments
    List<VetAppointment> findByOriginalAppointmentIdIsNotNullOrderByAppointmentDateTimeDesc();
    
    // Check for conflicting appointments during rescheduling
    @Query("SELECT COUNT(a) > 0 FROM VetAppointment a WHERE a.veterinarianId = :vetId AND " +
           "a.appointmentDateTime = :dateTime AND a.status IN ('PENDING', 'CONFIRMED') AND a.id != :excludeId")
    boolean existsByVeterinarianAndDateTimeExcludingId(@Param("vetId") Long veterinarianId, 
                                                       @Param("dateTime") LocalDateTime dateTime, 
                                                       @Param("excludeId") Long excludeId);
}
