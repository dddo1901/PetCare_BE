package TechWiz.petOwner.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import TechWiz.petOwner.dto.CreateAppointmentRequest;
import TechWiz.petOwner.models.Appointment;
import TechWiz.petOwner.models.Pet;
import TechWiz.petOwner.repositories.AppointmentRepository;
import TechWiz.petOwner.repositories.PetRepository;

@Service
@Transactional
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private PetRepository petRepository;

    // Create new appointment
    public Appointment createAppointment(CreateAppointmentRequest request, Long ownerId) {
        // Verify pet belongs to owner
        Pet pet = petRepository.findByIdAndOwnerId(request.getPetId(), ownerId)
            .orElseThrow(() -> new RuntimeException("Pet not found or doesn't belong to owner"));
        
        // Check for scheduling conflicts
        LocalDateTime appointmentStart = request.getAppointmentDateTime();
        LocalDateTime appointmentEnd = appointmentStart.plusHours(1); // Assuming 1-hour appointments
        
        List<Appointment> conflicts = appointmentRepository.findConflictingAppointments(
            request.getVeterinarianId(), appointmentStart, appointmentEnd, null);
        
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Time slot is already booked");
        }
        
        Appointment appointment = new Appointment();
        appointment.setPet(pet);
        appointment.setVeterinarianId(request.getVeterinarianId());
        appointment.setAppointmentDateTime(request.getAppointmentDateTime());
        appointment.setReason(request.getReason());
        appointment.setNotes(request.getNotes());
        appointment.setStatus("SCHEDULED");
        appointment.setPriority(request.getPriority() != null ? request.getPriority() : "NORMAL");
        
        return appointmentRepository.save(appointment);
    }

    // Get appointments by owner
    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByOwner(Long ownerId) {
        return appointmentRepository.findByOwnerIdOrderByDateTimeDesc(ownerId);
    }

    // Get appointments by owner with pagination
    @Transactional(readOnly = true)
    public Page<Appointment> getAppointmentsByOwner(Long ownerId, Pageable pageable) {
        return appointmentRepository.findByPetOwnerIdOrderByAppointmentDateTimeDesc(ownerId, pageable);
    }

    // Get appointments by pet
    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByPet(Long petId, Long ownerId) {
        // Verify pet belongs to owner
        if (!petRepository.existsByIdAndOwnerId(petId, ownerId)) {
            throw new RuntimeException("Pet not found or doesn't belong to owner");
        }
        return appointmentRepository.findByPetIdOrderByAppointmentDateTimeDesc(petId);
    }

    // Get appointment by ID (with owner verification)
    @Transactional(readOnly = true)
    public Optional<Appointment> getAppointmentById(Long appointmentId, Long ownerId) {
        return appointmentRepository.findByIdAndOwnerId(appointmentId, ownerId);
    }

    // Update appointment
    public Appointment updateAppointment(Long appointmentId, CreateAppointmentRequest request, Long ownerId) {
        Appointment appointment = appointmentRepository.findByIdAndOwnerId(appointmentId, ownerId)
            .orElseThrow(() -> new RuntimeException("Appointment not found or doesn't belong to owner"));
        
        // If changing time or veterinarian, check for conflicts
        if (!appointment.getAppointmentDateTime().equals(request.getAppointmentDateTime()) ||
            !appointment.getVeterinarianId().equals(request.getVeterinarianId())) {
            
            LocalDateTime appointmentStart = request.getAppointmentDateTime();
            LocalDateTime appointmentEnd = appointmentStart.plusHours(1);
            
            List<Appointment> conflicts = appointmentRepository.findConflictingAppointments(
                request.getVeterinarianId(), appointmentStart, appointmentEnd, appointmentId);
            
            if (!conflicts.isEmpty()) {
                throw new RuntimeException("Time slot is already booked");
            }
        }
        
        appointment.setVeterinarianId(request.getVeterinarianId());
        appointment.setAppointmentDateTime(request.getAppointmentDateTime());
        appointment.setReason(request.getReason());
        appointment.setNotes(request.getNotes());
        appointment.setPriority(request.getPriority() != null ? request.getPriority() : "NORMAL");
        
        return appointmentRepository.save(appointment);
    }

    // Cancel appointment
    public Appointment cancelAppointment(Long appointmentId, String reason, Long ownerId) {
        Appointment appointment = appointmentRepository.findByIdAndOwnerId(appointmentId, ownerId)
            .orElseThrow(() -> new RuntimeException("Appointment not found or doesn't belong to owner"));
        
        if ("COMPLETED".equals(appointment.getStatus()) || "CANCELLED".equals(appointment.getStatus())) {
            throw new RuntimeException("Cannot cancel appointment with status: " + appointment.getStatus());
        }
        
        appointment.setStatus("CANCELLED");
        appointment.setCancellationReason(reason);
        
        return appointmentRepository.save(appointment);
    }

    // Reschedule appointment
    public Appointment rescheduleAppointment(Long appointmentId, LocalDateTime newDateTime, Long ownerId) {
        Appointment appointment = appointmentRepository.findByIdAndOwnerId(appointmentId, ownerId)
            .orElseThrow(() -> new RuntimeException("Appointment not found or doesn't belong to owner"));
        
        if ("COMPLETED".equals(appointment.getStatus()) || "CANCELLED".equals(appointment.getStatus())) {
            throw new RuntimeException("Cannot reschedule appointment with status: " + appointment.getStatus());
        }
        
        // Check for conflicts at new time
        LocalDateTime appointmentEnd = newDateTime.plusHours(1);
        List<Appointment> conflicts = appointmentRepository.findConflictingAppointments(
            appointment.getVeterinarianId(), newDateTime, appointmentEnd, appointmentId);
        
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("New time slot is already booked");
        }
        
        appointment.setAppointmentDateTime(newDateTime);
        appointment.setStatus("RESCHEDULED");
        
        return appointmentRepository.save(appointment);
    }

    // Get upcoming appointments
    @Transactional(readOnly = true)
    public List<Appointment> getUpcomingAppointments(Long ownerId) {
        return appointmentRepository.findUpcomingAppointmentsByOwner(ownerId, LocalDateTime.now());
    }

    // Get past appointments (history)
    @Transactional(readOnly = true)
    public List<Appointment> getPastAppointments(Long ownerId) {
        return appointmentRepository.findPastAppointmentsByOwner(ownerId, LocalDateTime.now());
    }

    // Get appointments by status
    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByStatus(Long ownerId, String status) {
        return appointmentRepository.findByOwnerIdAndStatus(ownerId, status);
    }

    // Get appointments in date range
    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsInDateRange(Long ownerId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return appointmentRepository.findByOwnerIdAndDateTimeRange(ownerId, startDateTime, endDateTime);
    }

    // Confirm appointment (usually done by veterinarian or admin)
    public Appointment confirmAppointment(Long appointmentId, Long ownerId) {
        Appointment appointment = appointmentRepository.findByIdAndOwnerId(appointmentId, ownerId)
            .orElseThrow(() -> new RuntimeException("Appointment not found or doesn't belong to owner"));
        
        if (!"SCHEDULED".equals(appointment.getStatus()) && !"RESCHEDULED".equals(appointment.getStatus())) {
            throw new RuntimeException("Can only confirm scheduled appointments");
        }
        
        appointment.setStatus("CONFIRMED");
        return appointmentRepository.save(appointment);
    }

    // Complete appointment (add outcome)
    public Appointment completeAppointment(Long appointmentId, String outcome, Long ownerId) {
        Appointment appointment = appointmentRepository.findByIdAndOwnerId(appointmentId, ownerId)
            .orElseThrow(() -> new RuntimeException("Appointment not found or doesn't belong to owner"));
        
        appointment.setStatus("COMPLETED");
        appointment.setOutcome(outcome);
        
        return appointmentRepository.save(appointment);
    }

    // Count appointments by status
    @Transactional(readOnly = true)
    public long countAppointmentsByStatus(Long ownerId, String status) {
        return appointmentRepository.countByOwnerIdAndStatus(ownerId, status);
    }

    // Check if appointment belongs to owner
    @Transactional(readOnly = true)
    public boolean isAppointmentOwnedBy(Long appointmentId, Long ownerId) {
        return appointmentRepository.existsByIdAndOwnerId(appointmentId, ownerId);
    }

    // Get available time slots for a veterinarian on a specific date
    @Transactional(readOnly = true)
    public List<LocalDateTime> getAvailableTimeSlots(Long veterinarianId, LocalDateTime date) {
        // This would typically integrate with veterinarian availability
        // For now, return basic implementation
        List<LocalDateTime> availableSlots = new java.util.ArrayList<>();
        
        // Get existing appointments for the day
        List<Appointment> existingAppointments = appointmentRepository.findByVeterinarianIdAndDate(
            veterinarianId, date.toLocalDate());
        
        // Generate time slots (9 AM to 5 PM, 1-hour intervals)
        LocalDateTime slotTime = date.withHour(9).withMinute(0).withSecond(0);
        LocalDateTime endTime = date.withHour(17).withMinute(0).withSecond(0);
        
        while (slotTime.isBefore(endTime)) {
            final LocalDateTime currentSlot = slotTime;
            boolean isAvailable = existingAppointments.stream()
                .noneMatch(app -> app.getAppointmentDateTime().equals(currentSlot));
            
            if (isAvailable) {
                availableSlots.add(currentSlot);
            }
            
            slotTime = slotTime.plusHours(1);
        }
        
        return availableSlots;
    }
}
