package TechWiz.shelter.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import TechWiz.auths.models.ShelterProfile;
import TechWiz.auths.repositories.ShelterProfileRepository;
import TechWiz.shelter.dto.PetBasicInfoDto;
import TechWiz.shelter.dto.PetRequestDto;
import TechWiz.shelter.dto.PetResponseDto;
import TechWiz.shelter.dto.ShelterBasicInfoDto;
import TechWiz.shelter.models.AdoptionInquiry;
import TechWiz.shelter.models.Pet;
import TechWiz.shelter.repositories.AdoptionInquiryRepository;
import TechWiz.shelter.repositories.ShelterPetRepository;

@Service
@Transactional
public class ShelterPetService {
    
    @Autowired
    private ShelterPetRepository petRepository;
    
    @Autowired
    private ShelterProfileRepository shelterProfileRepository;
    
    @Autowired
    private AdoptionInquiryRepository adoptionInquiryRepository;
    
    public PetResponseDto createPet(Long shelterProfileId, PetRequestDto requestDto) {
        ShelterProfile shelterProfile = shelterProfileRepository.findById(shelterProfileId)
            .orElseThrow(() -> new RuntimeException("Shelter profile not found with id: " + shelterProfileId));
        
        Pet pet = new Pet();
        mapRequestDtoToPet(requestDto, pet);
        pet.setShelterProfile(shelterProfile);
        
        Pet savedPet = petRepository.save(pet);
        return convertToResponseDto(savedPet);
    }
    
    public PetResponseDto getPetById(Long id) {
        Pet pet = petRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pet not found with id: " + id));
        return convertToResponseDto(pet);
    }
    
    public Page<PetResponseDto> getPetsByShelterId(Long shelterProfileId, 
                                                  String name, 
                                                  Pet.PetType type, 
                                                  String breed,
                                                  Pet.AdoptionStatus adoptionStatus,
                                                  Pet.HealthStatus healthStatus,
                                                  Pet.Gender gender,
                                                  Pet.Size size,
                                                  Pageable pageable) {
        Page<Pet> pets = petRepository.findPetsWithFilters(
            shelterProfileId, name, type, breed, adoptionStatus, healthStatus, gender, size, pageable);
        
        List<PetResponseDto> dtos = pets.getContent().stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
            
        return new PageImpl<>(dtos, pageable, pets.getTotalElements());
    }
    
    public List<PetResponseDto> getAvailablePetsByShelterId(Long shelterProfileId) {
        List<Pet> pets = petRepository.findByShelterProfileIdAndAdoptionStatus(
            shelterProfileId, Pet.AdoptionStatus.AVAILABLE);
        return pets.stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
    }
    
    public Page<PetResponseDto> getAvailablePetsForAdoption(Pet.PetType type,
                                                           String breed,
                                                           Pet.Gender gender,
                                                           Pet.Size size,
                                                           Pageable pageable) {
        Page<Pet> pets = petRepository.findAvailablePetsWithFilters(
            type, breed, gender, size, pageable);
        
        List<PetResponseDto> dtos = pets.getContent().stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
            
        return new PageImpl<>(dtos, pageable, pets.getTotalElements());
    }
    
    public PetResponseDto updatePet(Long id, PetRequestDto requestDto) {
        Pet pet = petRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pet not found with id: " + id));
        
        mapRequestDtoToPet(requestDto, pet);
        pet.setUpdatedAt(LocalDateTime.now());
        
        Pet updatedPet = petRepository.save(pet);
        return convertToResponseDto(updatedPet);
    }
    
    public PetResponseDto updatePetAdoptionStatus(Long id, Pet.AdoptionStatus adoptionStatus) {
        Pet pet = petRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pet not found with id: " + id));
        
        pet.setAdoptionStatus(adoptionStatus);
        pet.setUpdatedAt(LocalDateTime.now());
        
        Pet updatedPet = petRepository.save(pet);
        return convertToResponseDto(updatedPet);
    }
    
    public void deletePet(Long id) {
        if (!petRepository.existsById(id)) {
            throw new RuntimeException("Pet not found with id: " + id);
        }
        petRepository.deleteById(id);
    }
    
    private void mapRequestDtoToPet(PetRequestDto requestDto, Pet pet) {
        pet.setName(requestDto.getName());
        pet.setType(requestDto.getType());
        pet.setBreed(requestDto.getBreed());
        pet.setAgeInMonths(requestDto.getAgeInMonths());
        pet.setGender(requestDto.getGender());
        pet.setSize(requestDto.getSize());
        pet.setColor(requestDto.getColor());
        pet.setWeight(requestDto.getWeight());
        pet.setImageUrl(requestDto.getImageUrl());
        pet.setDescription(requestDto.getDescription());
        pet.setAdoptionStatus(requestDto.getAdoptionStatus() != null ? 
            requestDto.getAdoptionStatus() : Pet.AdoptionStatus.AVAILABLE);
        pet.setHealthStatus(requestDto.getHealthStatus() != null ? 
            requestDto.getHealthStatus() : Pet.HealthStatus.HEALTHY);
        pet.setVaccinated(requestDto.getVaccinated() != null ? requestDto.getVaccinated() : false);
        pet.setSpayedNeutered(requestDto.getSpayedNeutered() != null ? requestDto.getSpayedNeutered() : false);
        pet.setMicrochipped(requestDto.getMicrochipped() != null ? requestDto.getMicrochipped() : false);
        pet.setHouseTrained(requestDto.getHouseTrained() != null ? requestDto.getHouseTrained() : false);
        pet.setGoodWithKids(requestDto.getGoodWithKids() != null ? requestDto.getGoodWithKids() : false);
        pet.setGoodWithPets(requestDto.getGoodWithPets() != null ? requestDto.getGoodWithPets() : false);
        pet.setEnergyLevel(requestDto.getEnergyLevel() != null ? 
            requestDto.getEnergyLevel() : Pet.EnergyLevel.MEDIUM);
        pet.setSpecialNeeds(requestDto.getSpecialNeeds());
        pet.setAdoptionFee(requestDto.getAdoptionFee());
        pet.setPersonality(requestDto.getPersonality());
        pet.setRequirements(requestDto.getRequirements());
    }
    
