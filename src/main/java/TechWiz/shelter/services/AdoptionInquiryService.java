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

import TechWiz.shelter.models.AdoptionInquiry;
import TechWiz.shelter.models.Pet;
import TechWiz.shelter.models.Shelter;
import TechWiz.shelter.repositories.AdoptionInquiryRepository;
import TechWiz.shelter.repositories.ShelterPetRepository;
import TechWiz.shelter.repositories.ShelterRepository;
import TechWiz.shelter.dto.*;

@Service
@Transactional
public class AdoptionInquiryService {
    
    @Autowired
    private AdoptionInquiryRepository adoptionInquiryRepository;
    
    @Autowired
    private ShelterPetRepository petRepository;
    
    @Autowired
    private ShelterRepository shelterRepository;
    
    @Autowired
    private ShelterPetService petService;
    
    @Autowired
    private ShelterService shelterService;
    
    public AdoptionInquiryResponseDto createAdoptionInquiry(AdoptionInquiryRequestDto requestDto) {
        Pet pet = petRepository.findById(requestDto.getPetId())
            .orElseThrow(() -> new RuntimeException("Pet not found with id: " + requestDto.getPetId()));
        
        // Check if pet is available for adoption
        if (pet.getAdoptionStatus() != Pet.AdoptionStatus.AVAILABLE) {
            throw new RuntimeException("Pet is not available for adoption");
        }
        
        // Check if adopter has already submitted inquiry for this pet
        if (adoptionInquiryRepository.existsByAdopterEmailAndPetId(
            requestDto.getAdopterEmail(), requestDto.getPetId())) {
            throw new RuntimeException("You have already submitted an inquiry for this pet");
        }
        
        AdoptionInquiry inquiry = new AdoptionInquiry();
        inquiry.setPet(pet);
        inquiry.setShelter(pet.getShelter());
        inquiry.setAdopterName(requestDto.getAdopterName());
        inquiry.setAdopterEmail(requestDto.getAdopterEmail());
        inquiry.setAdopterPhone(requestDto.getAdopterPhone());
        inquiry.setAdopterAddress(requestDto.getAdopterAddress());
        inquiry.setMessage(requestDto.getMessage());
        inquiry.setLivingSituation(requestDto.getLivingSituation());
        inquiry.setPetExperience(requestDto.getPetExperience());
        inquiry.setHasYard(requestDto.getHasYard() != null ? requestDto.getHasYard() : false);
        inquiry.setHasOtherPets(requestDto.getHasOtherPets() != null ? requestDto.getHasOtherPets() : false);
        inquiry.setHasChildren(requestDto.getHasChildren() != null ? requestDto.getHasChildren() : false);
        inquiry.setStatus(AdoptionInquiry.InquiryStatus.NEW);
        
        AdoptionInquiry savedInquiry = adoptionInquiryRepository.save(inquiry);
        return convertToResponseDto(savedInquiry);
    }
    
    public AdoptionInquiryResponseDto getInquiryById(Long id) {
        AdoptionInquiry inquiry = adoptionInquiryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Adoption inquiry not found with id: " + id));
        return convertToResponseDto(inquiry);
    }
    
    public Page<AdoptionInquiryResponseDto> getInquiriesByShelterId(Long shelterId,
                                                                   Long petId,
                                                                   AdoptionInquiry.InquiryStatus status,
                                                                   String adopterName,
                                                                   Pageable pageable) {
        Page<AdoptionInquiry> inquiries = adoptionInquiryRepository.findInquiriesWithFilters(
            shelterId, petId, status, adopterName, pageable);
        
        List<AdoptionInquiryResponseDto> dtos = inquiries.getContent().stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
            
        return new PageImpl<>(dtos, pageable, inquiries.getTotalElements());
    }
    
    public List<AdoptionInquiryResponseDto> getInquiriesByPetId(Long petId) {
        List<AdoptionInquiry> inquiries = adoptionInquiryRepository.findByPetId(petId);
        return inquiries.stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
    }
    
    public List<AdoptionInquiryResponseDto> getInquiriesByStatus(AdoptionInquiry.InquiryStatus status) {
        List<AdoptionInquiry> inquiries = adoptionInquiryRepository.findByStatus(status);
        return inquiries.stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
    }
    
    public Long getInquiryCountByShelterIdAndStatus(Long shelterId, AdoptionInquiry.InquiryStatus status) {
        return adoptionInquiryRepository.countByShelterIdAndStatus(shelterId, status);
    }
    
