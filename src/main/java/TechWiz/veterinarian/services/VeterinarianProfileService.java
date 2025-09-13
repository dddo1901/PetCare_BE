package TechWiz.veterinarian.services;

import TechWiz.auths.models.VeterinarianProfile;
import TechWiz.veterinarian.repositories.VeterinarianProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VeterinarianProfileService {
    
    @Autowired
    private VeterinarianProfileRepository veterinarianProfileRepository;
    
    // Get veterinarian profile ID by user ID
    public Long getVetIdByUserId(Long userId) {
        Optional<VeterinarianProfile> profile = veterinarianProfileRepository.findByUserId(userId);
        if (profile.isPresent()) {
            return profile.get().getId();
        }
        throw new RuntimeException("Veterinarian profile not found for user ID: " + userId);
    }
    
    // Get veterinarian profile by user ID
    public VeterinarianProfile getVeterinarianProfileByUserId(Long userId) {
        return veterinarianProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Veterinarian profile not found for user ID: " + userId));
    }
    
    // Check if user has veterinarian profile
    public boolean hasVeterinarianProfile(Long userId) {
        return veterinarianProfileRepository.existsByUserId(userId);
    }
}
