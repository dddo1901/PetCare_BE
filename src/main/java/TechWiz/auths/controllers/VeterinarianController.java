package TechWiz.auths.controllers;

import TechWiz.auths.models.dto.VeterinarianResponse;
import TechWiz.auths.services.VeterinarianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/veterinarians")
@CrossOrigin(origins = "*")
public class VeterinarianController {

    @Autowired
    private VeterinarianService veterinarianService;

    @GetMapping
    public ResponseEntity<?> getAllVeterinarians() {
        try {
            System.out.println("=== VETERINARIAN CONTROLLER: GET ALL ===");
            List<VeterinarianResponse> vets = veterinarianService.getAllVeterinarians();
            System.out.println("Controller returning " + vets.size() + " veterinarians");
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Veterinarians retrieved successfully",
                "data", vets
            ));
        } catch (Exception e) {
            System.err.println("Error in VeterinarianController: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableVeterinarians() {
        try {
            List<VeterinarianResponse> vets = veterinarianService.getAvailableVeterinarians();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Available veterinarians retrieved successfully",
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
    public ResponseEntity<?> getVeterinariansBySpecialization(@PathVariable String specialization) {
        try {
            List<VeterinarianResponse> vets = veterinarianService.getVeterinariansBySpecialization(specialization);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Veterinarians by specialization retrieved successfully",
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
