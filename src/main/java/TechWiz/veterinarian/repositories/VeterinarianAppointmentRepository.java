package TechWiz.veterinarian.repositories;

import TechWiz.petOwner.models.PetOwnerAppointment;
import TechWiz.veterinarian.dto.VeterinarianAppointmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VeterinarianAppointmentRepository extends JpaRepository<PetOwnerAppointment, Long> {
    
    // Find appointments by vet ID
    List<PetOwnerAppointment> findByVetIdOrderByAppointmentDateTimeDesc(Long vetId);
    
    // Find appointments by vet ID with pagination
    Page<PetOwnerAppointment> findByVetIdOrderByAppointmentDateTimeDesc(Long vetId, Pageable pageable);
    
    // Find appointments by vet ID and status
    List<PetOwnerAppointment> findByVetIdAndStatusOrderByAppointmentDateTimeDesc(Long vetId, PetOwnerAppointment.AppointmentStatus status);
    
    // Find upcoming appointments for a vet
    @Query("SELECT a FROM PetOwnerAppointment a WHERE a.vetId = :vetId AND a.appointmentDateTime >= :now AND a.status IN ('PENDING', 'CONFIRMED') ORDER BY a.appointmentDateTime ASC")
    List<PetOwnerAppointment> findUpcomingAppointments(@Param("vetId") Long vetId, @Param("now") LocalDateTime now);
    
    // Find today's appointments for a vet
    @Query("SELECT a FROM PetOwnerAppointment a WHERE a.vetId = :vetId AND DATE(a.appointmentDateTime) = DATE(:today) ORDER BY a.appointmentDateTime ASC")
    List<PetOwnerAppointment> findTodayAppointments(@Param("vetId") Long vetId, @Param("today") LocalDateTime today);
    
    // Find past appointments for a vet
    @Query("SELECT a FROM PetOwnerAppointment a WHERE a.vetId = :vetId AND a.appointmentDateTime < :now ORDER BY a.appointmentDateTime DESC")
    List<PetOwnerAppointment> findPastAppointments(@Param("vetId") Long vetId, @Param("now") LocalDateTime now);
    
    // Count appointments by status for a vet
    Long countByVetIdAndStatus(Long vetId, PetOwnerAppointment.AppointmentStatus status);
    
    // Find appointments by pet ID (for medical records)
    List<PetOwnerAppointment> findByPetIdOrderByAppointmentDateTimeDesc(Long petId);
    
    // Find appointments by owner ID (for vet to see all appointments with an owner)
    List<PetOwnerAppointment> findByVetIdAndOwnerIdOrderByAppointmentDateTimeDesc(Long vetId, Long ownerId);
    
    // Check for appointment conflicts
    @Query("SELECT COUNT(a) > 0 FROM PetOwnerAppointment a WHERE a.vetId = :vetId AND a.appointmentDateTime = :appointmentDateTime AND a.status IN ('PENDING', 'CONFIRMED')")
    boolean existsByVetIdAndAppointmentDateTimeAndStatusIn(@Param("vetId") Long vetId, @Param("appointmentDateTime") LocalDateTime appointmentDateTime);
    
    // Find appointments in date range
    @Query("SELECT a FROM PetOwnerAppointment a WHERE a.vetId = :vetId AND a.appointmentDateTime BETWEEN :startDate AND :endDate ORDER BY a.appointmentDateTime ASC")
    List<PetOwnerAppointment> findAppointmentsInDateRange(@Param("vetId") Long vetId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    List<PetOwnerAppointment> findByVetIdAndAppointmentDateTimeBetween(Long vetId, LocalDateTime appointmentDateTimeAfter, LocalDateTime appointmentDateTimeBefore);
}
