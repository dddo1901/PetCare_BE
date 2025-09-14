package TechWiz.petOwner.services;

import TechWiz.petOwner.dto.PetOwnerAppointmentRequest;
import TechWiz.petOwner.dto.PetOwnerAppointmentResponse;
import TechWiz.petOwner.models.PetOwnerAppointment;
import TechWiz.petOwner.models.PetOwnerPet;
import TechWiz.petOwner.repositories.PetOwnerAppointmentRepository;
import TechWiz.petOwner.repositories.PetOwnerPetRepository;
import TechWiz.auths.services.VeterinarianService;
import TechWiz.auths.models.dto.VeterinarianResponse;
import TechWiz.auths.models.User;
import TechWiz.auths.repositories.UserRepository;
import TechWiz.veterinarian.services.EmailService;
import TechWiz.veterinarian.repositories.VeterinarianProfileRepository;
import TechWiz.auths.models.VeterinarianProfile;
import TechWiz.petOwner.services.PetOwnerHealthRecordService;
import TechWiz.petOwner.models.PetOwnerHealthRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PetOwnerAppointmentService {
    
    @Autowired
    private PetOwnerAppointmentRepository appointmentRepository;
    
    @Autowired
    private PetOwnerPetRepository petRepository;
    
    @Autowired
    private VeterinarianService veterinarianService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    @Qualifier("veterinarianEmailService")
    private EmailService emailService;
    
    @Autowired
    private VeterinarianProfileRepository veterinarianProfileRepository;
    
    @Autowired
    private PetOwnerHealthRecordService healthRecordService;
    
    public PetOwnerAppointmentResponse createAppointment(PetOwnerAppointmentRequest request, Long ownerId) {
        // Check for appointment conflicts before creating
        if (hasAppointmentConflict(request.getVetId(), request.getAppointmentDateTime())) {
            throw new RuntimeException("Veterinarian already has a confirmed appointment at this time. Please choose a different time slot.");
        }
        
        PetOwnerAppointment appointment = new PetOwnerAppointment();
        appointment.setOwnerId(ownerId);
        appointment.setPetId(request.getPetId());
        appointment.setVetId(request.getVetId());
        appointment.setType(request.getType());
        appointment.setReason(request.getReason());
        appointment.setAppointmentDateTime(request.getAppointmentDateTime());
        appointment.setOwnerNotes(request.getOwnerNotes());
        appointment.setStatus(PetOwnerAppointment.AppointmentStatus.PENDING);
        
        PetOwnerAppointment savedAppointment = appointmentRepository.save(appointment);
        
        // Send email notification to vet
        sendNewAppointmentEmailToVet(savedAppointment);
        
        return convertToResponse(savedAppointment);
    }
    
    public List<PetOwnerAppointmentResponse> getAppointmentsByOwner(Long ownerId) {
        List<PetOwnerAppointment> appointments = appointmentRepository.findByOwnerId(ownerId);
        return appointments.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    public Page<PetOwnerAppointmentResponse> getAppointmentsByOwner(Long ownerId, Pageable pageable) {
        Page<PetOwnerAppointment> appointments = appointmentRepository.findByOwnerId(ownerId, pageable);
        return appointments.map(this::convertToResponse);
    }
    
    public List<PetOwnerAppointmentResponse> getAppointmentsByStatus(Long ownerId, String status) {
        List<PetOwnerAppointment> appointments = appointmentRepository.findByOwnerIdAndStatus(ownerId, 
            PetOwnerAppointment.AppointmentStatus.valueOf(status));
        return appointments.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    public List<PetOwnerAppointmentResponse> getUpcomingAppointments(Long ownerId) {
        List<PetOwnerAppointment> appointments = appointmentRepository.findUpcomingByOwnerId(ownerId, LocalDateTime.now());
        return appointments.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    public List<PetOwnerAppointmentResponse> getPastAppointments(Long ownerId) {
        List<PetOwnerAppointment> appointments = appointmentRepository.findPastByOwnerId(ownerId, LocalDateTime.now());
        return appointments.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    public PetOwnerAppointmentResponse updateAppointment(Long appointmentId, PetOwnerAppointmentRequest request, Long ownerId) {
        PetOwnerAppointment appointment = appointmentRepository.findById(appointmentId)
                .filter(a -> a.getOwnerId().equals(ownerId))
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        appointment.setPetId(request.getPetId());
        appointment.setVetId(request.getVetId());
        appointment.setType(request.getType());
        appointment.setReason(request.getReason());
        appointment.setAppointmentDateTime(request.getAppointmentDateTime());
        appointment.setOwnerNotes(request.getOwnerNotes());
        
        PetOwnerAppointment updatedAppointment = appointmentRepository.save(appointment);
        return convertToResponse(updatedAppointment);
    }
    
    public boolean deleteAppointment(Long appointmentId, Long ownerId) {
        Optional<PetOwnerAppointment> appointment = appointmentRepository.findById(appointmentId)
                .filter(a -> a.getOwnerId().equals(ownerId));
        
        if (appointment.isPresent()) {
            appointmentRepository.delete(appointment.get());
            return true;
        }
        return false;
    }
    
    public Long getAppointmentCountByStatus(Long ownerId, String status) {
        return appointmentRepository.countByOwnerIdAndStatus(ownerId, 
            PetOwnerAppointment.AppointmentStatus.valueOf(status));
    }

    public Map<String, Object> checkAppointmentConflicts(Long vetId, LocalDateTime appointmentDateTime) {
        Map<String, Object> result = new HashMap<>();
        
        // Check for existing appointments at the same time
        // List<PetOwnerAppointment> sameTimeAppointments = appointmentRepository.findByVetIdAndAppointmentDateTime(vetId, appointmentDateTime);
        
        // Check for appointments within 1 hour before and after
        LocalDateTime oneHourBefore = appointmentDateTime.minusHours(1);
        LocalDateTime oneHourAfter = appointmentDateTime.plusHours(1);
        
        List<PetOwnerAppointment> nearbyAppointments = appointmentRepository.findByVetIdAndAppointmentDateTimeBetween(
            vetId, oneHourBefore, oneHourAfter);
        
        // Filter out cancelled appointments
        List<PetOwnerAppointment> activeAppointments = nearbyAppointments.stream()
            .filter(apt -> apt.getStatus() != PetOwnerAppointment.AppointmentStatus.CANCELLED)
            .collect(java.util.stream.Collectors.toList());
        
        boolean hasConflict = !activeAppointments.isEmpty();
        
        result.put("hasConflict", hasConflict);
        result.put("conflictingAppointments", activeAppointments.size());
        
        if (hasConflict) {
            result.put("message", "Bác sĩ đã có cuộc hẹn trong khung giờ này. Vui lòng chọn thời gian khác cách ít nhất 1 tiếng.");
            result.put("suggestedTimes", generateSuggestedTimes(vetId, appointmentDateTime));
        } else {
            result.put("message", "Thời gian này có thể đặt hẹn.");
        }
        
        return result;
    }
    
    private List<String> generateSuggestedTimes(Long vetId, LocalDateTime appointmentDateTime) {
        List<String> suggestions = new ArrayList<>();
        
        // Suggest times 1 hour before and after
        LocalDateTime suggested1 = appointmentDateTime.minusHours(1);
        LocalDateTime suggested2 = appointmentDateTime.plusHours(1);
        
        // Check if suggested times are available
        if (checkAppointmentConflicts(vetId, suggested1).get("hasConflict").equals(false)) {
            suggestions.add(suggested1.toString());
        }
        
        if (checkAppointmentConflicts(vetId, suggested2).get("hasConflict").equals(false)) {
            suggestions.add(suggested2.toString());
        }
        
        return suggestions;
    }
    
    private PetOwnerAppointmentResponse convertToResponse(PetOwnerAppointment appointment) {
        PetOwnerAppointmentResponse response = new PetOwnerAppointmentResponse();
        response.setId(appointment.getId());
        response.setOwnerId(appointment.getOwnerId());
        response.setPetId(appointment.getPetId());
        response.setVetId(appointment.getVetId());
        response.setStatus(appointment.getStatus().name());
        response.setType(appointment.getType());
        response.setReason(appointment.getReason());
        response.setAppointmentDateTime(appointment.getAppointmentDateTime());
        response.setVetNotes(appointment.getVetNotes());
        response.setOwnerNotes(appointment.getOwnerNotes());
        response.setCreatedAt(appointment.getCreatedAt());
        response.setUpdatedAt(appointment.getUpdatedAt());
        
        // Format date and time for frontend
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        response.setDate(appointment.getAppointmentDateTime().format(dateFormatter));
        response.setTime(appointment.getAppointmentDateTime().format(timeFormatter));
        
        // Set nested objects (in real implementation, fetch from respective services)
        System.out.println("=== CREATING APPOINTMENT RESPONSE ===");
        System.out.println("Pet ID: " + appointment.getPetId());
        System.out.println("Vet ID: " + appointment.getVetId());
        System.out.println("Owner ID: " + appointment.getOwnerId());
        
        response.setPet(createPetInfo(appointment.getPetId()));
        response.setVet(createVetInfo(appointment.getVetId()));
        response.setOwner(createOwnerInfo(appointment.getOwnerId()));
        
        System.out.println("Owner info created: " + (response.getOwner() != null ? "YES" : "NO"));
        if (response.getOwner() != null) {
            System.out.println("Owner name: " + response.getOwner().getName());
        }
        
        return response;
    }
    
    private PetOwnerAppointmentResponse.PetInfo createPetInfo(Long petId) {
        return petRepository.findById(petId).map(pet -> {
            PetOwnerAppointmentResponse.PetInfo petInfo = new PetOwnerAppointmentResponse.PetInfo();
            petInfo.setId(pet.getId());
            petInfo.setName(pet.getName());
            petInfo.setBreed(pet.getBreed());
            petInfo.setAge(convertAgeToString(pet.getAgeInMonths()));
            // Handle photos array - get first photo if available
            String photos = pet.getPhotos();
            if (photos != null && !photos.trim().isEmpty()) {
                try {
                    // Parse JSON array or comma-separated string
                    if (photos.startsWith("[")) {
                        // JSON array format: ["url1", "url2"]
                        String cleanPhotos = photos.substring(1, photos.length() - 1);
                        String[] photoArray = cleanPhotos.split(",");
                        if (photoArray.length > 0) {
                            String firstPhoto = photoArray[0].trim().replaceAll("^\"|\"$", "");
                            petInfo.setImage(firstPhoto);
                        }
                    } else {
                        // Single URL or comma-separated
                        String[] photoArray = photos.split(",");
                        if (photoArray.length > 0) {
                            String firstPhoto = photoArray[0].trim().replaceAll("^\"|\"$", "");
                            petInfo.setImage(firstPhoto);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing pet photos: " + e.getMessage());
                    petInfo.setImage(pet.getImageUrl());
                }
            } else {
                petInfo.setImage(pet.getImageUrl());
            }
            return petInfo;
        }).orElse(null);
    }
    
    private PetOwnerAppointmentResponse.VetInfo createVetInfo(Long vetId) {
        try {
            // Fetch real vet data from veterinarian service
            List<VeterinarianResponse> veterinarians = veterinarianService.getAllVeterinarians();
            Optional<VeterinarianResponse> vetOptional = veterinarians.stream()
                .filter(vet -> vet.getId().equals(vetId))
                .findFirst();
            
            if (vetOptional.isPresent()) {
                VeterinarianResponse vet = vetOptional.get();
                PetOwnerAppointmentResponse.VetInfo vetInfo = new PetOwnerAppointmentResponse.VetInfo();
                vetInfo.setId(vet.getId());
                vetInfo.setName(vet.getName());
                vetInfo.setSpecialty(vet.getSpecialty());
                vetInfo.setLocation(vet.getLocation());
                vetInfo.setPhone(vet.getPhoneNumber());
                // Set avatar with fallback to default
                if (vet.getAvatar() != null && !vet.getAvatar().trim().isEmpty()) {
                    vetInfo.setAvatar(vet.getAvatar());
                } else {
                    vetInfo.setAvatar("https://via.placeholder.com/150"); // Default avatar
                }
                return vetInfo;
            } else {
                // Vet not found, return basic info
                System.err.println("Veterinarian with ID " + vetId + " not found");
                return createDefaultVetInfo(vetId);
            }
        } catch (Exception e) {
            System.err.println("Error fetching vet info: " + e.getMessage());
            e.printStackTrace();
            return createDefaultVetInfo(vetId);
        }
    }
    
    private PetOwnerAppointmentResponse.VetInfo createDefaultVetInfo(Long vetId) {
        PetOwnerAppointmentResponse.VetInfo vetInfo = new PetOwnerAppointmentResponse.VetInfo();
        vetInfo.setId(vetId);
        vetInfo.setName("Dr. " + vetId);
        vetInfo.setSpecialty("General Practice");
        vetInfo.setLocation("Vet Clinic");
        vetInfo.setPhone("(555) 123-4567");
        vetInfo.setAvatar("https://via.placeholder.com/150");
        return vetInfo;
    }
    
    private PetOwnerAppointmentResponse.OwnerInfo createOwnerInfo(Long ownerId) {
        try {
            System.out.println("=== CREATING OWNER INFO ===");
            System.out.println("Looking for owner ID: " + ownerId);
            
            Optional<User> userOptional = userRepository.findById(ownerId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                System.out.println("Found user: " + user.getFullName() + " (" + user.getEmail() + ")");
                
                PetOwnerAppointmentResponse.OwnerInfo ownerInfo = new PetOwnerAppointmentResponse.OwnerInfo();
                ownerInfo.setId(user.getId());
                ownerInfo.setName(user.getFullName());
                ownerInfo.setEmail(user.getEmail());
                ownerInfo.setPhone(user.getPhoneNumber());
                // For now, use default avatar - in real implementation, add avatar field to User entity
                ownerInfo.setAvatar("https://via.placeholder.com/150");
                
                System.out.println("Owner info created successfully");
                return ownerInfo;
            } else {
                System.err.println("User with ID " + ownerId + " not found");
                return createDefaultOwnerInfo(ownerId);
            }
        } catch (Exception e) {
            System.err.println("Error fetching owner info: " + e.getMessage());
            e.printStackTrace();
            return createDefaultOwnerInfo(ownerId);
        }
    }
    
    private PetOwnerAppointmentResponse.OwnerInfo createDefaultOwnerInfo(Long ownerId) {
        PetOwnerAppointmentResponse.OwnerInfo ownerInfo = new PetOwnerAppointmentResponse.OwnerInfo();
        ownerInfo.setId(ownerId);
        ownerInfo.setName("Owner " + ownerId);
        ownerInfo.setEmail("owner" + ownerId + "@example.com");
        ownerInfo.setPhone("(555) 000-0000");
        ownerInfo.setAvatar("https://via.placeholder.com/150");
        return ownerInfo;
    }
    
    private String convertAgeToString(Integer ageInMonths) {
        if (ageInMonths == null) return "Unknown";
        if (ageInMonths < 12) {
            return ageInMonths + "m";
        } else {
            int years = ageInMonths / 12;
            int months = ageInMonths % 12;
            if (months == 0) {
                return years + "y";
            } else {
                return years + "y " + months + "m";
            }
        }
    }
    
    // Send email notification to vet when new appointment is created
    private void sendNewAppointmentEmailToVet(PetOwnerAppointment appointment) {
        try {
            // Get vet profile and user info
            Optional<VeterinarianProfile> vetProfileOpt = veterinarianProfileRepository.findById(appointment.getVetId());
            if (!vetProfileOpt.isPresent()) {
                System.err.println("Vet profile not found for vetId: " + appointment.getVetId());
                return;
            }
            
            VeterinarianProfile vetProfile = vetProfileOpt.get();
            User vetUser = vetProfile.getUser();
            
            // Get pet info
            Optional<PetOwnerPet> petOpt = petRepository.findById(appointment.getPetId());
            if (!petOpt.isPresent()) {
                System.err.println("Pet not found for petId: " + appointment.getPetId());
                return;
            }
            
            PetOwnerPet pet = petOpt.get();
            
            // Get owner info
            Optional<User> ownerOpt = userRepository.findById(appointment.getOwnerId());
            if (!ownerOpt.isPresent()) {
                System.err.println("Owner not found for ownerId: " + appointment.getOwnerId());
                return;
            }
            
            User owner = ownerOpt.get();
            
            // Get pet's health records
            List<PetOwnerHealthRecord> healthRecords = healthRecordService.getHealthRecordsByPet(appointment.getPetId(), appointment.getOwnerId());
            
            // Format appointment date and time
            String appointmentDate = appointment.getAppointmentDateTime().toLocalDate().toString();
            String appointmentTime = appointment.getAppointmentDateTime().toLocalTime().toString();
            String location = vetProfile.getClinicAddress() != null ? vetProfile.getClinicAddress() : "Veterinary Clinic";
            
            // Send email to vet with health records
            emailService.sendNewAppointmentNotificationToVetWithHealthRecords(
                vetUser.getEmail(),
                vetUser.getFullName(),
                owner.getFullName(),
                pet.getName(),
                appointment.getType(),
                appointmentDate,
                appointmentTime,
                location,
                appointment.getReason(),
                healthRecords
            );
            
            System.out.println("New appointment email with health records sent to vet: " + vetUser.getEmail());
            
        } catch (Exception e) {
            System.err.println("Error sending new appointment email to vet: " + e.getMessage());
        }
    }
    
    // Check if there's a conflict with existing appointments
    private boolean hasAppointmentConflict(Long vetId, LocalDateTime appointmentDateTime) {
        try {
            // Get all confirmed appointments for this vet at the same time
            List<PetOwnerAppointment> existingAppointments = appointmentRepository.findByVetIdAndAppointmentDateTimeAndStatus(
                vetId, 
                appointmentDateTime, 
                PetOwnerAppointment.AppointmentStatus.CONFIRMED
            );
            
            // Also check for appointments within 30 minutes (to prevent overlapping)
            LocalDateTime startTime = appointmentDateTime.minusMinutes(30);
            LocalDateTime endTime = appointmentDateTime.plusMinutes(30);
            
            List<PetOwnerAppointment> overlappingAppointments = appointmentRepository.findByVetIdAndAppointmentDateTimeBetweenAndStatus(
                vetId,
                startTime,
                endTime,
                PetOwnerAppointment.AppointmentStatus.CONFIRMED
            );
            
            // Return true if there are any conflicts
            return !existingAppointments.isEmpty() || !overlappingAppointments.isEmpty();
            
        } catch (Exception e) {
            System.err.println("Error checking appointment conflicts: " + e.getMessage());
            // If there's an error checking conflicts, allow the appointment to be created
            // This prevents blocking appointments due to technical issues
            return false;
        }
    }
}
