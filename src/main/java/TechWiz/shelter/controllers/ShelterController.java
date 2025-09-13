package TechWiz.shelter.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import TechWiz.auths.models.ShelterProfile;
import TechWiz.auths.repositories.ShelterProfileRepository;
import TechWiz.auths.services.ProfileService;
import TechWiz.shelter.dto.ShelterRegistrationRequestDto;
import TechWiz.shelter.dto.ShelterResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/shelter")
@Validated
@CrossOrigin(origins = "*")
public class ShelterController {
    
    @Autowired
    private ProfileService profileService;
    
    @Autowired
    private ShelterProfileRepository shelterProfileRepository;
    
    @PostMapping("/register")
    public ResponseEntity<?> registerShelter(@Valid @RequestBody ShelterRegistrationRequestDto requestDto) {
        try {
            // Note: The shelter profile is now created via ProfileService.updateShelterProfile
            // This endpoint may need to be refactored to work with the new architecture
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Shelter registration has been moved to profile management. Please use the profile endpoints.");
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getShelterById(@PathVariable @Positive Long id) {
        try {
            // Get shelter profile by ID
            Optional<ShelterProfile> shelterProfileOpt = shelterProfileRepository.findById(id);
            if (shelterProfileOpt.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Shelter profile not found with id: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            ShelterProfile shelterProfile = shelterProfileOpt.get();
            ShelterResponseDto shelterDto = convertToShelterResponseDto(shelterProfile);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", shelterDto);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getShelterByEmail(@PathVariable String email) {
        try {
            // Get shelter profile by email using ProfileService
            var shelterProfileData = profileService.getShelterProfile(email);
            
            // Convert to ShelterResponseDto format
            ShelterResponseDto shelterDto = new ShelterResponseDto();
            shelterDto.setId(shelterProfileData.getProfileId());
            shelterDto.setShelterName(shelterProfileData.getShelterName());
            shelterDto.setContactPersonName(shelterProfileData.getContactPersonName());
            shelterDto.setEmail(email);
            shelterDto.setAddress(shelterProfileData.getAddress());
            shelterDto.setDescription(shelterProfileData.getDescription());
            shelterDto.setWebsite(shelterProfileData.getWebsite());
            shelterDto.setImageUrl(shelterProfileData.getProfileImageUrl());
            shelterDto.setStatus("ACTIVE");
            shelterDto.setIsVerified(shelterProfileData.getIsVerified());
            shelterDto.setCreatedAt(shelterProfileData.getCreatedAt());
            shelterDto.setUpdatedAt(shelterProfileData.getUpdatedAt());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", shelterDto);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    @GetMapping("/verified")
    public ResponseEntity<?> getVerifiedShelters() {
        try {
            // Get verified shelter profiles
            List<ShelterProfile> shelterProfiles = shelterProfileRepository.findByIsVerifiedTrue();
            
            // Convert to ShelterResponseDto list
            List<ShelterResponseDto> shelters = shelterProfiles.stream()
                    .map(this::convertToShelterResponseDto)
                    .collect(java.util.stream.Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", shelters);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllShelters(
            @RequestParam(required = false) String shelterName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean isVerified,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            // For now, return all shelter profiles with basic filtering
            Page<ShelterProfile> shelterProfilePage = shelterProfileRepository.findAll(pageable);
            
            // Convert to ShelterResponseDto
            List<ShelterResponseDto> shelters = shelterProfilePage.getContent().stream()
                    .map(this::convertToShelterResponseDto)
                    .collect(java.util.stream.Collectors.toList());
                    
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", shelters);
            response.put("currentPage", shelterProfilePage.getNumber());
            response.put("totalPages", shelterProfilePage.getTotalPages());
            response.put("totalElements", shelterProfilePage.getTotalElements());
            response.put("size", shelterProfilePage.getSize());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateShelter(@PathVariable @Positive Long id,
                                          @Valid @RequestBody ShelterRegistrationRequestDto requestDto) {
        try {
            // Note: This method should use ProfileService.updateShelterProfile
            // For now, returning a message about the refactor needed
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Shelter update has been moved to profile management. Please use the profile endpoints.");
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateShelterStatus(@PathVariable @Positive Long id,
                                                @RequestParam String status) {
        try {
            // Note: Status management is no longer applicable with ShelterProfile
            // ShelterProfile doesn't have a status field like the old Shelter entity
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Status update is not applicable for shelter profiles. Status is managed through user account status.");
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @PutMapping("/{id}/verify")
    public ResponseEntity<?> verifyShelter(@PathVariable @Positive Long id,
                                          @RequestParam boolean verified) {
        try {
            // Update shelter profile verification status
            Optional<ShelterProfile> shelterProfileOpt = shelterProfileRepository.findById(id);
            if (shelterProfileOpt.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Shelter profile not found with id: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            ShelterProfile shelterProfile = shelterProfileOpt.get();
            shelterProfile.setIsVerified(verified);
            shelterProfileRepository.save(shelterProfile);
            
            ShelterResponseDto shelterDto = convertToShelterResponseDto(shelterProfile);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", verified ? "Shelter verified successfully" : "Shelter verification removed");
            response.put("data", shelterDto);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShelter(@PathVariable @Positive Long id) {
        try {
            // Delete shelter profile
            Optional<ShelterProfile> shelterProfileOpt = shelterProfileRepository.findById(id);
            if (shelterProfileOpt.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Shelter profile not found with id: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            shelterProfileRepository.deleteById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Shelter profile deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    // Helper method to convert ShelterProfile to ShelterResponseDto
    private ShelterResponseDto convertToShelterResponseDto(ShelterProfile shelterProfile) {
        ShelterResponseDto dto = new ShelterResponseDto();
        dto.setId(shelterProfile.getId());
        dto.setShelterName(shelterProfile.getShelterName());
        dto.setContactPersonName(shelterProfile.getContactPersonName());
        dto.setEmail(shelterProfile.getUser().getEmail());
        dto.setPhoneNumber(shelterProfile.getUser().getPhoneNumber());
        dto.setAddress(shelterProfile.getAddress());
        dto.setDescription(shelterProfile.getDescription());
        dto.setWebsite(shelterProfile.getWebsite());
        dto.setImageUrl(shelterProfile.getProfileImageUrl());
        dto.setStatus("ACTIVE"); // Default status as string
        dto.setIsVerified(shelterProfile.getIsVerified());
        dto.setCreatedAt(shelterProfile.getCreatedAt());
        dto.setUpdatedAt(shelterProfile.getUpdatedAt());
        
        // TODO: Set statistics if needed
        dto.setTotalPets(0L);
        dto.setAvailablePets(0L);
        dto.setPendingInquiries(0L);
        
        return dto;
    }
}
