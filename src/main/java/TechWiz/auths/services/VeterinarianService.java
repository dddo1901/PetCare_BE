package TechWiz.auths.services;

import TechWiz.auths.models.VeterinarianProfile;
import TechWiz.auths.models.dto.VeterinarianResponse;
import TechWiz.auths.repositories.AuthVeterinarianProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VeterinarianService {
    
    @Autowired
    private AuthVeterinarianProfileRepository veterinarianRepository;
    
    public List<VeterinarianResponse> getAllVeterinarians() {
        try {
            System.out.println("=== GETTING ALL VETERINARIANS ===");
            List<VeterinarianProfile> profiles = veterinarianRepository.findByIsProfileCompleteTrue();
            System.out.println("Found " + profiles.size() + " complete profiles");
            
            List<VeterinarianResponse> result = profiles.stream()
                    .filter(profile -> profile.getUser() != null) // Filter out profiles with null user
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            System.out.println("Returning " + result.size() + " veterinarians");
            return result;
        } catch (Exception e) {
            System.err.println("Error in getAllVeterinarians: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    public List<VeterinarianResponse> getAvailableVeterinarians() {
        List<VeterinarianProfile> profiles = veterinarianRepository.findByIsProfileCompleteTrue();
        return profiles.stream()
                .filter(profile -> profile.getUser() != null) // Filter out profiles with null user
                .filter(profile -> profile.getIsAvailableForEmergency() != null && profile.getIsAvailableForEmergency())
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<VeterinarianResponse> getVeterinariansBySpecialization(String specialization) {
        List<VeterinarianProfile> profiles = veterinarianRepository.findBySpecializationsContaining(specialization);
        return profiles.stream()
                .filter(profile -> profile.getUser() != null) // Filter out profiles with null user
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    private VeterinarianResponse convertToResponse(VeterinarianProfile profile) {
        VeterinarianResponse response = new VeterinarianResponse();
        
        // Basic info from User
        response.setId(profile.getId());
        response.setName(profile.getUser().getFullName());
        response.setEmail(profile.getUser().getEmail());
        response.setPhoneNumber(profile.getUser().getPhoneNumber());
        
        // Profile info
        response.setAddress(profile.getAddress());
        response.setLicenseNumber(profile.getLicenseNumber());
        response.setExperienceYears(profile.getExperienceYears());
        response.setSpecializations(profile.getSpecializationsList());
        response.setClinicName(profile.getClinicName());
        response.setClinicAddress(profile.getClinicAddress());
        response.setAvailableFromTime(profile.getAvailableFromTime() != null ? profile.getAvailableFromTime().toString() : null);
        response.setAvailableToTime(profile.getAvailableToTime() != null ? profile.getAvailableToTime().toString() : null);
        response.setAvailableDays(profile.getAvailableDaysList());
        response.setProfileImageUrl(profile.getProfileImageUrl());
        response.setBio(profile.getBio());
        response.setConsultationFee(profile.getConsultationFee());
        response.setIsAvailableForEmergency(profile.getIsAvailableForEmergency());
        response.setIsProfileComplete(profile.getIsProfileComplete());
        
        // Computed fields for frontend
        response.setSpecialty(getPrimarySpecialization(profile.getSpecializationsList()));
        response.setLocation(buildLocationString(profile.getClinicName(), profile.getClinicAddress()));
        response.setAvatar(profile.getProfileImageUrl());
        response.setWorkingHours(buildWorkingHoursString(profile.getAvailableDaysList(), 
            profile.getAvailableFromTime() != null ? profile.getAvailableFromTime().toString() : null,
            profile.getAvailableToTime() != null ? profile.getAvailableToTime().toString() : null));
        response.setDisplayText(buildDisplayText(response.getName(), response.getSpecialty(), response.getLocation(), response.getWorkingHours()));
        
        return response;
    }
    
    private String getPrimarySpecialization(List<String> specializations) {
        if (specializations == null || specializations.isEmpty()) {
            return "General Practice";
        }
        return specializations.get(0).trim();
    }
    
    private String buildLocationString(String clinicName, String clinicAddress) {
        if (clinicName != null && !clinicName.trim().isEmpty()) {
            if (clinicAddress != null && !clinicAddress.trim().isEmpty()) {
                return clinicName + " - " + clinicAddress;
            }
            return clinicName;
        }
        return clinicAddress != null ? clinicAddress : "Vet Clinic";
    }
    
    private String buildWorkingHoursString(List<String> availableDays, String fromTime, String toTime) {
        if (availableDays == null || availableDays.isEmpty()) {
            return "Working hours not specified";
        }
        
        if (fromTime == null || toTime == null) {
            return String.join(", ", availableDays);
        }
        
        return String.join(", ", availableDays) + " (" + fromTime + " - " + toTime + ")";
    }
    
    private String buildDisplayText(String name, String specialty, String location, String workingHours) {
        StringBuilder display = new StringBuilder();
        display.append(name);
        
        if (specialty != null && !specialty.trim().isEmpty()) {
            display.append(" - ").append(specialty);
        }
        
        if (location != null && !location.trim().isEmpty()) {
            display.append(" | ").append(location);
        }
        
        if (workingHours != null && !workingHours.trim().isEmpty()) {
            display.append(" | ").append(workingHours);
        }
        
        return display.toString();
    }
}
