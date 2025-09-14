package TechWiz.petOwner.services;

import TechWiz.petOwner.dto.PetOwnerPetRequest;
import TechWiz.petOwner.dto.PetOwnerPetResponse;
import TechWiz.petOwner.models.PetOwnerPet;
import TechWiz.petOwner.repositories.PetOwnerPetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PetOwnerPetService {
    
    @Autowired
    private PetOwnerPetRepository petRepository;
    
    public PetOwnerPetResponse createPet(PetOwnerPetRequest request, Long ownerId) {
        PetOwnerPet pet = new PetOwnerPet();
        pet.setName(request.getName());
        pet.setType(PetOwnerPet.PetType.valueOf(request.getType()));
        pet.setBreed(request.getBreed());
        pet.setAgeInMonths(request.getAgeInMonths());
        pet.setGender(PetOwnerPet.Gender.valueOf(request.getGender()));
        pet.setColor(request.getColor());
        pet.setWeight(request.getWeight());
        pet.setMicrochip(request.getMicrochip());
        pet.setImageUrl(request.getImageUrl());
        // Handle photos - ensure it's properly formatted
        String photos = request.getPhotos();
        if (photos != null && !photos.trim().isEmpty()) {
            // If it's not JSON format, convert to JSON array
            if (!photos.startsWith("[")) {
                // Convert single photo or comma-separated photos to JSON array
                String[] photoArray = photos.split(",");
                StringBuilder jsonPhotos = new StringBuilder("[");
                for (int i = 0; i < photoArray.length; i++) {
                    if (i > 0) jsonPhotos.append(",");
                    // Clean the photo URL before adding quotes
                    String cleanPhoto = photoArray[i].trim().replaceAll("^\"|\"$", "");
                    jsonPhotos.append("\"").append(cleanPhoto).append("\"");
                }
                jsonPhotos.append("]");
                pet.setPhotos(jsonPhotos.toString());
            } else {
                pet.setPhotos(photos);
            }
        } else {
            pet.setPhotos("[]");
        }
        pet.setDescription(request.getDescription());
        pet.setHealthStatus(PetOwnerPet.HealthStatus.valueOf(request.getHealthStatus()));
        pet.setVaccinated(request.getVaccinated());
        pet.setSpayedNeutered(request.getSpayedNeutered());
        pet.setMicrochipped(request.getMicrochipped());
        pet.setHouseTrained(request.getHouseTrained());
        pet.setGoodWithKids(request.getGoodWithKids() != null ? request.getGoodWithKids() : false);
        pet.setGoodWithPets(request.getGoodWithPets() != null ? request.getGoodWithPets() : false);
        pet.setEnergyLevel(request.getEnergyLevel() != null ? PetOwnerPet.EnergyLevel.valueOf(request.getEnergyLevel()) : PetOwnerPet.EnergyLevel.MEDIUM);
        pet.setSpecialNeeds(request.getSpecialNeeds());
        pet.setPersonality(request.getPersonality());
        pet.setRequirements(request.getRequirements());
        pet.setOwnerId(ownerId);
        
        PetOwnerPet savedPet = petRepository.save(pet);
        return convertToResponse(savedPet);
    }
    
    public List<PetOwnerPetResponse> getPetsByOwner(Long ownerId) {
        List<PetOwnerPet> pets = petRepository.findByOwnerId(ownerId);
        return pets.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    public Page<PetOwnerPetResponse> getPetsByOwner(Long ownerId, Pageable pageable) {
        Page<PetOwnerPet> pets = petRepository.findByOwnerId(ownerId, pageable);
        return pets.map(this::convertToResponse);
    }
    
    public Optional<PetOwnerPetResponse> getPetById(Long petId, Long ownerId) {
        Optional<PetOwnerPet> pet = petRepository.findById(petId)
                .filter(p -> p.getOwnerId().equals(ownerId));
        return pet.map(this::convertToResponse);
    }
    
    public PetOwnerPetResponse updatePet(Long petId, PetOwnerPetRequest request, Long ownerId) {
        PetOwnerPet pet = petRepository.findById(petId)
                .filter(p -> p.getOwnerId().equals(ownerId))
                .orElseThrow(() -> new RuntimeException("Pet not found"));
        
        // Store old photos to delete them later
        String oldPhotos = pet.getPhotos();
        
        pet.setName(request.getName());
        pet.setType(PetOwnerPet.PetType.valueOf(request.getType()));
        pet.setBreed(request.getBreed());
        pet.setAgeInMonths(request.getAgeInMonths());
        pet.setGender(PetOwnerPet.Gender.valueOf(request.getGender()));
        pet.setColor(request.getColor());
        pet.setWeight(request.getWeight());
        pet.setMicrochip(request.getMicrochip());
        pet.setImageUrl(request.getImageUrl());
        // Handle photos - ensure it's properly formatted
        String photos = request.getPhotos();
        if (photos != null && !photos.trim().isEmpty()) {
            // If it's not JSON format, convert to JSON array
            if (!photos.startsWith("[")) {
                // Convert single photo or comma-separated photos to JSON array
                String[] photoArray = photos.split(",");
                StringBuilder jsonPhotos = new StringBuilder("[");
                for (int i = 0; i < photoArray.length; i++) {
                    if (i > 0) jsonPhotos.append(",");
                    // Clean the photo URL before adding quotes
                    String cleanPhoto = photoArray[i].trim().replaceAll("^\"|\"$", "");
                    jsonPhotos.append("\"").append(cleanPhoto).append("\"");
                }
                jsonPhotos.append("]");
                pet.setPhotos(jsonPhotos.toString());
            } else {
                pet.setPhotos(photos);
            }
        } else {
            pet.setPhotos("[]");
        }
        pet.setDescription(request.getDescription());
        pet.setHealthStatus(PetOwnerPet.HealthStatus.valueOf(request.getHealthStatus()));
        pet.setVaccinated(request.getVaccinated());
        pet.setSpayedNeutered(request.getSpayedNeutered());
        pet.setMicrochipped(request.getMicrochipped());
        pet.setHouseTrained(request.getHouseTrained());
        pet.setGoodWithKids(request.getGoodWithKids() != null ? request.getGoodWithKids() : false);
        pet.setGoodWithPets(request.getGoodWithPets() != null ? request.getGoodWithPets() : false);
        pet.setEnergyLevel(request.getEnergyLevel() != null ? PetOwnerPet.EnergyLevel.valueOf(request.getEnergyLevel()) : PetOwnerPet.EnergyLevel.MEDIUM);
        pet.setSpecialNeeds(request.getSpecialNeeds());
        pet.setPersonality(request.getPersonality());
        pet.setRequirements(request.getRequirements());
        
        PetOwnerPet updatedPet = petRepository.save(pet);
        
        // Delete old photos if they exist and are different from new ones
        if (oldPhotos != null && !oldPhotos.trim().isEmpty() && !oldPhotos.equals(updatedPet.getPhotos())) {
            deleteOldPhotos(oldPhotos);
        }
        
        return convertToResponse(updatedPet);
    }
    
    public boolean deletePet(Long petId, Long ownerId) {
        Optional<PetOwnerPet> pet = petRepository.findById(petId)
                .filter(p -> p.getOwnerId().equals(ownerId));
        
        if (pet.isPresent()) {
            // Delete photos before deleting pet
            String photos = pet.get().getPhotos();
            if (photos != null && !photos.trim().isEmpty()) {
                deleteOldPhotos(photos);
            }
            
            petRepository.delete(pet.get());
            return true;
        }
        return false;
    }
    
    public List<PetOwnerPetResponse> searchPets(Long ownerId, String keyword) {
        List<PetOwnerPet> pets = petRepository.findByOwnerIdAndKeyword(ownerId, keyword);
        return pets.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    public List<PetOwnerPetResponse> getPetsByType(Long ownerId, String type) {
        List<PetOwnerPet> pets = petRepository.findByOwnerIdAndType(ownerId, PetOwnerPet.PetType.valueOf(type));
        return pets.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    public List<PetOwnerPetResponse> getPetsByHealthStatus(Long ownerId, String status) {
        List<PetOwnerPet> pets = petRepository.findByOwnerIdAndHealthStatus(ownerId, PetOwnerPet.HealthStatus.valueOf(status));
        return pets.stream().map(this::convertToResponse).collect(Collectors.toList());
    }
    
    public Long getPetCountByOwner(Long ownerId) {
        return petRepository.countByOwnerId(ownerId);
    }
    
    public Long getPetCountByOwnerAndType(Long ownerId, String type) {
        return petRepository.countByOwnerIdAndType(ownerId, PetOwnerPet.PetType.valueOf(type));
    }
    
    private PetOwnerPetResponse convertToResponse(PetOwnerPet pet) {
        PetOwnerPetResponse response = new PetOwnerPetResponse();
        response.setId(pet.getId());
        response.setName(pet.getName());
        response.setType(pet.getType().name());
        response.setBreed(pet.getBreed());
        response.setAge(convertAgeToString(pet.getAgeInMonths()));
        response.setGender(pet.getGender().name());
        response.setColor(pet.getColor());
        response.setWeight(pet.getWeight());
        response.setMicrochip(pet.getMicrochip());
        response.setImageUrl(pet.getImageUrl());
        response.setPhotos(convertPhotosToArray(pet.getPhotos()));
        response.setDescription(pet.getDescription());
        response.setHealthStatus(pet.getHealthStatus().name());
        response.setVaccinated(pet.getVaccinated());
        response.setSpayedNeutered(pet.getSpayedNeutered());
        response.setMicrochipped(pet.getMicrochipped());
        response.setHouseTrained(pet.getHouseTrained());
        response.setGoodWithKids(pet.getGoodWithKids());
        response.setGoodWithPets(pet.getGoodWithPets());
        response.setEnergyLevel(pet.getEnergyLevel().name());
        response.setSpecialNeeds(pet.getSpecialNeeds());
        response.setPersonality(pet.getPersonality());
        response.setRequirements(pet.getRequirements());
        response.setCreatedAt(pet.getCreatedAt());
        response.setUpdatedAt(pet.getUpdatedAt());
        response.setOwnerId(pet.getOwnerId());
        return response;
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
    
    private String[] convertPhotosToArray(String photos) {
        if (photos == null || photos.trim().isEmpty()) {
            return new String[0];
        }
        
        try {
            // Try to parse as JSON array first
            if (photos.startsWith("[") && photos.endsWith("]")) {
                // Simple JSON array parsing - remove brackets and quotes, then split
                String cleaned = photos.substring(1, photos.length() - 1); // Remove [ and ]
                if (cleaned.trim().isEmpty()) {
                    return new String[0];
                }
                // Split by comma and clean each URL
                String[] urls = cleaned.split(",");
                for (int i = 0; i < urls.length; i++) {
                    urls[i] = urls[i].trim().replaceAll("^\"|\"$", "");
                }
                return urls;
            } else {
                // If not JSON format, treat as comma-separated string
                String[] urls = photos.split(",");
                for (int i = 0; i < urls.length; i++) {
                    urls[i] = urls[i].trim().replaceAll("^\"|\"$", "");
                }
                return urls;
            }
        } catch (Exception e) {
            System.err.println("Error parsing photos: " + e.getMessage());
            return new String[0];
        }
    }
    
    private void deleteOldPhotos(String oldPhotos) {
        try {
            String[] photoArray = convertPhotosToArray(oldPhotos);
            for (String photo : photoArray) {
                if (photo != null && !photo.trim().isEmpty()) {
                    // Extract filename from URL
                    String filename = extractFilenameFromUrl(photo.trim());
                    if (filename != null) {
                        Path filePath = Paths.get("uploads/pets/" + filename);
                        if (Files.exists(filePath)) {
                            Files.delete(filePath);
                            System.out.println("Deleted old photo: " + filename);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error deleting old photos: " + e.getMessage());
        }
    }
    
    private String extractFilenameFromUrl(String photoUrl) {
        if (photoUrl == null || photoUrl.trim().isEmpty()) {
            return null;
        }
        
        // Remove quotes if present
        String cleanedUrl = photoUrl.trim().replaceAll("^\"|\"$", "");
        
        // Handle different URL formats
        if (cleanedUrl.contains("/api/pet-owner/photos/")) {
            return cleanedUrl.substring(cleanedUrl.lastIndexOf("/") + 1);
        } else if (cleanedUrl.contains("/")) {
            return cleanedUrl.substring(cleanedUrl.lastIndexOf("/") + 1);
        } else {
            return cleanedUrl;
        }
    }
}
