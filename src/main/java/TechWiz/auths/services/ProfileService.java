package TechWiz.auths.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import TechWiz.auths.models.*;
import TechWiz.auths.models.dto.UserProfileResponse;
import TechWiz.auths.repositories.*;

import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetOwnerProfileRepository petOwnerProfileRepository;

    @Autowired
    private AuthVeterinarianProfileRepository veterinarianProfileRepository;

    @Autowired
    private ShelterProfileRepository shelterProfileRepository;

    public UserProfileResponse getUserProfileWithRoleData(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        
        if (userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();
        UserProfileResponse response = new UserProfileResponse();
        
        // Set basic user info
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setRole(user.getRole());
        response.setIsActive(user.getIsActive());
        response.setIsEmailVerified(user.getIsEmailVerified());
        response.setCreatedAt(user.getCreatedAt());
        
        // Set role-specific profile data
        Object profileData = getRoleSpecificProfileData(user);
        response.setProfile(profileData);
        
        return response;
    }

    private Object getRoleSpecificProfileData(User user) {
        switch (user.getRole()) {
            case PET_OWNER:
                return getPetOwnerProfileData(user);
            case VETERINARIAN:
                return getVeterinarianProfileData(user);
            case SHELTER:
                return getShelterProfileData(user);
            case ADMIN:
                return null; // Admin doesn't have profile
            default:
                return null;
        }
    }

    private UserProfileResponse.PetOwnerProfileData getPetOwnerProfileData(User user) {
        Optional<PetOwnerProfile> profileOptional = petOwnerProfileRepository.findByUser(user);
        
        if (profileOptional.isEmpty()) {
            return null;
        }

        PetOwnerProfile profile = profileOptional.get();
        return new UserProfileResponse.PetOwnerProfileData(
            profile.getId(),
            profile.getAddress(),
            profile.getEmergencyContactName(),
            profile.getEmergencyContactPhone(),
            profile.getProfileImageUrl(),
            profile.getBio(),
            profile.getAllowAccountSharing(),
            profile.getCreatedAt(),
            profile.getUpdatedAt()
        );
    }

    private UserProfileResponse.VeterinarianProfileData getVeterinarianProfileData(User user) {
        Optional<VeterinarianProfile> profileOptional = veterinarianProfileRepository.findByUser(user);
        
        if (profileOptional.isEmpty()) {
            return null;
        }

        VeterinarianProfile profile = profileOptional.get();
        return new UserProfileResponse.VeterinarianProfileData(
            profile.getId(),
            profile.getAddress(),
            profile.getLicenseNumber(),
            profile.getExperienceYears(),
            profile.getSpecializations(),
            profile.getClinicName(),
            profile.getClinicAddress(),
            profile.getAvailableFromTime(),
            profile.getAvailableToTime(),
            profile.getAvailableDays(),
            profile.getProfileImageUrl(),
            profile.getBio(),
            profile.getConsultationFee(),
            profile.getIsAvailableForEmergency(),
            profile.getIsProfileComplete(),
            profile.getCreatedAt(),
            profile.getUpdatedAt()
        );
    }

    private UserProfileResponse.ShelterProfileData getShelterProfileData(User user) {
        Optional<ShelterProfile> profileOptional = shelterProfileRepository.findByUser(user);
        
        if (profileOptional.isEmpty()) {
            return null;
        }

        ShelterProfile profile = profileOptional.get();
        return new UserProfileResponse.ShelterProfileData(
            profile.getId(),
            profile.getAddress(),
            profile.getShelterName(),
            profile.getContactPersonName(),
            profile.getRegistrationNumber(),
            profile.getWebsite(),
            profile.getDescription(),
            profile.getCapacity(),
            profile.getCurrentOccupancy(),
            profile.getProfileImageUrl(),
            profile.getImages(),
            profile.getIsVerified(),
            profile.getAcceptsDonations(),
            profile.getOperatingHours(),
            profile.getCreatedAt(),
            profile.getUpdatedAt()
        );
    }
}