    private PetResponseDto convertToResponseDto(Pet pet) {
        PetResponseDto dto = new PetResponseDto();
        dto.setId(pet.getId());
        dto.setName(pet.getName());
        dto.setType(pet.getType());
        dto.setBreed(pet.getBreed());
        dto.setAgeInMonths(pet.getAgeInMonths());
        dto.setGender(pet.getGender());
        dto.setSize(pet.getSize());
        dto.setColor(pet.getColor());
        dto.setWeight(pet.getWeight());
        dto.setImageUrl(pet.getImageUrl());
        dto.setDescription(pet.getDescription());
        dto.setAdoptionStatus(pet.getAdoptionStatus());
        dto.setHealthStatus(pet.getHealthStatus());
        dto.setVaccinated(pet.getVaccinated());
        dto.setSpayedNeutered(pet.getSpayedNeutered());
        dto.setMicrochipped(pet.getMicrochipped());
        dto.setHouseTrained(pet.getHouseTrained());
        dto.setGoodWithKids(pet.getGoodWithKids());
        dto.setGoodWithPets(pet.getGoodWithPets());
        dto.setEnergyLevel(pet.getEnergyLevel());
        dto.setSpecialNeeds(pet.getSpecialNeeds());
        dto.setAdoptionFee(pet.getAdoptionFee());
        dto.setPersonality(pet.getPersonality());
        dto.setRequirements(pet.getRequirements());
        dto.setCreatedAt(pet.getCreatedAt());
        dto.setUpdatedAt(pet.getUpdatedAt());
        
        // Set shelter info from shelter profile
        if (pet.getShelterProfile() != null) {
            // Create a basic shelter info DTO from shelter profile
            ShelterBasicInfoDto shelterInfo = new ShelterBasicInfoDto();
            shelterInfo.setId(pet.getShelterProfile().getId());
            shelterInfo.setShelterName(pet.getShelterProfile().getShelterName());
            shelterInfo.setContactPersonName(pet.getShelterProfile().getContactPersonName());
            shelterInfo.setAddress(pet.getShelterProfile().getAddress());
            shelterInfo.setImageUrl(pet.getShelterProfile().getProfileImageUrl());
            dto.setShelter(shelterInfo);
        }
        
        // Add statistics
        dto.setTotalInquiries((long) pet.getAdoptionInquiries().size());
        // Count pending inquiries by shelter profile ID
        if (pet.getShelterProfile() != null) {
            dto.setPendingInquiries(adoptionInquiryRepository.countByShelterProfileIdAndStatus(
                pet.getShelterProfile().getId(), AdoptionInquiry.InquiryStatus.NEW));
        }
        
        return dto;
    }
    
    public PetBasicInfoDto convertToBasicInfoDto(Pet pet) {
        PetBasicInfoDto dto = new PetBasicInfoDto();
        dto.setId(pet.getId());
        dto.setName(pet.getName());
        dto.setType(pet.getType());
        dto.setBreed(pet.getBreed());
        dto.setImageUrl(pet.getImageUrl());
        return dto;
    }
    
    // Statistics methods for shelter dashboard
    public Long getTotalPetsByShelterProfile(Long shelterProfileId) {
        return petRepository.countByShelterProfileId(shelterProfileId);
    }
    
    public Long getPetCountByStatus(Long shelterProfileId, Pet.AdoptionStatus status) {
        return petRepository.countByShelterProfileIdAndAdoptionStatus(shelterProfileId, status);
    }
    
    public Long getTotalViewsByShelterProfile(Long shelterProfileId) {
        return petRepository.findByShelterProfileId(shelterProfileId).stream()
            .mapToLong(pet -> 0L) // placeholder - would need views field in Pet model
            .sum();
    }
    
    public Long getTotalApplicationsByShelterProfile(Long shelterProfileId) {
        return adoptionInquiryRepository.countByShelterProfileId(shelterProfileId);
    }
    
    public void incrementPetViews(Long petId) {
        Pet pet = petRepository.findById(petId)
            .orElseThrow(() -> new RuntimeException("Pet not found with id: " + petId));
        // Would increment view count if field exists in Pet model
        // For now, just update the updatedAt field to track access
        pet.setUpdatedAt(LocalDateTime.now());
        petRepository.save(pet);
    }
}
