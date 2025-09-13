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

import TechWiz.shelter.models.Shelter;
import TechWiz.shelter.models.Pet;
import TechWiz.shelter.models.AdoptionInquiry;
import TechWiz.shelter.repositories.ShelterRepository;
import TechWiz.shelter.repositories.ShelterPetRepository;
import TechWiz.shelter.repositories.AdoptionInquiryRepository;
import TechWiz.shelter.dto.*;

@Service
@Transactional
public class ShelterService {
    
    @Autowired
    private ShelterRepository shelterRepository;
    
    @Autowired
    private ShelterPetRepository petRepository;
    
    @Autowired
    private AdoptionInquiryRepository adoptionInquiryRepository;
    
    public ShelterResponseDto registerShelter(ShelterRegistrationRequestDto requestDto) {
        // Check if email already exists
        if (shelterRepository.existsByEmail(requestDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Check if phone number already exists
        if (shelterRepository.existsByPhoneNumber(requestDto.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists");
        }
        
        Shelter shelter = new Shelter();
        shelter.setShelterName(requestDto.getShelterName());
        shelter.setContactPersonName(requestDto.getContactPersonName());
        shelter.setEmail(requestDto.getEmail());
        shelter.setPhoneNumber(requestDto.getPhoneNumber());
        shelter.setAddress(requestDto.getAddress());
        shelter.setDescription(requestDto.getDescription());
        shelter.setWebsite(requestDto.getWebsite());
        shelter.setImageUrl(requestDto.getImageUrl());
        shelter.setStatus(Shelter.ShelterStatus.PENDING);
        shelter.setIsVerified(false);
        
        Shelter savedShelter = shelterRepository.save(shelter);
        return convertToResponseDto(savedShelter);
    }
    
    public ShelterResponseDto getShelterById(Long id) {
        Shelter shelter = shelterRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Shelter not found with id: " + id));
        return convertToResponseDto(shelter);
    }
    
    public ShelterResponseDto getShelterByEmail(String email) {
        Shelter shelter = shelterRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Shelter not found with email: " + email));
        return convertToResponseDto(shelter);
    }
    
    public Page<ShelterResponseDto> getAllShelters(String shelterName, 
                                                  Shelter.ShelterStatus status, 
                                                  Boolean isVerified, 
                                                  Pageable pageable) {
        Page<Shelter> shelters = shelterRepository.findSheltersWithFilters(
            shelterName, status, isVerified, pageable);
        
        List<ShelterResponseDto> dtos = shelters.getContent().stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
            
        return new PageImpl<>(dtos, pageable, shelters.getTotalElements());
    }
    
    public List<ShelterResponseDto> getVerifiedShelters() {
        List<Shelter> shelters = shelterRepository.findByIsVerifiedTrue();
        return shelters.stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
    }
    
    public ShelterResponseDto updateShelter(Long id, ShelterRegistrationRequestDto requestDto) {
        Shelter shelter = shelterRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Shelter not found with id: " + id));
        
        // Check if email is being changed and if new email already exists
        if (!shelter.getEmail().equals(requestDto.getEmail()) && 
            shelterRepository.existsByEmail(requestDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        shelter.setShelterName(requestDto.getShelterName());
        shelter.setContactPersonName(requestDto.getContactPersonName());
        shelter.setEmail(requestDto.getEmail());
        shelter.setPhoneNumber(requestDto.getPhoneNumber());
        shelter.setAddress(requestDto.getAddress());
        shelter.setDescription(requestDto.getDescription());
        shelter.setWebsite(requestDto.getWebsite());
        shelter.setImageUrl(requestDto.getImageUrl());
        shelter.setUpdatedAt(LocalDateTime.now());
        
        Shelter updatedShelter = shelterRepository.save(shelter);
        return convertToResponseDto(updatedShelter);
    }
    
    public ShelterResponseDto updateShelterStatus(Long id, Shelter.ShelterStatus status) {
        Shelter shelter = shelterRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Shelter not found with id: " + id));
        
        shelter.setStatus(status);
        shelter.setUpdatedAt(LocalDateTime.now());
        
        Shelter updatedShelter = shelterRepository.save(shelter);
        return convertToResponseDto(updatedShelter);
    }
    
    public ShelterResponseDto verifyShelter(Long id, boolean verified) {
        Shelter shelter = shelterRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Shelter not found with id: " + id));
        
        shelter.setIsVerified(verified);
        if (verified && shelter.getStatus() == Shelter.ShelterStatus.PENDING) {
            shelter.setStatus(Shelter.ShelterStatus.ACTIVE);
        }
        shelter.setUpdatedAt(LocalDateTime.now());
        
        Shelter updatedShelter = shelterRepository.save(shelter);
        return convertToResponseDto(updatedShelter);
    }
    
    public void deleteShelter(Long id) {
        if (!shelterRepository.existsById(id)) {
            throw new RuntimeException("Shelter not found with id: " + id);
        }
        shelterRepository.deleteById(id);
    }
    
    private ShelterResponseDto convertToResponseDto(Shelter shelter) {
        ShelterResponseDto dto = new ShelterResponseDto();
        dto.setId(shelter.getId());
        dto.setShelterName(shelter.getShelterName());
        dto.setContactPersonName(shelter.getContactPersonName());
        dto.setEmail(shelter.getEmail());
        dto.setPhoneNumber(shelter.getPhoneNumber());
        dto.setAddress(shelter.getAddress());
        dto.setDescription(shelter.getDescription());
        dto.setWebsite(shelter.getWebsite());
        dto.setImageUrl(shelter.getImageUrl());
        dto.setStatus(shelter.getStatus());
        dto.setIsVerified(shelter.getIsVerified());
        dto.setCreatedAt(shelter.getCreatedAt());
        dto.setUpdatedAt(shelter.getUpdatedAt());
        
        // Add statistics
        dto.setTotalPets(petRepository.countByShelterIdAndAdoptionStatus(
            shelter.getId(), Pet.AdoptionStatus.AVAILABLE) +
            petRepository.countByShelterIdAndAdoptionStatus(
            shelter.getId(), Pet.AdoptionStatus.PENDING) +
            petRepository.countByShelterIdAndAdoptionStatus(
            shelter.getId(), Pet.AdoptionStatus.ADOPTED));
        dto.setAvailablePets(petRepository.countByShelterIdAndAdoptionStatus(
            shelter.getId(), Pet.AdoptionStatus.AVAILABLE));
        dto.setPendingInquiries(adoptionInquiryRepository.countByShelterIdAndStatus(
            shelter.getId(), AdoptionInquiry.InquiryStatus.NEW));
        
        return dto;
    }
    
    public ShelterBasicInfoDto convertToBasicInfoDto(Shelter shelter) {
        ShelterBasicInfoDto dto = new ShelterBasicInfoDto();
        dto.setId(shelter.getId());
        dto.setShelterName(shelter.getShelterName());
        dto.setContactPersonName(shelter.getContactPersonName());
        dto.setEmail(shelter.getEmail());
        dto.setPhoneNumber(shelter.getPhoneNumber());
        dto.setAddress(shelter.getAddress());
        dto.setImageUrl(shelter.getImageUrl());
        return dto;
    }
}
