package TechWiz.petOwner.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
import TechWiz.petOwner.dto.CreateMedicalRecordRequest;
import TechWiz.petOwner.models.MedicalRecord;
import TechWiz.petOwner.services.HealthRecordService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pet-owner/health-records")
@PreAuthorize("hasRole('PET_OWNER')")
@CrossOrigin(origins = "*")
public class HealthRecordController {

    @Autowired
    private HealthRecordService healthRecordService;

    // Create new medical record
    @PostMapping
    public ResponseEntity<?> createMedicalRecord(@Valid @RequestBody CreateMedicalRecordRequest request, 
                                               Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            MedicalRecord record = healthRecordService.createMedicalRecord(request, user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Medical record created successfully",
                "data", record
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get all medical records by owner
    @GetMapping
    public ResponseEntity<?> getMedicalRecords(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            List<MedicalRecord> records = healthRecordService.getMedicalRecordsByOwner(user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Medical records retrieved successfully",
                "data", records
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get medical records by pet
    @GetMapping("/pet/{petId}")
    public ResponseEntity<?> getMedicalRecordsByPet(
            @PathVariable Long petId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            
            if (size > 0) {
                Pageable pageable = PageRequest.of(page, size);
                Page<MedicalRecord> records = healthRecordService.getMedicalRecordsByPet(petId, user.getId(), pageable);
                
                return ResponseEntity.ok().body(java.util.Map.of(
                    "success", true,
                    "message", "Medical records retrieved successfully",
                    "data", records.getContent(),
                    "pagination", java.util.Map.of(
                        "currentPage", records.getNumber(),
                        "totalPages", records.getTotalPages(),
                        "totalElements", records.getTotalElements(),
                        "pageSize", records.getSize()
                    )
                ));
            } else {
                List<MedicalRecord> records = healthRecordService.getMedicalRecordsByPet(petId, user.getId());
                return ResponseEntity.ok().body(java.util.Map.of(
                    "success", true,
                    "message", "Medical records retrieved successfully",
                    "data", records
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get medical record by ID
    @GetMapping("/{recordId}")
    public ResponseEntity<?> getMedicalRecordById(@PathVariable Long recordId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Optional<MedicalRecord> record = healthRecordService.getMedicalRecordById(recordId, user.getId());
            
            if (record.isPresent()) {
                return ResponseEntity.ok().body(java.util.Map.of(
                    "success", true,
                    "message", "Medical record retrieved successfully",
                    "data", record.get()
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

    // Update medical record
    @PutMapping("/{recordId}")
    public ResponseEntity<?> updateMedicalRecord(@PathVariable Long recordId,
                                               @Valid @RequestBody CreateMedicalRecordRequest request,
                                               Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            MedicalRecord record = healthRecordService.updateMedicalRecord(recordId, request, user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Medical record updated successfully",
                "data", record
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Delete medical record
    @DeleteMapping("/{recordId}")
    public ResponseEntity<?> deleteMedicalRecord(@PathVariable Long recordId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            boolean deleted = healthRecordService.deleteMedicalRecord(recordId, user.getId());
            
            if (deleted) {
                return ResponseEntity.ok().body(java.util.Map.of(
                    "success", true,
                    "message", "Medical record deleted successfully"
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

    // Get medical records by type
    @GetMapping("/pet/{petId}/type/{recordType}")
    public ResponseEntity<?> getMedicalRecordsByType(@PathVariable Long petId, 
                                                   @PathVariable String recordType,
                                                   Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            List<MedicalRecord> records = healthRecordService.getMedicalRecordsByType(petId, recordType, user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Medical records retrieved successfully",
                "data", records
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get recent medical records
    @GetMapping("/pet/{petId}/recent")
    public ResponseEntity<?> getRecentMedicalRecords(@PathVariable Long petId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            List<MedicalRecord> records = healthRecordService.getRecentMedicalRecords(petId, user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Recent medical records retrieved successfully",
                "data", records
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Search medical records
    @GetMapping("/pet/{petId}/search")
    public ResponseEntity<?> searchMedicalRecords(@PathVariable Long petId, 
                                                @RequestParam String keyword,
                                                Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            List<MedicalRecord> records = healthRecordService.searchMedicalRecords(petId, keyword, user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Search completed successfully",
                "data", records
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get medical records by date range
    @GetMapping("/pet/{petId}/date-range")
    public ResponseEntity<?> getMedicalRecordsByDateRange(
            @PathVariable Long petId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            List<MedicalRecord> records = healthRecordService.getMedicalRecordsByDateRange(petId, start, end, user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Medical records retrieved successfully",
                "data", records
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get records requiring follow-up
    @GetMapping("/follow-up")
    public ResponseEntity<?> getRecordsRequiringFollowUp(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            List<MedicalRecord> records = healthRecordService.getRecordsRequiringFollowUp(user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Records requiring follow-up retrieved successfully",
                "data", records
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get health summary for pet
    @GetMapping("/pet/{petId}/summary")
    public ResponseEntity<?> getHealthSummary(@PathVariable Long petId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Map<String, Object> summary = healthRecordService.getHealthSummary(petId, user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Health summary retrieved successfully",
                "data", summary
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Get medical record count by pet
    @GetMapping("/pet/{petId}/count")
    public ResponseEntity<?> getMedicalRecordCount(@PathVariable Long petId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            long count = healthRecordService.getMedicalRecordCountByPet(petId, user.getId());
            
            return ResponseEntity.ok().body(java.util.Map.of(
                "success", true,
                "message", "Record count retrieved successfully",
                "data", java.util.Map.of("count", count)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}
