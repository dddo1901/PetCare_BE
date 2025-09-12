package TechWiz.petOwner.repositories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import TechWiz.petOwner.models.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    // Find appointments by pet owner
    @Query("SELECT a FROM Appointment a JOIN a.pet p WHERE p.ownerId = :ownerId " +
           "ORDER BY a.appointmentDateTime DESC")
    List<Appointment> findByOwnerIdOrderByDateTimeDesc(@Param("ownerId") Long ownerId);
    
    @Query("SELECT a FROM Appointment a JOIN a.pet p WHERE p.ownerId = :ownerId " +
           "ORDER BY a.appointmentDateTime DESC")
    Page<Appointment> findByPetOwnerIdOrderByAppointmentDateTimeDesc(@Param("ownerId") Long ownerId, Pageable pageable);
    
    // Find appointments by pet
    List<Appointment> findByPetIdOrderByAppointmentDateTimeDesc(Long petId);
    
    // Find appointments by veterinarian
    List<Appointment> findByVeterinarianIdOrderByAppointmentDateTimeDesc(Long veterinarianId);
    
    // Find appointments by status
    @Query("SELECT a FROM Appointment a JOIN a.pet p WHERE p.ownerId = :ownerId AND a.status = :status " +
           "ORDER BY a.appointmentDateTime DESC")
    List<Appointment> findByOwnerIdAndStatus(@Param("ownerId") Long ownerId, @Param("status") String status);
    
    List<Appointment> findByPetIdAndStatus(Long petId, String status);
    List<Appointment> findByVeterinarianIdAndStatus(Long veterinarianId, String status);
    
    // Find upcoming appointments
    @Query("SELECT a FROM Appointment a JOIN a.pet p WHERE p.ownerId = :ownerId AND " +
           "a.appointmentDateTime >= :now AND a.status IN ('SCHEDULED', 'CONFIRMED') " +
           "ORDER BY a.appointmentDateTime ASC")
    List<Appointment> findUpcomingAppointmentsByOwner(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);
    
    @Query("SELECT a FROM Appointment a WHERE a.veterinarianId = :veterinarianId AND " +
           "a.appointmentDateTime >= :now AND a.status IN ('SCHEDULED', 'CONFIRMED') " +
           "ORDER BY a.appointmentDateTime ASC")
    List<Appointment> findUpcomingAppointmentsByVeterinarian(@Param("veterinarianId") Long veterinarianId, @Param("now") LocalDateTime now);
    
    // Find appointments for specific date
    @Query("SELECT a FROM Appointment a WHERE a.veterinarianId = :veterinarianId AND " +
           "DATE(a.appointmentDateTime) = :date ORDER BY a.appointmentDateTime ASC")
    List<Appointment> findByVeterinarianIdAndDate(@Param("veterinarianId") Long veterinarianId, @Param("date") LocalDate date);
    
    // Find appointments in date range
    @Query("SELECT a FROM Appointment a JOIN a.pet p WHERE p.ownerId = :ownerId AND " +
           "a.appointmentDateTime BETWEEN :startDateTime AND :endDateTime " +
           "ORDER BY a.appointmentDateTime ASC")
    List<Appointment> findByOwnerIdAndDateTimeRange(@Param("ownerId") Long ownerId,
                                                   @Param("startDateTime") LocalDateTime startDateTime,
                                                   @Param("endDateTime") LocalDateTime endDateTime);
    
    // Check for scheduling conflicts
    @Query("SELECT a FROM Appointment a WHERE a.veterinarianId = :veterinarianId AND " +
           "a.appointmentDateTime BETWEEN :startTime AND :endTime AND " +
           "a.status IN ('SCHEDULED', 'CONFIRMED') AND " +
           "(:appointmentId IS NULL OR a.id != :appointmentId)")
    List<Appointment> findConflictingAppointments(@Param("veterinarianId") Long veterinarianId,
                                                 @Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime,
                                                 @Param("appointmentId") Long appointmentId);
    
    // Find today's appointments
    @Query("SELECT a FROM Appointment a WHERE DATE(a.appointmentDateTime) = CURRENT_DATE AND " +
           "a.status IN ('SCHEDULED', 'CONFIRMED') ORDER BY a.appointmentDateTime ASC")
    List<Appointment> findTodaysAppointments();
    
    // Count appointments by status for owner
    @Query("SELECT COUNT(a) FROM Appointment a JOIN a.pet p WHERE p.ownerId = :ownerId AND a.status = :status")
    long countByOwnerIdAndStatus(@Param("ownerId") Long ownerId, @Param("status") String status);
    
    // Check if appointment belongs to owner
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Appointment a " +
           "JOIN a.pet p WHERE a.id = :appointmentId AND p.ownerId = :ownerId")
    boolean existsByIdAndOwnerId(@Param("appointmentId") Long appointmentId, @Param("ownerId") Long ownerId);
    
    // Find appointment by ID ensuring it belongs to owner
    @Query("SELECT a FROM Appointment a JOIN a.pet p WHERE a.id = :appointmentId AND p.ownerId = :ownerId")
    Optional<Appointment> findByIdAndOwnerId(@Param("appointmentId") Long appointmentId, @Param("ownerId") Long ownerId);
    
    // Find past appointments (for history)
    @Query("SELECT a FROM Appointment a JOIN a.pet p WHERE p.ownerId = :ownerId AND " +
           "a.appointmentDateTime < :now AND a.status = 'COMPLETED' " +
           "ORDER BY a.appointmentDateTime DESC")
    List<Appointment> findPastAppointmentsByOwner(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);
}
