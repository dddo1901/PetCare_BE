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

import TechWiz.shelter.models.Pet;
import TechWiz.shelter.models.Shelter;
import TechWiz.shelter.models.AdoptionInquiry;
import TechWiz.shelter.repositories.PetRepository;
import TechWiz.shelter.repositories.ShelterRepository;
import TechWiz.shelter.repositories.AdoptionInquiryRepository;
import TechWiz.shelter.dto.*;

@Service
@Transactional
public class PetService {
    
    @Autowired
    private PetRepository petRepository;
    
    @Autowired
    private ShelterRepository shelterRepository;
    
    @Autowired
    private AdoptionInquiryRepository adoptionInquiryRepository;
    
    @Autowired
    private ShelterService shelterService;
    
    public PetResponseDto createPet(Long shelterId, PetRequestDto requestDto) {
        Shelter shelter = shelterRepository.findById(shelterId)
            .orElseThrow(() -> new RuntimeException("Shelter not found with id: " + shelterId));
        
        Pet pet = new Pet();
        mapRequestDtoToPet(requestDto, pet);
        pet.setShelter(shelter);
        
        Pet savedPet = petRepository.save(pet);
        return convertToResponseDto(savedPet);
    }
    
    public PetResponseDto getPetById(Long id) {
        Pet pet = petRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pet not found with id: " + id));
        return convertToResponseDto(pet);
    }
    
    public Page<PetResponseDto> getPetsByShelterId(Long shelterId, 
                                                  String name, 
                                                  Pet.PetType type, 
                                                  String breed,
                                                  Pet.AdoptionStatus adoptionStatus,
                                                  Pet.HealthStatus healthStatus,
                                                  Pet.Gender gender,
                                                  Pet.Size size,
                                                  Pageable pageable) {
        Page<Pet> pets = petRepository.findPetsWithFilters(
            shelterId, name, type, breed, adoptionStatus, healthStatus, gender, size, pageable);
        
        List<PetResponseDto> dtos = pets.getContent().stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
            
        return new PageImpl<>(dtos, pageable, pets.getTotalElements());
    }
    
    public List<PetResponseDto> getAvailablePetsByShelterId(Long shelterId) {
        List<Pet> pets = petRepository.findByShelterIdAndAdoptionStatus(
            shelterId, Pet.AdoptionStatus.AVAILABLE);
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
        
        // Set shelter info
        dto.setShelter(shelterService.convertToBasicInfoDto(pet.getShelter()));
        
        // Add statistics
        dto.setTotalInquiries((long) pet.getAdoptionInquiries().size());
        dto.setPendingInquiries(adoptionInquiryRepository.countByShelterIdAndStatus(
            pet.getShelter().getId(), AdoptionInquiry.InquiryStatus.NEW));
        
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
}
