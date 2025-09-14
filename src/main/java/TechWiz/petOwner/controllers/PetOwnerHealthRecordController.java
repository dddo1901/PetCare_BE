package TechWiz.petOwner.controllers;

import TechWiz.auths.models.User;
import TechWiz.petOwner.dto.PetOwnerHealthRecordRequest;
import TechWiz.petOwner.models.PetOwnerHealthRecord;
import TechWiz.petOwner.services.PetOwnerHealthRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/pet-owner/health-records")
@PreAuthorize("hasRole('PET_OWNER')")
@CrossOrigin(origins = "*")
public class PetOwnerHealthRecordController {

    @Autowired
    private PetOwnerHealthRecordService healthRecordService;

    @GetMapping("/pet/{petId}/combined")
    public ResponseEntity<?> getHealthRecordsByPetId(@PathVariable Long petId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Map<String, Object> healthData = healthRecordService.getCombinedHealthData(petId, user.getId());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Health records retrieved successfully",
                "data", healthData
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/pet/{petId}")
    public ResponseEntity<?> getHealthRecordsByPet(
            @PathVariable Long petId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Authentication authentication) {
        try {
            // For testing, use a default owner ID if authentication is not available
            Long ownerId = 3L; // Default owner ID for testing
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                ownerId = user.getId();
            }
            
            if (size > 0) {
                Pageable pageable = PageRequest.of(page, size);
                Page<PetOwnerHealthRecord> records = healthRecordService.getHealthRecordsByPet(petId, ownerId, pageable);
                
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Health records retrieved successfully",
                    "data", Map.of(
                        "content", records.getContent(),
                        "number", records.getNumber(),
                        "totalPages", records.getTotalPages(),
                        "totalElements", records.getTotalElements(),
                        "size", records.getSize()
                    )
                ));
            } else {
                List<PetOwnerHealthRecord> records = healthRecordService.getHealthRecordsByPet(petId, ownerId);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Health records retrieved successfully",
                    "data", records
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping
    public ResponseEntity<?> getHealthRecordsByOwner(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            
            if (size > 0) {
                Pageable pageable = PageRequest.of(page, size);
                Page<PetOwnerHealthRecord> records = healthRecordService.getHealthRecordsByOwner(user.getId(), pageable);
                
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Health records retrieved successfully",
                    "data", Map.of(
                        "content", records.getContent(),
                        "number", records.getNumber(),
                        "totalPages", records.getTotalPages(),
                        "totalElements", records.getTotalElements(),
                        "size", records.getSize()
                    )
                ));
            } else {
                List<PetOwnerHealthRecord> records = healthRecordService.getHealthRecordsByOwner(user.getId());
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Health records retrieved successfully",
                    "data", records
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<?> getHealthRecordsByType(
            @PathVariable String type,
            @RequestParam Long petId,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            List<PetOwnerHealthRecord> records = healthRecordService.getHealthRecordsByType(petId, type);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Health records retrieved successfully",
                "data", records
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping
    public ResponseEntity<?> createHealthRecord(@Valid @RequestBody PetOwnerHealthRecordRequest request, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            PetOwnerHealthRecord record = healthRecordService.createHealthRecord(request, user.getId());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Health record created successfully",
                "data", record
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/{recordId}")
    public ResponseEntity<?> updateHealthRecord(@PathVariable Long recordId, 
                                               @Valid @RequestBody PetOwnerHealthRecordRequest request, 
                                               Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            PetOwnerHealthRecord record = healthRecordService.updateHealthRecord(recordId, request, user.getId());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Health record updated successfully",
                "data", record
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<?> deleteHealthRecord(@PathVariable Long recordId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            boolean deleted = healthRecordService.deleteHealthRecord(recordId, user.getId());
            
            if (deleted) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Health record deleted successfully"
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> getHealthRecordStatistics(
            @RequestParam(required = false) Long petId,
            Authentication authentication) {
        try {
            // For testing, use a default owner ID if authentication is not available
            Long ownerId = 3L; // Default owner ID for testing
            if (authentication != null && authentication.getPrincipal() instanceof User) {
                User user = (User) authentication.getPrincipal();
                ownerId = user.getId();
            }
            
            Map<String, Object> stats;
            if (petId != null) {
                stats = healthRecordService.getHealthRecordStatistics(petId, ownerId);
            } else {
                stats = Map.of(
                    "totalRecords", healthRecordService.getHealthRecordCountByOwner(ownerId)
                );
            }
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Health record statistics retrieved successfully",
                "data", stats
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}
