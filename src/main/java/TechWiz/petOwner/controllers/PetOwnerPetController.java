package TechWiz.petOwner.controllers;

import TechWiz.auths.models.User;
import TechWiz.petOwner.dto.PetOwnerPetRequest;
import TechWiz.petOwner.dto.PetOwnerPetResponse;
import TechWiz.petOwner.services.PetOwnerPetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestController
@RequestMapping("/api/pet-owner/pets")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('PET_OWNER')")
public class PetOwnerPetController {

    @Autowired
    private PetOwnerPetService petService;

    @PostMapping
    public ResponseEntity<?> createPet(@Valid @RequestBody PetOwnerPetRequest request, Authentication authentication) {
        try {
            System.out.println("=== CREATE PET REQUEST RECEIVED ===");
            System.out.println("Request: " + request);
            
            User user = (User) authentication.getPrincipal();
            System.out.println("User: " + user.getEmail() + " (ID: " + user.getId() + ")");
            
            PetOwnerPetResponse pet = petService.createPet(request, user.getId());
            System.out.println("Pet created successfully with ID: " + pet.getId());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Pet created successfully",
                "data", pet
            ));
        } catch (Exception e) {
            System.out.println("=== ERROR IN CREATE PET ===");
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<?> getPets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Pageable pageable = PageRequest.of(page, size);
            Page<PetOwnerPetResponse> pets = petService.getPetsByOwner(user.getId(), pageable);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Pets retrieved successfully",
                "data", pets
            ));
        } catch (Exception e) {
            System.out.println("=== ERROR IN GET PETS ===");
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPetById(@PathVariable Long id, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Optional<PetOwnerPetResponse> pet = petService.getPetById(id, user.getId());
            
            if (pet.isPresent()) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Pet retrieved successfully",
                    "data", pet.get()
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.out.println("=== ERROR IN GET PET BY ID ===");
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePet(@PathVariable Long id, 
                                     @Valid @RequestBody PetOwnerPetRequest request, 
                                     Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            PetOwnerPetResponse pet = petService.updatePet(id, request, user.getId());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Pet updated successfully",
                "data", pet
            ));
        } catch (Exception e) {
            System.out.println("=== ERROR IN UPDATE PET ===");
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePet(@PathVariable Long id, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            petService.deletePet(id, user.getId());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Pet deleted successfully"
            ));
        } catch (Exception e) {
            System.out.println("=== ERROR IN DELETE PET ===");
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new java.util.HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        System.out.println("=== VALIDATION ERRORS ===");
        errors.forEach((field, message) -> {
            System.out.println(field + ": " + message);
        });
        
        return ResponseEntity.badRequest().body(Map.of(
            "success", false,
            "message", "Validation failed",
            "errors", errors
        ));
    }
}
