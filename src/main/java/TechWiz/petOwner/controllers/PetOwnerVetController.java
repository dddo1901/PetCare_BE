package TechWiz.petOwner.controllers;

import TechWiz.auths.models.dto.VeterinarianResponse;
import TechWiz.auths.services.VeterinarianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pet-owner/vets")
@PreAuthorize("hasRole('PET_OWNER')")
@CrossOrigin(origins = "*")
public class PetOwnerVetController {

    @Autowired
    private VeterinarianService veterinarianService;

    @GetMapping
    public ResponseEntity<?> getVets() {
        try {
            List<VeterinarianResponse> vets = veterinarianService.getAllVeterinarians();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Vets retrieved successfully",
                "data", vets
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableVets() {
        try {
            List<VeterinarianResponse> vets = veterinarianService.getAvailableVeterinarians();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Available vets retrieved successfully",
                "data", vets
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<?> getVetsBySpecialization(@PathVariable String specialization) {
        try {
            List<VeterinarianResponse> vets = veterinarianService.getVeterinariansBySpecialization(specialization);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Vets by specialization retrieved successfully",
                "data", vets
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}
