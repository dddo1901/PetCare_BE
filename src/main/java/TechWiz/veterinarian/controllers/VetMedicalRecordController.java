package TechWiz.veterinarian.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import TechWiz.auths.models.User;
import TechWiz.auths.repositories.UserRepository;
import TechWiz.veterinarian.dto.MedicalRecordRequest;
import TechWiz.veterinarian.dto.MedicalRecordResponse;
import TechWiz.veterinarian.services.VetMedicalRecordService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/veterinarian/medical-records")
public class VetMedicalRecordController {
    
    @Autowired
    private VetMedicalRecordService medicalRecordService;
    
    @Autowired
    private UserRepository userRepository;
    
    // Create medical record
    @PostMapping
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> createMedicalRecord(@Valid @RequestBody MedicalRecordRequest request) {
        try {
            // Verify the veterinarian ID matches current user
            Long currentVetId = getCurrentVeterinarianId();
            if (!currentVetId.equals(request.getVeterinarianId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse("error", "Access denied", null));
            }
            
            MedicalRecordResponse record = medicalRecordService.createMedicalRecord(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse("success", "Medical record created successfully", record));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to create medical record: " + e.getMessage(), null));
        }
    }
    
    // Update medical record
    @PutMapping("/{recordId}")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> updateMedicalRecord(@PathVariable Long recordId, @Valid @RequestBody MedicalRecordRequest request) {
        try {
            // Verify the veterinarian ID matches current user
            Long currentVetId = getCurrentVeterinarianId();
            if (!currentVetId.equals(request.getVeterinarianId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse("error", "Access denied", null));
            }
            
            Optional<MedicalRecordResponse> record = medicalRecordService.updateMedicalRecord(recordId, request);
            if (record.isPresent()) {
                return ResponseEntity.ok(new ApiResponse("success", "Medical record updated successfully", record.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to update medical record: " + e.getMessage(), null));
        }
    }
    
    // Get medical record by ID
    @GetMapping("/{recordId}")
    @PreAuthorize("hasRole('VETERINARIAN') or hasRole('PET_OWNER')")
    public ResponseEntity<?> getMedicalRecordById(@PathVariable Long recordId) {
        try {
            Optional<MedicalRecordResponse> record = medicalRecordService.getMedicalRecordById(recordId);
            if (record.isPresent()) {
                return ResponseEntity.ok(new ApiResponse("success", "Medical record retrieved successfully", record.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve medical record: " + e.getMessage(), null));
        }
    }
    
    // Get medical record by appointment ID
    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasRole('VETERINARIAN') or hasRole('PET_OWNER')")
    public ResponseEntity<?> getMedicalRecordByAppointmentId(@PathVariable Long appointmentId) {
        try {
            Optional<MedicalRecordResponse> record = medicalRecordService.getMedicalRecordByAppointmentId(appointmentId);
            if (record.isPresent()) {
                return ResponseEntity.ok(new ApiResponse("success", "Medical record retrieved successfully", record.get()));
            } else {
                return ResponseEntity.ok(new ApiResponse("info", "No medical record found for this appointment", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve medical record: " + e.getMessage(), null));
        }
    }
    
    // Get current veterinarian's medical records
    @GetMapping("/me")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> getMyMedicalRecords() {
        try {
            Long veterinarianId = getCurrentVeterinarianId();
            List<MedicalRecordResponse> records = medicalRecordService.getMedicalRecordsByVeterinarian(veterinarianId);
            return ResponseEntity.ok(new ApiResponse("success", "Medical records retrieved successfully", records));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve medical records: " + e.getMessage(), null));
        }
    }
    
    // Get medical records by pet ID
    @GetMapping("/pet/{petId}")
    @PreAuthorize("hasRole('VETERINARIAN') or hasRole('PET_OWNER')")
    public ResponseEntity<?> getMedicalRecordsByPet(@PathVariable Long petId) {
        try {
            List<MedicalRecordResponse> records = medicalRecordService.getMedicalRecordsByPet(petId);
            return ResponseEntity.ok(new ApiResponse("success", "Pet medical records retrieved successfully", records));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve medical records: " + e.getMessage(), null));
        }
    }
    
    // Get recent medical records for pet
    @GetMapping("/pet/{petId}/recent")
    @PreAuthorize("hasRole('VETERINARIAN') or hasRole('PET_OWNER')")
    public ResponseEntity<?> getRecentMedicalRecordsByPet(@PathVariable Long petId) {
        try {
            List<MedicalRecordResponse> records = medicalRecordService.getRecentMedicalRecordsByPet(petId);
            return ResponseEntity.ok(new ApiResponse("success", "Recent medical records retrieved successfully", records));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve medical records: " + e.getMessage(), null));
        }
    }
    
    // Get medical records by pet owner
    @GetMapping("/owner/{petOwnerId}")
    @PreAuthorize("hasRole('VETERINARIAN') or hasRole('PET_OWNER')")
    public ResponseEntity<?> getMedicalRecordsByPetOwner(@PathVariable Long petOwnerId) {
        try {
            List<MedicalRecordResponse> records = medicalRecordService.getMedicalRecordsByPetOwner(petOwnerId);
            return ResponseEntity.ok(new ApiResponse("success", "Pet owner medical records retrieved successfully", records));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve medical records: " + e.getMessage(), null));
        }
    }
    
    // Get medical records by date range for current veterinarian
    @GetMapping("/me/date-range")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> getMyMedicalRecordsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            Long veterinarianId = getCurrentVeterinarianId();
            List<MedicalRecordResponse> records = medicalRecordService.getMedicalRecordsByDateRange(veterinarianId, startDate, endDate);
            return ResponseEntity.ok(new ApiResponse("success", "Medical records retrieved successfully", records));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve medical records: " + e.getMessage(), null));
        }
    }
    
    // Get medical records by pet and date range
    @GetMapping("/pet/{petId}/date-range")
    @PreAuthorize("hasRole('VETERINARIAN') or hasRole('PET_OWNER')")
    public ResponseEntity<?> getMedicalRecordsByPetAndDateRange(
            @PathVariable Long petId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<MedicalRecordResponse> records = medicalRecordService.getMedicalRecordsByPetAndDateRange(petId, startDate, endDate);
            return ResponseEntity.ok(new ApiResponse("success", "Medical records retrieved successfully", records));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve medical records: " + e.getMessage(), null));
        }
    }
    
    // Search medical records
    @GetMapping("/search")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> searchMedicalRecords(@RequestParam String keyword) {
        try {
            List<MedicalRecordResponse> records = medicalRecordService.searchMedicalRecords(keyword);
            return ResponseEntity.ok(new ApiResponse("success", "Search completed successfully", records));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Search failed: " + e.getMessage(), null));
        }
    }
    
    // Get records by diagnosis
    @GetMapping("/diagnosis/{diagnosis}")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> getMedicalRecordsByDiagnosis(@PathVariable String diagnosis) {
        try {
            List<MedicalRecordResponse> records = medicalRecordService.getMedicalRecordsByDiagnosis(diagnosis);
            return ResponseEntity.ok(new ApiResponse("success", "Medical records retrieved successfully", records));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve medical records: " + e.getMessage(), null));
        }
    }
    
    // Get records by symptoms
    @GetMapping("/symptoms/{symptom}")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> getMedicalRecordsBySymptoms(@PathVariable String symptom) {
        try {
            List<MedicalRecordResponse> records = medicalRecordService.getMedicalRecordsBySymptoms(symptom);
            return ResponseEntity.ok(new ApiResponse("success", "Medical records retrieved successfully", records));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve medical records: " + e.getMessage(), null));
        }
    }
    
    // Get records by medication
    @GetMapping("/medication/{medication}")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> getMedicalRecordsByMedication(@PathVariable String medication) {
        try {
            List<MedicalRecordResponse> records = medicalRecordService.getMedicalRecordsByMedication(medication);
            return ResponseEntity.ok(new ApiResponse("success", "Medical records retrieved successfully", records));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve medical records: " + e.getMessage(), null));
        }
    }
    
    // Get records by treatment
    @GetMapping("/treatment/{treatment}")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> getMedicalRecordsByTreatment(@PathVariable String treatment) {
        try {
            List<MedicalRecordResponse> records = medicalRecordService.getMedicalRecordsByTreatment(treatment);
            return ResponseEntity.ok(new ApiResponse("success", "Medical records retrieved successfully", records));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve medical records: " + e.getMessage(), null));
        }
    }
    
    // Get records with follow-up required
    @GetMapping("/follow-up-required")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> getRecordsWithFollowUpRequired() {
        try {
            List<MedicalRecordResponse> records = medicalRecordService.getRecordsWithFollowUpRequired();
            return ResponseEntity.ok(new ApiResponse("success", "Records with follow-up retrieved successfully", records));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve records: " + e.getMessage(), null));
        }
    }
    
    // Get upcoming follow-ups for current veterinarian
    @GetMapping("/me/follow-ups/upcoming")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> getMyUpcomingFollowUps(@RequestParam(defaultValue = "30") int days) {
        try {
            Long veterinarianId = getCurrentVeterinarianId();
            List<MedicalRecordResponse> records = medicalRecordService.getUpcomingFollowUps(veterinarianId, days);
            return ResponseEntity.ok(new ApiResponse("success", "Upcoming follow-ups retrieved successfully", records));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve follow-ups: " + e.getMessage(), null));
        }
    }
    
    // Get overdue follow-ups
    @GetMapping("/follow-ups/overdue")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> getOverdueFollowUps() {
        try {
            List<MedicalRecordResponse> records = medicalRecordService.getOverdueFollowUps();
            return ResponseEntity.ok(new ApiResponse("success", "Overdue follow-ups retrieved successfully", records));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve overdue follow-ups: " + e.getMessage(), null));
        }
    }
    
    // Mark follow-up as completed
    @PostMapping("/{recordId}/follow-up/complete")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> markFollowUpCompleted(@PathVariable Long recordId, @RequestParam(required = false) String followUpNotes) {
        try {
            Optional<MedicalRecordResponse> record = medicalRecordService.markFollowUpCompleted(recordId, followUpNotes);
            if (record.isPresent()) {
                return ResponseEntity.ok(new ApiResponse("success", "Follow-up marked as completed", record.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to complete follow-up: " + e.getMessage(), null));
        }
    }
    
    // Get pet's diagnosis history
    @GetMapping("/pet/{petId}/diagnosis-history")
    @PreAuthorize("hasRole('VETERINARIAN') or hasRole('PET_OWNER')")
    public ResponseEntity<?> getPetDiagnosisHistory(@PathVariable Long petId) {
        try {
            List<VetMedicalRecordService.DiagnosisHistory> history = medicalRecordService.getPetDiagnosisHistory(petId);
            return ResponseEntity.ok(new ApiResponse("success", "Diagnosis history retrieved successfully", history));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve diagnosis history: " + e.getMessage(), null));
        }
    }
    
    // Get current veterinarian's medical record statistics
    @GetMapping("/me/stats")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> getMyMedicalRecordStats() {
        try {
            Long veterinarianId = getCurrentVeterinarianId();
            VetMedicalRecordService.MedicalRecordStats stats = medicalRecordService.getVeterinarianMedicalRecordStats(veterinarianId);
            return ResponseEntity.ok(new ApiResponse("success", "Medical record statistics retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve statistics: " + e.getMessage(), null));
        }
    }
    
    // Count records by current veterinarian
    @GetMapping("/me/count")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> getMyRecordCount() {
        try {
            Long veterinarianId = getCurrentVeterinarianId();
            long count = medicalRecordService.countRecordsByVeterinarian(veterinarianId);
            return ResponseEntity.ok(new ApiResponse("success", "Record count retrieved successfully", 
                new RecordCount("veterinarian", veterinarianId, count)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve record count: " + e.getMessage(), null));
        }
    }
    
    // Count records by pet
    @GetMapping("/pet/{petId}/count")
    @PreAuthorize("hasRole('VETERINARIAN') or hasRole('PET_OWNER')")
    public ResponseEntity<?> getRecordCountByPet(@PathVariable Long petId) {
        try {
            long count = medicalRecordService.countRecordsByPet(petId);
            return ResponseEntity.ok(new ApiResponse("success", "Pet record count retrieved successfully", 
                new RecordCount("pet", petId, count)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve record count: " + e.getMessage(), null));
        }
    }
    
    // Check if appointment has medical record
    @GetMapping("/appointment/{appointmentId}/exists")
    @PreAuthorize("hasRole('VETERINARIAN') or hasRole('PET_OWNER')")
    public ResponseEntity<?> hasAppointmentMedicalRecord(@PathVariable Long appointmentId) {
        try {
            boolean hasRecord = medicalRecordService.hasAppointmentMedicalRecord(appointmentId);
            return ResponseEntity.ok(new ApiResponse("success", "Check completed successfully", 
                new RecordExistence(appointmentId, hasRecord)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to check medical record: " + e.getMessage(), null));
        }
    }
    
    // Delete medical record
    @DeleteMapping("/{recordId}")
    @PreAuthorize("hasRole('VETERINARIAN') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteMedicalRecord(@PathVariable Long recordId) {
        try {
            boolean deleted = medicalRecordService.deleteMedicalRecord(recordId);
            if (deleted) {
                return ResponseEntity.ok(new ApiResponse("success", "Medical record deleted successfully", null));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to delete medical record: " + e.getMessage(), null));
        }
    }
    
    // Get records needing follow-up reminders
    @GetMapping("/follow-up-reminders")
    @PreAuthorize("hasRole('VETERINARIAN')")
    public ResponseEntity<?> getRecordsNeedingFollowUpReminder(@RequestParam(defaultValue = "7") int reminderDaysBefore) {
        try {
            List<MedicalRecordResponse> records = medicalRecordService.getRecordsNeedingFollowUpReminder(reminderDaysBefore);
            return ResponseEntity.ok(new ApiResponse("success", "Records needing reminders retrieved successfully", records));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse("error", "Failed to retrieve records: " + e.getMessage(), null));
        }
    }
    
    // Helper method to get current veterinarian ID
    private Long getCurrentVeterinarianId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return user.getId();
    }
    
    // Helper classes
    public static class RecordCount {
        private String type;
        private Long entityId;
        private long count;
        
        public RecordCount(String type, Long entityId, long count) {
            this.type = type;
            this.entityId = entityId;
            this.count = count;
        }
        
        // Getters and setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public Long getEntityId() { return entityId; }
        public void setEntityId(Long entityId) { this.entityId = entityId; }
        
        public long getCount() { return count; }
        public void setCount(long count) { this.count = count; }
    }
    
    public static class RecordExistence {
        private Long appointmentId;
        private boolean hasRecord;
        
        public RecordExistence(Long appointmentId, boolean hasRecord) {
            this.appointmentId = appointmentId;
            this.hasRecord = hasRecord;
        }
        
        // Getters and setters
        public Long getAppointmentId() { return appointmentId; }
        public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }
        
        public boolean isHasRecord() { return hasRecord; }
        public void setHasRecord(boolean hasRecord) { this.hasRecord = hasRecord; }
    }
    
    // API Response class
    public static class ApiResponse {
        private String status;
        private String message;
        private Object data;
        
        public ApiResponse(String status, String message, Object data) {
            this.status = status;
            this.message = message;
            this.data = data;
        }
        
        // Getters and setters
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
    }
}
