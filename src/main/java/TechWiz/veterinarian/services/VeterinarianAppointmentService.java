package TechWiz.veterinarian.services;

import TechWiz.veterinarian.dto.VeterinarianAppointmentResponse;
import TechWiz.petOwner.models.PetOwnerAppointment;
import TechWiz.petOwner.models.PetOwnerPet;
import TechWiz.auths.models.User;
import TechWiz.auths.models.VeterinarianProfile;
import TechWiz.veterinarian.repositories.VeterinarianAppointmentRepository;
import TechWiz.petOwner.repositories.PetOwnerPetRepository;
import TechWiz.auths.repositories.UserRepository;
import TechWiz.veterinarian.repositories.VeterinarianProfileRepository;
import TechWiz.veterinarian.services.EmailService;
import TechWiz.veterinarian.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VeterinarianAppointmentService {
    
    @Autowired
    private VeterinarianAppointmentRepository appointmentRepository;
    
    @Autowired
    private PetOwnerPetRepository petRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private VeterinarianProfileRepository veterinarianProfileRepository;
    
    @Autowired
    @Qualifier("veterinarianEmailService")
    private EmailService emailService;
    
    @Autowired
    private NotificationService notificationService;
    
    // Get all appointments for a vet
    public List<VeterinarianAppointmentResponse> getAppointmentsByVet(Long vetId) {
        List<PetOwnerAppointment> appointments = appointmentRepository.findByVetIdOrderByAppointmentDateTimeDesc(vetId);
        return appointments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // Get appointments with pagination
    public Page<VeterinarianAppointmentResponse> getAppointmentsByVet(Long vetId, Pageable pageable) {
        Page<PetOwnerAppointment> appointments = appointmentRepository.findByVetIdOrderByAppointmentDateTimeDesc(vetId, pageable);
        return appointments.map(this::convertToResponse);
    }
    
    // Get appointments by status
    public List<VeterinarianAppointmentResponse> getAppointmentsByStatus(Long vetId, String status) {
        PetOwnerAppointment.AppointmentStatus appointmentStatus = PetOwnerAppointment.AppointmentStatus.valueOf(status);
        List<PetOwnerAppointment> appointments = appointmentRepository.findByVetIdAndStatusOrderByAppointmentDateTimeDesc(vetId, appointmentStatus);
        return appointments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // Get upcoming appointments
    public List<VeterinarianAppointmentResponse> getUpcomingAppointments(Long vetId) {
        List<PetOwnerAppointment> appointments = appointmentRepository.findUpcomingAppointments(vetId, LocalDateTime.now());
        return appointments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // Get today's appointments
    public List<VeterinarianAppointmentResponse> getTodayAppointments(Long vetId) {
        List<PetOwnerAppointment> appointments = appointmentRepository.findTodayAppointments(vetId, LocalDateTime.now());
        return appointments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // Get past appointments
    public List<VeterinarianAppointmentResponse> getPastAppointments(Long vetId) {
        List<PetOwnerAppointment> appointments = appointmentRepository.findPastAppointments(vetId, LocalDateTime.now());
        return appointments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // Update appointment status
    public VeterinarianAppointmentResponse updateAppointmentStatus(Long appointmentId, String status, Long vetId) {
        PetOwnerAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        if (!appointment.getVetId().equals(vetId)) {
            throw new RuntimeException("Unauthorized to update this appointment");
        }
        
        appointment.setStatus(PetOwnerAppointment.AppointmentStatus.valueOf(status));
        appointment.setUpdatedAt(LocalDateTime.now());
        
        PetOwnerAppointment savedAppointment = appointmentRepository.save(appointment);
        return convertToResponse(savedAppointment);
    }
    
    // Add vet notes to appointment
    public VeterinarianAppointmentResponse addVetNotes(Long appointmentId, String vetNotes, Long vetId) {
        PetOwnerAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        if (!appointment.getVetId().equals(vetId)) {
            throw new RuntimeException("Unauthorized to update this appointment");
        }
        
        appointment.setVetNotes(vetNotes);
        appointment.setUpdatedAt(LocalDateTime.now());
        
        PetOwnerAppointment savedAppointment = appointmentRepository.save(appointment);
        return convertToResponse(savedAppointment);
    }
    
    // Reschedule appointment
    public VeterinarianAppointmentResponse rescheduleAppointment(Long appointmentId, LocalDateTime newDateTime, Long vetId) {
        PetOwnerAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        if (!appointment.getVetId().equals(vetId)) {
            throw new RuntimeException("Unauthorized to update this appointment");
        }
        
        // Check for conflicts
        if (appointmentRepository.existsByVetIdAndAppointmentDateTimeAndStatusIn(vetId, newDateTime)) {
            throw new RuntimeException("Appointment time conflict");
        }
        
        appointment.setAppointmentDateTime(newDateTime);
        appointment.setStatus(PetOwnerAppointment.AppointmentStatus.RESCHEDULED);
        appointment.setUpdatedAt(LocalDateTime.now());
        
        PetOwnerAppointment savedAppointment = appointmentRepository.save(appointment);
        return convertToResponse(savedAppointment);
    }
    
    // Get appointment statistics
    public Map<String, Long> getAppointmentStatistics(Long vetId) {
        return Map.of(
            "total", appointmentRepository.countByVetIdAndStatus(vetId, PetOwnerAppointment.AppointmentStatus.PENDING) +
                    appointmentRepository.countByVetIdAndStatus(vetId, PetOwnerAppointment.AppointmentStatus.CONFIRMED) +
                    appointmentRepository.countByVetIdAndStatus(vetId, PetOwnerAppointment.AppointmentStatus.COMPLETED),
            "pending", appointmentRepository.countByVetIdAndStatus(vetId, PetOwnerAppointment.AppointmentStatus.PENDING),
            "confirmed", appointmentRepository.countByVetIdAndStatus(vetId, PetOwnerAppointment.AppointmentStatus.CONFIRMED),
            "completed", appointmentRepository.countByVetIdAndStatus(vetId, PetOwnerAppointment.AppointmentStatus.COMPLETED),
            "cancelled", appointmentRepository.countByVetIdAndStatus(vetId, PetOwnerAppointment.AppointmentStatus.CANCELLED)
        );
    }
    
    // Check appointment conflicts
    public Map<String, Object> checkAppointmentConflicts(Long vetId, LocalDateTime appointmentDateTime) {
        boolean hasConflict = appointmentRepository.existsByVetIdAndAppointmentDateTimeAndStatusIn(vetId, appointmentDateTime);
        
        return Map.of(
            "hasConflict", hasConflict,
            "message", hasConflict ? "Time slot is not available" : "Time slot is available"
        );
    }
    
    // Convert entity to response DTO
    private VeterinarianAppointmentResponse convertToResponse(PetOwnerAppointment appointment) {
        VeterinarianAppointmentResponse response = new VeterinarianAppointmentResponse();
        response.setId(appointment.getId());
        response.setVetId(appointment.getVetId());
        response.setOwnerId(appointment.getOwnerId());
        response.setPetId(appointment.getPetId());
        response.setStatus(appointment.getStatus().name());
        response.setType(appointment.getType());
        response.setReason(appointment.getReason());
        response.setAppointmentDateTime(appointment.getAppointmentDateTime());
        response.setVetNotes(appointment.getVetNotes());
        response.setOwnerNotes(appointment.getOwnerNotes());
        // Get location and consultation fee from vet profile
        try {
            Optional<VeterinarianProfile> vetProfile = veterinarianProfileRepository.findById(appointment.getVetId());
            if (vetProfile.isPresent()) {
                VeterinarianProfile profile = vetProfile.get();
                response.setLocation(profile.getClinicAddress() != null ? profile.getClinicAddress() : "Clinic Location");
                response.setConsultationFee(profile.getConsultationFee() != null ? profile.getConsultationFee() : 0.0);
            } else {
                response.setLocation("Clinic Location");
                response.setConsultationFee(0.0);
            }
        } catch (Exception e) {
            response.setLocation("Clinic Location");
            response.setConsultationFee(0.0);
        }
        response.setCreatedAt(appointment.getCreatedAt());
        response.setUpdatedAt(appointment.getUpdatedAt());
        
        // Populate pet, owner, and vet information
        response.setPet(createPetInfo(appointment.getPetId()));
        response.setOwner(createOwnerInfo(appointment.getOwnerId()));
        response.setVet(createVetInfo(appointment.getVetId()));
        
        return response;
    }
    
    // Helper methods to create info objects
    private VeterinarianAppointmentResponse.PetInfo createPetInfo(Long petId) {
        VeterinarianAppointmentResponse.PetInfo petInfo = new VeterinarianAppointmentResponse.PetInfo();
        petInfo.setId(petId);
        
        try {
            Optional<PetOwnerPet> pet = petRepository.findById(petId);
            if (pet.isPresent()) {
                PetOwnerPet petEntity = pet.get();
                petInfo.setName(petEntity.getName());
                petInfo.setBreed(petEntity.getBreed());
                petInfo.setAge(petEntity.getAgeInMonths() + " months");
                petInfo.setGender(petEntity.getGender() != null ? petEntity.getGender().name() : "Unknown");
                // Use actual image URL from database
                String imageUrl = petEntity.getImageUrl();
                String photos = petEntity.getPhotos();
                
                // Try to get image from photos field if imageUrl is null
                if (imageUrl == null || imageUrl.trim().isEmpty()) {
                    if (photos != null && !photos.trim().isEmpty()) {
                        try {
                            // Parse JSON array and get first photo
                            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                            String[] photoArray = mapper.readValue(photos, String[].class);
                            if (photoArray.length > 0) {
                                imageUrl = photoArray[0];
                            }
                        } catch (Exception e) {
                            // Handle JSON parsing error silently
                        }
                    }
                }
                
                // Convert relative path to full URL if needed
                if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                    if (imageUrl.startsWith("/api/")) {
                        // Convert relative path to full URL
                        imageUrl = "http://localhost:8080" + imageUrl;
                    }
                }
                
                petInfo.setImageUrl(imageUrl); // Return actual image URL, even if null
                petInfo.setSpecies(petEntity.getType() != null ? petEntity.getType().name() : "Unknown");
            } else {
                // Fallback data
                petInfo.setName("Pet " + petId);
                petInfo.setBreed("Unknown");
                petInfo.setAge("Unknown");
                petInfo.setGender("Unknown");
                petInfo.setImageUrl(null); // No fallback, return null if pet not found
                petInfo.setSpecies("Unknown");
            }
        } catch (Exception e) {
            // Fallback data in case of error
            petInfo.setName("Pet " + petId);
            petInfo.setBreed("Unknown");
            petInfo.setAge("Unknown");
            petInfo.setGender("Unknown");
            petInfo.setImageUrl("https://images.unsplash.com/photo-1552053831-71594a27632d?w=150&h=150&fit=crop&crop=face");
            petInfo.setSpecies("Unknown");
        }
        
        return petInfo;
    }
    
    private VeterinarianAppointmentResponse.OwnerInfo createOwnerInfo(Long ownerId) {
        VeterinarianAppointmentResponse.OwnerInfo ownerInfo = new VeterinarianAppointmentResponse.OwnerInfo();
        ownerInfo.setId(ownerId);
        
        try {
            Optional<User> user = userRepository.findById(ownerId);
            if (user.isPresent()) {
                User userEntity = user.get();
                ownerInfo.setName(userEntity.getFullName());
                ownerInfo.setEmail(userEntity.getEmail());
                ownerInfo.setPhoneNumber(userEntity.getPhoneNumber());
                ownerInfo.setAddress("N/A"); // Address not available in User entity
                // User entity doesn't have imageUrl field, so return null
                ownerInfo.setImageUrl(null);
            } else {
                // Fallback data
                ownerInfo.setName("Owner " + ownerId);
                ownerInfo.setEmail("owner" + ownerId + "@example.com");
                ownerInfo.setPhoneNumber("N/A");
                ownerInfo.setAddress("N/A");
                ownerInfo.setImageUrl(null);
            }
        } catch (Exception e) {
            // Fallback data in case of error
            ownerInfo.setName("Owner " + ownerId);
            ownerInfo.setEmail("owner" + ownerId + "@example.com");
            ownerInfo.setPhoneNumber("N/A");
            ownerInfo.setAddress("N/A");
            ownerInfo.setImageUrl(null);
        }
        
        return ownerInfo;
    }
    
    private VeterinarianAppointmentResponse.VetInfo createVetInfo(Long vetId) {
        VeterinarianAppointmentResponse.VetInfo vetInfo = new VeterinarianAppointmentResponse.VetInfo();
        vetInfo.setId(vetId);
        
        try {
            Optional<VeterinarianProfile> vetProfile = veterinarianProfileRepository.findById(vetId);
            if (vetProfile.isPresent()) {
                VeterinarianProfile profile = vetProfile.get();
                vetInfo.setName(profile.getUser().getFullName());
                vetInfo.setClinicName(profile.getClinicName());
                vetInfo.setClinicAddress(profile.getClinicAddress());
                vetInfo.setPhoneNumber(profile.getUser().getPhoneNumber());
                vetInfo.setSpecializations(profile.getSpecializations());
                vetInfo.setImageUrl(profile.getProfileImageUrl());
            } else {
                // Fallback data
                vetInfo.setName("Dr. Vet " + vetId);
                vetInfo.setClinicName("Pet Care Clinic");
                vetInfo.setClinicAddress("Clinic Address");
                vetInfo.setPhoneNumber("N/A");
                vetInfo.setSpecializations("General Practice");
                vetInfo.setImageUrl(null);
            }
        } catch (Exception e) {
            // Fallback data in case of error
            vetInfo.setName("Dr. Vet " + vetId);
            vetInfo.setClinicName("Pet Care Clinic");
            vetInfo.setClinicAddress("Clinic Address");
            vetInfo.setPhoneNumber("N/A");
            vetInfo.setSpecializations("General Practice");
            vetInfo.setImageUrl(null);
        }
        
        return vetInfo;
    }
    
    // Approve appointment and send notification/email
    public boolean approveAppointment(Long appointmentId) {
        try {
            Optional<PetOwnerAppointment> appointmentOpt = appointmentRepository.findById(appointmentId);
            if (appointmentOpt.isPresent()) {
                PetOwnerAppointment appointment = appointmentOpt.get();
                appointment.setStatus(PetOwnerAppointment.AppointmentStatus.CONFIRMED);
                appointmentRepository.save(appointment);
                
                // Send notification and email
                sendAppointmentUpdateNotification(appointment, "APPROVED");
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error approving appointment: " + e.getMessage());
            return false;
        }
    }
    
    // Reject appointment and send notification/email
    public boolean rejectAppointment(Long appointmentId) {
        try {
            Optional<PetOwnerAppointment> appointmentOpt = appointmentRepository.findById(appointmentId);
            if (appointmentOpt.isPresent()) {
                PetOwnerAppointment appointment = appointmentOpt.get();
                appointment.setStatus(PetOwnerAppointment.AppointmentStatus.CANCELLED);
                appointmentRepository.save(appointment);
                
                // Send notification and email
                sendAppointmentUpdateNotification(appointment, "REJECTED");
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error rejecting appointment: " + e.getMessage());
            return false;
        }
    }
    
    // Reschedule appointment and send notification/email
    public boolean rescheduleAppointment(Long appointmentId, String newDateTime) {
        try {
            Optional<PetOwnerAppointment> appointmentOpt = appointmentRepository.findById(appointmentId);
            if (appointmentOpt.isPresent()) {
                PetOwnerAppointment appointment = appointmentOpt.get();
                appointment.setAppointmentDateTime(LocalDateTime.parse(newDateTime));
                appointmentRepository.save(appointment);
                
                // Send notification and email
                sendAppointmentUpdateNotification(appointment, "RESCHEDULED");
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error rescheduling appointment: " + e.getMessage());
            return false;
        }
    }
    
    // Helper method to send notification and email
    private void sendAppointmentUpdateNotification(PetOwnerAppointment appointment, String action) {
        try {
            // Get pet and owner info
            Optional<PetOwnerPet> petOpt = petRepository.findById(appointment.getPetId());
            Optional<User> ownerOpt = userRepository.findById(appointment.getOwnerId());
            
            if (petOpt.isPresent() && ownerOpt.isPresent()) {
                PetOwnerPet pet = petOpt.get();
                User owner = ownerOpt.get();
                
                // Create notification
                String title = "Appointment " + action + " - " + pet.getName();
                String message = "Your appointment for " + pet.getName() + " has been " + action.toLowerCase() + " by the veterinarian.";
                notificationService.createAppointmentNotification(
                    appointment.getOwnerId(), 
                    title, 
                    message, 
                    "APPOINTMENT_UPDATE"
                );
                
                // Send email
                String appointmentDate = appointment.getAppointmentDateTime().toLocalDate().toString();
                String appointmentTime = appointment.getAppointmentDateTime().toLocalTime().toString();
                String location = "Veterinary Clinic"; // Default location
                
                emailService.sendAppointmentNotification(
                    owner.getEmail(),
                    owner.getFullName(),
                    pet.getName(),
                    appointment.getType(),
                    appointmentDate,
                    appointmentTime,
                    location,
                    action
                );
            }
        } catch (Exception e) {
            System.err.println("Error sending notification/email: " + e.getMessage());
        }
    }

    public List<VeterinarianAppointmentResponse> getAppointmentsByVetAndDate(Long vetId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<PetOwnerAppointment> appointments =
                appointmentRepository.findByVetIdAndAppointmentDateTimeBetween(vetId, startOfDay, endOfDay);

        return appointments.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private VeterinarianAppointmentResponse mapToResponse(PetOwnerAppointment entity) {
        VeterinarianAppointmentResponse dto = new VeterinarianAppointmentResponse();
        dto.setId(entity.getId());
        dto.setPetId(entity.getPetId());
        dto.setVetId(entity.getVetId());
        dto.setOwnerId(entity.getOwnerId());
        dto.setStatus(entity.getStatus().name());
        dto.setType(entity.getType());
        dto.setReason(entity.getReason());
        dto.setAppointmentDateTime(entity.getAppointmentDateTime());
        dto.setVetNotes(entity.getVetNotes());
        dto.setOwnerNotes(entity.getOwnerNotes());
        return dto;
    }

}
