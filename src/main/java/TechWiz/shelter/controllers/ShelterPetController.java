package TechWiz.shelter.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import TechWiz.shelter.dto.PetRequestDto;
import TechWiz.shelter.dto.PetResponseDto;
import TechWiz.shelter.models.ShelterPet;
import TechWiz.shelter.services.ShelterPetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/shelter")
@Validated
@CrossOrigin(origins = "*")
public class ShelterPetController {
    
    @Autowired
    private ShelterPetService petService;
    
    @PostMapping("/{shelterId}/pets")
    public ResponseEntity<?> createPet(@PathVariable @Positive Long shelterId, // Now using shelter profile ID
                                      @Valid @RequestBody PetRequestDto requestDto) {
        try {
            PetResponseDto pet = petService.createPet(shelterId, requestDto); // shelterId is actually shelter profile ID
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Pet added successfully");
            response.put("data", pet);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @GetMapping("/pets/{id}")
    public ResponseEntity<?> getPetById(@PathVariable @Positive Long id) {
        try {
            PetResponseDto pet = petService.getPetById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", pet);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    @GetMapping("/{shelterId}/pets")
    public ResponseEntity<?> getPetsByShelterId(
            @PathVariable @Positive Long shelterId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ShelterPet.PetType type,
            @RequestParam(required = false) String breed,
            @RequestParam(required = false) ShelterPet.AdoptionStatus adoptionStatus,
            @RequestParam(required = false) ShelterPet.HealthStatus healthStatus,
            @RequestParam(required = false) ShelterPet.Gender gender,
            @RequestParam(required = false) ShelterPet.Size size,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            System.out.println("DEBUG: getPetsByShelterId called with shelterId: " + shelterId);
            
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, pageSize, sort);
            
            Page<PetResponseDto> pets = petService.getPetsByShelterId(
                shelterId, name, type, breed, adoptionStatus, healthStatus, gender, size, pageable);
            
            System.out.println("DEBUG: Found " + pets.getTotalElements() + " pets for shelterId: " + shelterId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", pets.getContent());
            response.put("currentPage", pets.getNumber());
            response.put("totalPages", pets.getTotalPages());
            response.put("totalElements", pets.getTotalElements());
            response.put("size", pets.getSize());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("DEBUG: Error getting pets for shelterId " + shelterId + ": " + e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/{shelterId}/pets/available")
    public ResponseEntity<?> getAvailablePetsByShelterId(@PathVariable @Positive Long shelterId) {
        try {
            List<PetResponseDto> pets = petService.getAvailablePetsByShelterId(shelterId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", pets);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/pets/available")
    public ResponseEntity<?> getAvailablePetsForAdoption(
            @RequestParam(required = false) ShelterPet.PetType type,
            @RequestParam(required = false) String breed,
            @RequestParam(required = false) ShelterPet.Gender gender,
            @RequestParam(required = false) ShelterPet.Size size,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, pageSize, sort);
            
            Page<PetResponseDto> pets = petService.getAvailablePetsForAdoption(
                type, breed, gender, size, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", pets.getContent());
            response.put("currentPage", pets.getNumber());
            response.put("totalPages", pets.getTotalPages());
            response.put("totalElements", pets.getTotalElements());
            response.put("size", pets.getSize());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping("/pets/{id}")
    public ResponseEntity<?> updatePet(@PathVariable @Positive Long id,
                                      @Valid @RequestBody PetRequestDto requestDto) {
        try {
            PetResponseDto pet = petService.updatePet(id, requestDto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Pet updated successfully");
            response.put("data", pet);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @PutMapping("/pets/{id}/adoption-status")
    public ResponseEntity<?> updatePetAdoptionStatus(@PathVariable @Positive Long id,
                                                    @RequestParam ShelterPet.AdoptionStatus adoptionStatus) {
        try {
            PetResponseDto pet = petService.updatePetAdoptionStatus(id, adoptionStatus);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Pet adoption status updated successfully");
            response.put("data", pet);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @DeleteMapping("/pets/{id}")
    public ResponseEntity<?> deletePet(@PathVariable @Positive Long id) {
        try {
            petService.deletePet(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Pet deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @GetMapping("/{shelterId}/pets/stats")
    public ResponseEntity<?> getPetStatsByShelterId(@PathVariable @Positive Long shelterId) {
        try {
            Map<String, Long> stats = new HashMap<>();
            stats.put("total", petService.getTotalPetsByShelterProfile(shelterId));
            stats.put("available", petService.getPetCountByStatus(shelterId, ShelterPet.AdoptionStatus.AVAILABLE));
            stats.put("pending", petService.getPetCountByStatus(shelterId, ShelterPet.AdoptionStatus.PENDING));
            stats.put("adopted", petService.getPetCountByStatus(shelterId, ShelterPet.AdoptionStatus.ADOPTED));
            stats.put("totalViews", petService.getTotalViewsByShelterProfile(shelterId));
            stats.put("totalApplications", petService.getTotalApplicationsByShelterProfile(shelterId));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/pets/{id}/view")
    public ResponseEntity<?> incrementPetViews(@PathVariable @Positive Long id) {
        try {
            petService.incrementPetViews(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "View count updated");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
