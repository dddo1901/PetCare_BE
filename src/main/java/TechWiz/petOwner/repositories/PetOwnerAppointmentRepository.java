package TechWiz.petOwner.repositories;

import TechWiz.petOwner.models.PetOwnerAppointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PetOwnerAppointmentRepository extends JpaRepository<PetOwnerAppointment, Long> {
    
    // Find appointments by owner ID
    List<PetOwnerAppointment> findByOwnerId(Long ownerId);
    
    // Find appointments by owner ID with pagination
    Page<PetOwnerAppointment> findByOwnerId(Long ownerId, Pageable pageable);
    
    // Find appointments by owner ID and status
    List<PetOwnerAppointment> findByOwnerIdAndStatus(Long ownerId, PetOwnerAppointment.AppointmentStatus status);
    
    // Find appointments by owner ID and status with pagination
    Page<PetOwnerAppointment> findByOwnerIdAndStatus(Long ownerId, PetOwnerAppointment.AppointmentStatus status, Pageable pageable);
    
    // Find appointments by owner ID and date range
    List<PetOwnerAppointment> findByOwnerIdAndAppointmentDateTimeBetween(Long ownerId, LocalDateTime start, LocalDateTime end);
    
    // Find upcoming appointments by owner ID
    @Query("SELECT a FROM PetOwnerAppointment a WHERE a.ownerId = :ownerId AND a.appointmentDateTime >= :now ORDER BY a.appointmentDateTime ASC")
    List<PetOwnerAppointment> findUpcomingByOwnerId(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);
    
    // Find past appointments by owner ID
    @Query("SELECT a FROM PetOwnerAppointment a WHERE a.ownerId = :ownerId AND a.appointmentDateTime < :now ORDER BY a.appointmentDateTime DESC")
    List<PetOwnerAppointment> findPastByOwnerId(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);
    
    // Find appointments by pet ID
    List<PetOwnerAppointment> findByPetId(Long petId);
    
    // Find appointments by vet ID
    List<PetOwnerAppointment> findByVetId(Long vetId);
    
    // Count appointments by owner ID and status
    @Query("SELECT COUNT(a) FROM PetOwnerAppointment a WHERE a.ownerId = :ownerId AND a.status = :status")
    Long countByOwnerIdAndStatus(@Param("ownerId") Long ownerId, @Param("status") PetOwnerAppointment.AppointmentStatus status);
    
    // Find appointments by vet ID and exact appointment time
    List<PetOwnerAppointment> findByVetIdAndAppointmentDateTime(Long vetId, LocalDateTime appointmentDateTime);
    
    // Find appointments by vet ID and appointment time range
    List<PetOwnerAppointment> findByVetIdAndAppointmentDateTimeBetween(Long vetId, LocalDateTime start, LocalDateTime end);
    
    // Count appointments by owner ID and date range excluding status
    @Query("SELECT COUNT(a) FROM PetOwnerAppointment a WHERE a.ownerId = :ownerId AND a.appointmentDateTime BETWEEN :start AND :end AND a.status != :status")
    Long countByOwnerIdAndAppointmentDateTimeBetweenAndStatusNot(@Param("ownerId") Long ownerId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("status") PetOwnerAppointment.AppointmentStatus status);
    
    // Find top 5 appointments by owner ID ordered by appointment date
    @Query("SELECT a FROM PetOwnerAppointment a WHERE a.ownerId = :ownerId ORDER BY a.appointmentDateTime DESC")
    List<PetOwnerAppointment> findTop5ByOwnerIdOrderByAppointmentDateTimeDesc(@Param("ownerId") Long ownerId);
}
