package TechWiz.veterinarian.controllers;

import TechWiz.veterinarian.services.VeterinarianHealthRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/veterinarian/health-records")
@CrossOrigin(origins = "http://localhost:3000")
public class VeterinarianHealthRecordController {

    @Autowired
    private VeterinarianHealthRecordService healthRecordService;

    @GetMapping("/pet/{petId}")
    public ResponseEntity<?> getHealthRecordsByPet(@PathVariable Long petId, Pageable pageable) {
        try {
            Page<Map<String, Object>> records = healthRecordService.getHealthRecordsByPet(petId, pageable);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", records,
                "message", "Health records retrieved successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to retrieve health records: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/pet/{petId}/statistics")
    public ResponseEntity<?> getHealthRecordStatistics(@PathVariable Long petId) {
        try {
            Map<String, Object> statistics = healthRecordService.getHealthRecordStatistics(petId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", statistics,
                "message", "Statistics retrieved successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to retrieve statistics: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/pet/{petId}/info")
    public ResponseEntity<?> getPetInfo(@PathVariable Long petId) {
        try {
            Map<String, Object> petInfo = healthRecordService.getPetInfo(petId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", petInfo,
                "message", "Pet information retrieved successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to retrieve pet information: " + e.getMessage()
            ));
        }
    }
}
