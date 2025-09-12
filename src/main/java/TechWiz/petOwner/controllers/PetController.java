package TechWiz.petOwner.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import TechWiz.auths.models.User;
import TechWiz.petOwner.dto.CreatePetRequest;
import TechWiz.petOwner.models.Pet;
import TechWiz.petOwner.services.PetService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pet-owner/pets")
@PreAuthorize("hasRole('PET_OWNER')")
@CrossOrigin(origins = "*")
public class PetController {

    @Autowired
    private PetService petService;

    // Create new pet
    @PostMapping
    public ResponseEntity<?> createPet(@Valid @RequestBody CreatePetRequest request, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Pet pet = petService.createPet(request, user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Pet created successfully",
                "data", pet
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get all pets by owner
    @GetMapping
    public ResponseEntity<?> getPets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            
            if (size > 0) {
                Pageable pageable = PageRequest.of(page, size);
                Page<Pet> pets = petService.getPetsByOwner(user.getId(), pageable);
                
                return ResponseEntity.ok().body(java.util.Map.of(
                    "success", true,
                    "message", "Pets retrieved successfully",
                    "data", pets.getContent(),
                    "pagination", java.util.Map.of(
                        "currentPage", pets.getNumber(),
                        "totalPages", pets.getTotalPages(),
                        "totalElements", pets.getTotalElements(),
                        "pageSize", pets.getSize()
                    )
                ));
            } else {
                List<Pet> pets = petService.getPetsByOwner(user.getId());
                return ResponseEntity.ok().body(java.util.Map.of(
                    "success", true,
                    "message", "Pets retrieved successfully",
                    "data", pets
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get pet by ID
    @GetMapping("/{petId}")
    public ResponseEntity<?> getPetById(@PathVariable Long petId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Optional<Pet> pet = petService.getPetById(petId, user.getId());
            
            if (pet.isPresent()) {
                return ResponseEntity.ok().body(java.util.Map.of(
                    "success", true,
                    "message", "Pet retrieved successfully",
                    "data", pet.get()
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Update pet
    @PutMapping("/{petId}")
    public ResponseEntity<?> updatePet(@PathVariable Long petId, 
                                      @Valid @RequestBody CreatePetRequest request, 
                                      Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Pet pet = petService.updatePet(petId, request, user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Pet updated successfully",
                "data", pet
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Delete pet
    @DeleteMapping("/{petId}")
    public ResponseEntity<?> deletePet(@PathVariable Long petId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            boolean deleted = petService.deletePet(petId, user.getId());
            
            if (deleted) {
                return ResponseEntity.ok().body(java.util.Map.of(
                    "success", true,
                    "message", "Pet deleted successfully"
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Search pets
    @GetMapping("/search")
    public ResponseEntity<?> searchPets(@RequestParam String keyword, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            List<Pet> pets = petService.searchPets(user.getId(), keyword);
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Search completed successfully",
                "data", pets
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get pets by type
    @GetMapping("/type/{type}")
    public ResponseEntity<?> getPetsByType(@PathVariable String type, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            List<Pet> pets = petService.getPetsByType(user.getId(), type);
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Pets retrieved successfully",
                "data", pets
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get pets by health status
    @GetMapping("/health-status/{status}")
    public ResponseEntity<?> getPetsByHealthStatus(@PathVariable String status, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            List<Pet> pets = petService.getPetsByHealthStatus(user.getId(), status);
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Pets retrieved successfully",
                "data", pets
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get pets needing vaccination
    @GetMapping("/needing-vaccination")
    public ResponseEntity<?> getPetsNeedingVaccination(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            List<Pet> pets = petService.getPetsNeedingVaccination(user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Pets needing vaccination retrieved successfully",
                "data", pets
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get pets needing checkup
    @GetMapping("/needing-checkup")
    public ResponseEntity<?> getPetsNeedingCheckup(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            List<Pet> pets = petService.getPetsNeedingCheckup(user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Pets needing checkup retrieved successfully",
                "data", pets
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Update pet health status
    @PatchMapping("/{petId}/health-status")
    public ResponseEntity<?> updateHealthStatus(@PathVariable Long petId, 
                                               @RequestParam String status,
                                               Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Pet pet = petService.updateHealthStatus(petId, status, user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Pet health status updated successfully",
                "data", pet
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Update last checkup
    @PatchMapping("/{petId}/last-checkup")
    public ResponseEntity<?> updateLastCheckup(@PathVariable Long petId, 
                                              @RequestParam String date,
                                              Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            LocalDate checkupDate = LocalDate.parse(date);
            Pet pet = petService.updateLastCheckup(petId, checkupDate, user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Last checkup date updated successfully",
                "data", pet
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Add photo
    @PostMapping("/{petId}/photos")
    public ResponseEntity<?> addPhoto(@PathVariable Long petId, 
                                     @RequestParam String photoUrl,
                                     Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Pet pet = petService.addPhoto(petId, photoUrl, user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Photo added successfully",
                "data", pet
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Remove photo
    @DeleteMapping("/{petId}/photos")
    public ResponseEntity<?> removePhoto(@PathVariable Long petId, 
                                        @RequestParam String photoUrl,
                                        Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Pet pet = petService.removePhoto(petId, photoUrl, user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Photo removed successfully",
                "data", pet
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get pet statistics
    @GetMapping("/statistics")
    public ResponseEntity<?> getPetStatistics(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            
            java.util.Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("totalPets", petService.getPetCountByOwner(user.getId()));
            stats.put("dogs", petService.getPetCountByOwnerAndType(user.getId(), "DOG"));
            stats.put("cats", petService.getPetCountByOwnerAndType(user.getId(), "CAT"));
            stats.put("petsNeedingVaccination", petService.getPetsNeedingVaccination(user.getId()).size());
            stats.put("petsNeedingCheckup", petService.getPetsNeedingCheckup(user.getId()).size());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Pet statistics retrieved successfully",
                "data", stats
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}
