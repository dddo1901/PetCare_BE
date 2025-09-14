package TechWiz.auths.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import TechWiz.auths.models.PetOwnerProfile;
import TechWiz.auths.models.ShelterProfile;
import TechWiz.auths.models.User;
import TechWiz.auths.models.VeterinarianProfile;
import TechWiz.auths.models.dto.UserProfileResponse;
import TechWiz.auths.repositories.AuthVeterinarianProfileRepository;
import TechWiz.auths.repositories.PetOwnerProfileRepository;
import TechWiz.auths.repositories.ShelterProfileRepository;
import TechWiz.auths.repositories.UserRepository;

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
        response.setProfileImageUrl(user.getProfileImageUrl()); // Add avatar from User model
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
            user.getProfileImageUrl(),
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
            profile.getSpecializationsList(),
            profile.getClinicName(),
            profile.getClinicAddress(),
            profile.getAvailableFromTime(),
            profile.getAvailableToTime(),
            profile.getAvailableDaysList(),
            user.getProfileImageUrl(),
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
            user.getProfileImageUrl(),
            profile.getImages(),
            profile.getIsVerified(),
            profile.getAcceptsDonations(),
            profile.getOperatingHours(),
            profile.getCreatedAt(),
            profile.getUpdatedAt()
        );
    }

    // Update profile methods
    public UserProfileResponse.PetOwnerProfileData updatePetOwnerProfile(String email, TechWiz.auths.models.dto.UpdatePetOwnerProfileRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();
        Optional<PetOwnerProfile> profileOptional = petOwnerProfileRepository.findByUser(user);
        
        PetOwnerProfile profile;
        if (profileOptional.isEmpty()) {
            // Create new profile if doesn't exist
            profile = new PetOwnerProfile();
            profile.setUser(user);
            profile.setAddress("");
            profile.setEmergencyContactName("");
            profile.setEmergencyContactPhone("");
            profile.setBio("");
            profile.setAllowAccountSharing(false);
        } else {
            profile = profileOptional.get();
        }
        
        // Update fields if provided
        if (request.getAddress() != null) profile.setAddress(request.getAddress());
        if (request.getEmergencyContactName() != null) profile.setEmergencyContactName(request.getEmergencyContactName());
        if (request.getEmergencyContactPhone() != null) profile.setEmergencyContactPhone(request.getEmergencyContactPhone());
        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl());
            userRepository.save(user);
        }
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getAllowAccountSharing() != null) profile.setAllowAccountSharing(request.getAllowAccountSharing());

        petOwnerProfileRepository.save(profile);

        return getPetOwnerProfileData(user);
    }

    public UserProfileResponse.VeterinarianProfileData updateVeterinarianProfile(String email, TechWiz.auths.models.dto.UpdateVeterinarianProfileRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();
        Optional<VeterinarianProfile> profileOptional = veterinarianProfileRepository.findByUser(user);
        
        VeterinarianProfile profile;
        if (profileOptional.isEmpty()) {
            // Create new profile if doesn't exist
            profile = new VeterinarianProfile();
            profile.setUser(user);
            profile.setAddress("");
            profile.setLicenseNumber("");
            profile.setSpecializations("");
            profile.setExperienceYears(0);
            profile.setClinicName("");
            profile.setClinicAddress("");
            profile.setIsAvailableForEmergency(false);
            profile.setIsProfileComplete(false);
        } else {
            profile = profileOptional.get();
        }
        
        // Update fields if provided
        if (request.getAddress() != null) profile.setAddress(request.getAddress());
        if (request.getLicenseNumber() != null) profile.setLicenseNumber(request.getLicenseNumber());
        if (request.getExperienceYears() != null) profile.setExperienceYears(request.getExperienceYears());
        if (request.getSpecializations() != null) profile.setSpecializationsList(request.getSpecializations());
        if (request.getClinicName() != null) profile.setClinicName(request.getClinicName());
        if (request.getClinicAddress() != null) profile.setClinicAddress(request.getClinicAddress());
        if (request.getAvailableFromTime() != null) profile.setAvailableFromTime(request.getAvailableFromTime());
        if (request.getAvailableToTime() != null) profile.setAvailableToTime(request.getAvailableToTime());
        if (request.getAvailableDays() != null) profile.setAvailableDaysList(request.getAvailableDays());
        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl());
            userRepository.save(user);
        }
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getConsultationFee() != null) profile.setConsultationFee(request.getConsultationFee());
        if (request.getIsAvailableForEmergency() != null) profile.setIsAvailableForEmergency(request.getIsAvailableForEmergency());

        // Check if profile is complete
        boolean isComplete = profile.getLicenseNumber() != null && !profile.getLicenseNumber().trim().isEmpty() &&
                           profile.getSpecializations() != null && !profile.getSpecializations().trim().isEmpty() &&
                           profile.getAvailableFromTime() != null &&
                           profile.getAvailableToTime() != null;
        profile.setIsProfileComplete(isComplete);

        veterinarianProfileRepository.save(profile);

        return getVeterinarianProfileData(user);
    }

    public UserProfileResponse.ShelterProfileData updateShelterProfile(String email, TechWiz.auths.models.dto.UpdateShelterProfileRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();
        Optional<ShelterProfile> profileOptional = shelterProfileRepository.findByUser(user);
        
        ShelterProfile profile;
        if (profileOptional.isEmpty()) {
            // Create new profile if doesn't exist
            profile = new ShelterProfile();
            profile.setUser(user);
            profile.setShelterName("");
            profile.setAddress("");
            profile.setContactPersonName("");
            profile.setCapacity(0);
            profile.setCurrentOccupancy(0);
            profile.setAcceptsDonations(false);
        } else {
            profile = profileOptional.get();
        }
        
        // Update fields if provided
        if (request.getAddress() != null) profile.setAddress(request.getAddress());
        if (request.getShelterName() != null) profile.setShelterName(request.getShelterName());
        if (request.getContactPersonName() != null) profile.setContactPersonName(request.getContactPersonName());
        if (request.getRegistrationNumber() != null) profile.setRegistrationNumber(request.getRegistrationNumber());
        if (request.getWebsite() != null) profile.setWebsite(request.getWebsite());
        if (request.getDescription() != null) profile.setDescription(request.getDescription());
        if (request.getCapacity() != null) profile.setCapacity(request.getCapacity());
        if (request.getCurrentOccupancy() != null) profile.setCurrentOccupancy(request.getCurrentOccupancy());
        if (request.getImages() != null) profile.setImages(request.getImages());
        if (request.getAcceptsDonations() != null) profile.setAcceptsDonations(request.getAcceptsDonations());
        if (request.getOperatingHours() != null) profile.setOperatingHours(request.getOperatingHours());
        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl());
            userRepository.save(user);
        }

        shelterProfileRepository.save(profile);

        return getShelterProfileData(user);
    }

    public UserProfileResponse.PetOwnerProfileData getPetOwnerProfile(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();
        Optional<PetOwnerProfile> profileOptional = petOwnerProfileRepository.findByUser(user);
        
        PetOwnerProfile profile;
        if (profileOptional.isEmpty()) {
            // Create new profile if doesn't exist
            profile = new PetOwnerProfile();
            profile.setUser(user);
            profile.setAddress("");
            profile.setEmergencyContactName("");
            profile.setEmergencyContactPhone("");
            profile.setBio("");
            profile.setAllowAccountSharing(false);
            profile = petOwnerProfileRepository.save(profile);
        } else {
            profile = profileOptional.get();
        }

        UserProfileResponse.PetOwnerProfileData data = new UserProfileResponse.PetOwnerProfileData();
        data.setProfileId(profile.getId());
        data.setAddress(profile.getAddress());
        data.setEmergencyContactName(profile.getEmergencyContactName());
        data.setEmergencyContactPhone(profile.getEmergencyContactPhone());
        data.setProfileImageUrl(profile.getProfileImageUrl());
        data.setBio(profile.getBio());
        data.setAllowAccountSharing(profile.getAllowAccountSharing());
        data.setCreatedAt(profile.getCreatedAt());
        data.setUpdatedAt(profile.getUpdatedAt());

        return data;
    }

    public UserProfileResponse.VeterinarianProfileData getVeterinarianProfile(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();
        Optional<VeterinarianProfile> profileOptional = veterinarianProfileRepository.findByUser(user);
        
        VeterinarianProfile profile;
        if (profileOptional.isEmpty()) {
            // Create new profile if doesn't exist
            profile = new VeterinarianProfile();
            profile.setUser(user);
            profile.setAddress("");
            profile.setLicenseNumber("");
            profile.setSpecializations("");
            profile.setExperienceYears(0);
            profile.setClinicName("");
            profile.setClinicAddress("");
            profile.setIsAvailableForEmergency(false);
            profile.setIsProfileComplete(false);
            profile = veterinarianProfileRepository.save(profile);
        } else {
            profile = profileOptional.get();
        }

        UserProfileResponse.VeterinarianProfileData data = new UserProfileResponse.VeterinarianProfileData();
        data.setProfileId(profile.getId());
        data.setAddress(profile.getAddress());
        data.setLicenseNumber(profile.getLicenseNumber());
        data.setExperienceYears(profile.getExperienceYears());
        data.setSpecializations(profile.getSpecializationsList());
        data.setClinicName(profile.getClinicName());
        data.setClinicAddress(profile.getClinicAddress());
        data.setAvailableFromTime(profile.getAvailableFromTime());
        data.setAvailableToTime(profile.getAvailableToTime());
        data.setAvailableDays(profile.getAvailableDaysList());
        data.setProfileImageUrl(profile.getProfileImageUrl());
        data.setBio(profile.getBio());
        data.setConsultationFee(profile.getConsultationFee());
        data.setIsAvailableForEmergency(profile.getIsAvailableForEmergency());
        data.setIsProfileComplete(profile.getIsProfileComplete());
        data.setCreatedAt(profile.getCreatedAt());
        data.setUpdatedAt(profile.getUpdatedAt());

        return data;
    }

    public UserProfileResponse.ShelterProfileData getShelterProfile(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();
        Optional<ShelterProfile> profileOptional = shelterProfileRepository.findByUser(user);
        
        ShelterProfile profile;
        if (profileOptional.isEmpty()) {
            // Create new profile if doesn't exist
            profile = new ShelterProfile();
            profile.setUser(user);
            profile.setShelterName("");
            profile.setAddress("");
            profile.setContactPersonName("");
            profile.setCapacity(0);
            profile.setCurrentOccupancy(0);
            profile.setAcceptsDonations(false);
            profile = shelterProfileRepository.save(profile);
        } else {
            profile = profileOptional.get();
        }

        UserProfileResponse.ShelterProfileData data = new UserProfileResponse.ShelterProfileData();
        data.setProfileId(profile.getId());
        data.setShelterName(profile.getShelterName());
        data.setAddress(profile.getAddress());
        data.setContactPersonName(profile.getContactPersonName());
        data.setRegistrationNumber(profile.getRegistrationNumber());
        data.setWebsite(profile.getWebsite());
        data.setDescription(profile.getDescription());
        data.setCapacity(profile.getCapacity());
        data.setCurrentOccupancy(profile.getCurrentOccupancy());
        data.setProfileImageUrl(user.getProfileImageUrl());
        data.setImages(profile.getImages());
        data.setIsVerified(profile.getIsVerified());
        data.setAcceptsDonations(profile.getAcceptsDonations());
        data.setOperatingHours(profile.getOperatingHours());
        data.setCreatedAt(profile.getCreatedAt());
        data.setUpdatedAt(profile.getUpdatedAt());

        return data;
    }
}
