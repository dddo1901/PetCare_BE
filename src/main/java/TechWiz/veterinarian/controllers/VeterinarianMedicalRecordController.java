package TechWiz.veterinarian.controllers;

import TechWiz.auths.models.User;
import TechWiz.veterinarian.dto.VeterinarianMedicalRecordRequest;
import TechWiz.veterinarian.dto.VeterinarianMedicalRecordResponse;
import TechWiz.veterinarian.services.VeterinarianMedicalRecordService;
import TechWiz.veterinarian.services.VeterinarianProfileService;
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

@RestController
@RequestMapping("/api/veterinarian/medical-records")
@PreAuthorize("hasRole('VETERINARIAN')")
@CrossOrigin(origins = "*")
public class VeterinarianMedicalRecordController {
    
    @Autowired
    private VeterinarianMedicalRecordService medicalRecordService;
    
    @Autowired
    private VeterinarianProfileService veterinarianProfileService;
    
    @PostMapping
    public ResponseEntity<?> createMedicalRecord(@Valid @RequestBody VeterinarianMedicalRecordRequest request, 
                                                Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Long vetId = veterinarianProfileService.getVetIdByUserId(user.getId());
            VeterinarianMedicalRecordResponse record = medicalRecordService.createMedicalRecord(request, vetId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Medical record created successfully",
                "data", record
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getMedicalRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) Long petId,
            @RequestParam(required = false) String type,
            Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Long vetId = veterinarianProfileService.getVetIdByUserId(user.getId());
            
            if (petId != null) {
                List<VeterinarianMedicalRecordResponse> records = medicalRecordService.getMedicalRecordsByPet(vetId, petId);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Medical records retrieved successfully",
                    "data", records
                ));
            } else if (type != null && !type.isEmpty()) {
                List<VeterinarianMedicalRecordResponse> records = medicalRecordService.getMedicalRecordsByType(vetId, type);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Medical records retrieved successfully",
                    "data", records
                ));
            } else if (size > 0) {
                Pageable pageable = PageRequest.of(page, size);
                Page<VeterinarianMedicalRecordResponse> records = medicalRecordService.getMedicalRecordsByVet(vetId, pageable);
                
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Medical records retrieved successfully",
                    "data", records.getContent(),
                    "pagination", Map.of(
                        "currentPage", records.getNumber(),
                        "totalPages", records.getTotalPages(),
                        "totalElements", records.getTotalElements(),
                        "pageSize", records.getSize()
                    )
                ));
            } else {
                List<VeterinarianMedicalRecordResponse> records = medicalRecordService.getMedicalRecordsByVet(vetId);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Medical records retrieved successfully",
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
    
    @GetMapping("/pet/{petId}")
    public ResponseEntity<?> getMedicalRecordsByPet(@PathVariable Long petId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Long vetId = veterinarianProfileService.getVetIdByUserId(user.getId());
            List<VeterinarianMedicalRecordResponse> records = medicalRecordService.getMedicalRecordsByPet(vetId, petId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Medical records retrieved successfully",
                "data", records
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<?> getMedicalRecordsByAppointment(@PathVariable Long appointmentId, Authentication authentication) {
        try {
            List<VeterinarianMedicalRecordResponse> records = medicalRecordService.getMedicalRecordsByAppointment(appointmentId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Medical records retrieved successfully",
                "data", records
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentMedicalRecords(@RequestParam(defaultValue = "7") int days, 
                                                    Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Long vetId = veterinarianProfileService.getVetIdByUserId(user.getId());
            List<VeterinarianMedicalRecordResponse> records = medicalRecordService.getRecentMedicalRecords(vetId, days);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Recent medical records retrieved successfully",
                "data", records
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> searchMedicalRecords(@RequestParam String diagnosis, 
                                                 Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Long vetId = veterinarianProfileService.getVetIdByUserId(user.getId());
            List<VeterinarianMedicalRecordResponse> records = medicalRecordService.searchMedicalRecordsByDiagnosis(vetId, diagnosis);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Medical records search completed",
                "data", records
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @PutMapping("/{recordId}")
    public ResponseEntity<?> updateMedicalRecord(@PathVariable Long recordId, 
                                                @Valid @RequestBody VeterinarianMedicalRecordRequest request, 
                                                Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Long vetId = veterinarianProfileService.getVetIdByUserId(user.getId());
            VeterinarianMedicalRecordResponse record = medicalRecordService.updateMedicalRecord(recordId, request, vetId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Medical record updated successfully",
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
    public ResponseEntity<?> deleteMedicalRecord(@PathVariable Long recordId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Long vetId = veterinarianProfileService.getVetIdByUserId(user.getId());
            boolean deleted = medicalRecordService.deleteMedicalRecord(recordId, vetId);
            
            if (deleted) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Medical record deleted successfully"
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
    public ResponseEntity<?> getMedicalRecordStatistics(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Long vetId = veterinarianProfileService.getVetIdByUserId(user.getId());
            Map<String, Object> stats = medicalRecordService.getMedicalRecordStatistics(vetId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Medical record statistics retrieved successfully",
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