    public AdoptionInquiryResponseDto respondToInquiry(Long id, InquiryResponseRequestDto responseDto) {
        AdoptionInquiry inquiry = adoptionInquiryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Adoption inquiry not found with id: " + id));
        
        inquiry.setStatus(responseDto.getStatus());
        inquiry.setShelterResponse(responseDto.getShelterResponse());
        inquiry.setRespondedAt(LocalDateTime.now());
        inquiry.setUpdatedAt(LocalDateTime.now());
        
        // If approved, update pet status to pending
        if (responseDto.getStatus() == AdoptionInquiry.InquiryStatus.APPROVED) {
            Pet pet = inquiry.getPet();
            pet.setAdoptionStatus(Pet.AdoptionStatus.PENDING);
            petRepository.save(pet);
        }
        
        AdoptionInquiry updatedInquiry = adoptionInquiryRepository.save(inquiry);
        return convertToResponseDto(updatedInquiry);
    }
    
    public AdoptionInquiryResponseDto updateInquiryStatus(Long id, AdoptionInquiry.InquiryStatus status) {
        AdoptionInquiry inquiry = adoptionInquiryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Adoption inquiry not found with id: " + id));
        
        AdoptionInquiry.InquiryStatus oldStatus = inquiry.getStatus();
        inquiry.setStatus(status);
        inquiry.setUpdatedAt(LocalDateTime.now());
        
        // Handle pet status updates based on inquiry status changes
        Pet pet = inquiry.getPet();
        if (status == AdoptionInquiry.InquiryStatus.APPROVED && 
            oldStatus != AdoptionInquiry.InquiryStatus.APPROVED) {
            pet.setAdoptionStatus(Pet.AdoptionStatus.PENDING);
        } else if (status == AdoptionInquiry.InquiryStatus.COMPLETED) {
            pet.setAdoptionStatus(Pet.AdoptionStatus.ADOPTED);
        } else if ((status == AdoptionInquiry.InquiryStatus.REJECTED || 
                   status == AdoptionInquiry.InquiryStatus.NEW) &&
                   oldStatus == AdoptionInquiry.InquiryStatus.APPROVED) {
            // Check if there are other approved inquiries
            boolean hasOtherApproved = adoptionInquiryRepository.findByPetId(pet.getId())
                .stream()
                .anyMatch(i -> !i.getId().equals(inquiry.getId()) && 
                         i.getStatus() == AdoptionInquiry.InquiryStatus.APPROVED);
            
            if (!hasOtherApproved) {
                pet.setAdoptionStatus(Pet.AdoptionStatus.AVAILABLE);
            }
        }
        
        petRepository.save(pet);
        AdoptionInquiry updatedInquiry = adoptionInquiryRepository.save(inquiry);
        return convertToResponseDto(updatedInquiry);
    }
    
    public void deleteInquiry(Long id) {
        AdoptionInquiry inquiry = adoptionInquiryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Adoption inquiry not found with id: " + id));
        
        // If deleting an approved inquiry, check if pet status should be updated
        if (inquiry.getStatus() == AdoptionInquiry.InquiryStatus.APPROVED) {
            Pet pet = inquiry.getPet();
            boolean hasOtherApproved = adoptionInquiryRepository.findByPetId(pet.getId())
                .stream()
                .anyMatch(i -> !i.getId().equals(inquiry.getId()) && 
                         i.getStatus() == AdoptionInquiry.InquiryStatus.APPROVED);
            
            if (!hasOtherApproved) {
                pet.setAdoptionStatus(Pet.AdoptionStatus.AVAILABLE);
                petRepository.save(pet);
            }
        }
        
        adoptionInquiryRepository.deleteById(id);
    }
    
    private AdoptionInquiryResponseDto convertToResponseDto(AdoptionInquiry inquiry) {
        AdoptionInquiryResponseDto dto = new AdoptionInquiryResponseDto();
        dto.setId(inquiry.getId());
        dto.setAdopterName(inquiry.getAdopterName());
        dto.setAdopterEmail(inquiry.getAdopterEmail());
        dto.setAdopterPhone(inquiry.getAdopterPhone());
        dto.setAdopterAddress(inquiry.getAdopterAddress());
        dto.setMessage(inquiry.getMessage());
        dto.setLivingSituation(inquiry.getLivingSituation());
        dto.setPetExperience(inquiry.getPetExperience());
        dto.setHasYard(inquiry.getHasYard());
        dto.setHasOtherPets(inquiry.getHasOtherPets());
        dto.setHasChildren(inquiry.getHasChildren());
        dto.setStatus(inquiry.getStatus());
        dto.setShelterResponse(inquiry.getShelterResponse());
        dto.setRespondedAt(inquiry.getRespondedAt());
        dto.setCreatedAt(inquiry.getCreatedAt());
        dto.setUpdatedAt(inquiry.getUpdatedAt());
        
        // Set pet and shelter info
        dto.setPet(petService.convertToBasicInfoDto(inquiry.getPet()));
        dto.setShelter(shelterService.convertToBasicInfoDto(inquiry.getShelter()));
        
        return dto;
    }
}
