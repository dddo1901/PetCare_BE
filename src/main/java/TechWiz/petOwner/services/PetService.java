package TechWiz.petOwner.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import TechWiz.petOwner.dto.CreatePetRequest;
import TechWiz.petOwner.models.Pet;
import TechWiz.petOwner.repositories.PetRepository;

@Service
@Transactional
public class PetService {

    @Autowired
    private PetRepository petRepository;

    // Create new pet
    public Pet createPet(CreatePetRequest request, Long ownerId) {
        Pet pet = new Pet();
        pet.setName(request.getName());
        pet.setType(request.getType());
        pet.setBreed(request.getBreed());
        pet.setAge(request.getAge());
        pet.setWeight(request.getWeight());
        pet.setGender(request.getGender());
        pet.setColor(request.getColor());
        pet.setDateOfBirth(request.getDateOfBirth());
        pet.setMicrochipId(request.getMicrochipId());
        pet.setHealthStatus("HEALTHY"); // Default status
        pet.setOwnerId(ownerId);
        pet.setIsActive(true);
        
        // Set default photos if provided
        if (request.getPhotos() != null && !request.getPhotos().isEmpty()) {
            pet.setPhotos(String.join(",", request.getPhotos()));
        }
        
        return petRepository.save(pet);
    }

    // Get all pets by owner
    @Transactional(readOnly = true)
    public List<Pet> getPetsByOwner(Long ownerId) {
        return petRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId);
    }

    // Get pets by owner with pagination
    @Transactional(readOnly = true)
    public Page<Pet> getPetsByOwner(Long ownerId, Pageable pageable) {
        return petRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId, pageable);
    }

    // Get pet by ID (with owner verification)
    @Transactional(readOnly = true)
    public Optional<Pet> getPetById(Long petId, Long ownerId) {
        return petRepository.findByIdAndOwnerId(petId, ownerId);
    }

    // Update pet information
    public Pet updatePet(Long petId, CreatePetRequest request, Long ownerId) {
        Pet pet = petRepository.findByIdAndOwnerId(petId, ownerId)
            .orElseThrow(() -> new RuntimeException("Pet not found or doesn't belong to owner"));
        
        pet.setName(request.getName());
        pet.setType(request.getType());
        pet.setBreed(request.getBreed());
        pet.setAge(request.getAge());
        pet.setWeight(request.getWeight());
        pet.setGender(request.getGender());
        pet.setColor(request.getColor());
        pet.setDateOfBirth(request.getDateOfBirth());
        pet.setMicrochipId(request.getMicrochipId());
        
        // Update photos if provided
        if (request.getPhotos() != null && !request.getPhotos().isEmpty()) {
            pet.setPhotos(String.join(",", request.getPhotos()));
        }
        
        return petRepository.save(pet);
    }

    // Delete pet (soft delete by setting isActive to false)
    public boolean deletePet(Long petId, Long ownerId) {
        Optional<Pet> petOpt = petRepository.findByIdAndOwnerId(petId, ownerId);
        if (petOpt.isPresent()) {
            Pet pet = petOpt.get();
            pet.setIsActive(false);
            petRepository.save(pet);
            return true;
        }
        return false;
    }

    // Search pets by keyword (name or breed)
    @Transactional(readOnly = true)
    public List<Pet> searchPets(Long ownerId, String keyword) {
        return petRepository.searchPetsByOwner(ownerId, keyword);
    }

    // Get pets by type
    @Transactional(readOnly = true)
    public List<Pet> getPetsByType(Long ownerId, String type) {
        return petRepository.findByOwnerIdAndTypeOrderByCreatedAtDesc(ownerId, type);
    }

    // Get pets by health status
    @Transactional(readOnly = true)
    public List<Pet> getPetsByHealthStatus(Long ownerId, String healthStatus) {
        return petRepository.findByOwnerIdAndHealthStatusOrderByCreatedAtDesc(ownerId, healthStatus);
    }

    // Get pets needing vaccination
    @Transactional(readOnly = true)
    public List<Pet> getPetsNeedingVaccination(Long ownerId) {
        LocalDate thirtyDaysFromNow = LocalDate.now().plusDays(30);
        return petRepository.findPetsNeedingVaccination(ownerId, thirtyDaysFromNow);
    }

    // Get pets needing checkup (last checkup > 6 months ago)
    @Transactional(readOnly = true)
    public List<Pet> getPetsNeedingCheckup(Long ownerId) {
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
        return petRepository.findPetsNeedingCheckup(ownerId, sixMonthsAgo);
    }

    // Update pet health status
    public Pet updateHealthStatus(Long petId, String healthStatus, Long ownerId) {
        Pet pet = petRepository.findByIdAndOwnerId(petId, ownerId)
            .orElseThrow(() -> new RuntimeException("Pet not found or doesn't belong to owner"));
        
        pet.setHealthStatus(healthStatus);
        return petRepository.save(pet);
    }

    // Update last checkup date
    public Pet updateLastCheckup(Long petId, LocalDate checkupDate, Long ownerId) {
        Pet pet = petRepository.findByIdAndOwnerId(petId, ownerId)
            .orElseThrow(() -> new RuntimeException("Pet not found or doesn't belong to owner"));
        
        pet.setLastCheckup(checkupDate);
        return petRepository.save(pet);
    }

    // Update next vaccination date
    public Pet updateNextVaccination(Long petId, LocalDate vaccinationDate, Long ownerId) {
        Pet pet = petRepository.findByIdAndOwnerId(petId, ownerId)
            .orElseThrow(() -> new RuntimeException("Pet not found or doesn't belong to owner"));
        
        pet.setNextVaccination(vaccinationDate);
        return petRepository.save(pet);
    }

    // Add photo to pet gallery
    public Pet addPhoto(Long petId, String photoUrl, Long ownerId) {
        Pet pet = petRepository.findByIdAndOwnerId(petId, ownerId)
            .orElseThrow(() -> new RuntimeException("Pet not found or doesn't belong to owner"));
        
        String currentPhotos = pet.getPhotos();
        if (currentPhotos == null || currentPhotos.isEmpty()) {
            pet.setPhotos(photoUrl);
        } else {
            pet.setPhotos(currentPhotos + "," + photoUrl);
        }
        
        return petRepository.save(pet);
    }

    // Remove photo from pet gallery
    public Pet removePhoto(Long petId, String photoUrl, Long ownerId) {
        Pet pet = petRepository.findByIdAndOwnerId(petId, ownerId)
            .orElseThrow(() -> new RuntimeException("Pet not found or doesn't belong to owner"));
        
        String currentPhotos = pet.getPhotos();
        if (currentPhotos != null && !currentPhotos.isEmpty()) {
            String updatedPhotos = currentPhotos.replace(photoUrl, "")
                                                .replace(",,", ",")
                                                .replaceAll("^,|,$", "");
            pet.setPhotos(updatedPhotos.isEmpty() ? null : updatedPhotos);
        }
        
        return petRepository.save(pet);
    }

    // Get pet count by owner
    @Transactional(readOnly = true)
    public long getPetCountByOwner(Long ownerId) {
        return petRepository.countByOwnerId(ownerId);
    }

    // Get pet count by owner and type
    @Transactional(readOnly = true)
    public long getPetCountByOwnerAndType(Long ownerId, String type) {
        return petRepository.countByOwnerIdAndType(ownerId, type);
    }

    // Check if pet belongs to owner
    @Transactional(readOnly = true)
    public boolean isPetOwnedBy(Long petId, Long ownerId) {
        return petRepository.existsByIdAndOwnerId(petId, ownerId);
    }
}
