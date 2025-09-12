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
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import TechWiz.shelter.models.AdoptionInquiry;
import TechWiz.shelter.services.AdoptionInquiryService;
import TechWiz.shelter.dto.*;

@RestController
@RequestMapping("/api/shelter")
@Validated
@CrossOrigin(origins = "*")
public class AdoptionInquiryController {
    
    @Autowired
    private AdoptionInquiryService adoptionInquiryService;
    
    @PostMapping("/adoption-inquiries")
    public ResponseEntity<?> createAdoptionInquiry(@Valid @RequestBody AdoptionInquiryRequestDto requestDto) {
        try {
            AdoptionInquiryResponseDto inquiry = adoptionInquiryService.createAdoptionInquiry(requestDto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Adoption inquiry submitted successfully");
            response.put("data", inquiry);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @GetMapping("/adoption-inquiries/{id}")
    public ResponseEntity<?> getInquiryById(@PathVariable @Positive Long id) {
        try {
            AdoptionInquiryResponseDto inquiry = adoptionInquiryService.getInquiryById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", inquiry);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    @GetMapping("/{shelterId}/adoption-inquiries")
    public ResponseEntity<?> getInquiriesByShelterId(
            @PathVariable @Positive Long shelterId,
            @RequestParam(required = false) Long petId,
            @RequestParam(required = false) AdoptionInquiry.InquiryStatus status,
            @RequestParam(required = false) String adopterName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<AdoptionInquiryResponseDto> inquiries = adoptionInquiryService.getInquiriesByShelterId(
                shelterId, petId, status, adopterName, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", inquiries.getContent());
            response.put("currentPage", inquiries.getNumber());
            response.put("totalPages", inquiries.getTotalPages());
            response.put("totalElements", inquiries.getTotalElements());
            response.put("size", inquiries.getSize());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/pets/{petId}/adoption-inquiries")
    public ResponseEntity<?> getInquiriesByPetId(@PathVariable @Positive Long petId) {
        try {
            List<AdoptionInquiryResponseDto> inquiries = adoptionInquiryService.getInquiriesByPetId(petId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", inquiries);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/adoption-inquiries/status/{status}")
    public ResponseEntity<?> getInquiriesByStatus(@PathVariable AdoptionInquiry.InquiryStatus status) {
        try {
            List<AdoptionInquiryResponseDto> inquiries = adoptionInquiryService.getInquiriesByStatus(status);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", inquiries);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/{shelterId}/adoption-inquiries/stats")
    public ResponseEntity<?> getInquiryStatsByShelterId(@PathVariable @Positive Long shelterId) {
        try {
            Map<String, Long> stats = new HashMap<>();
            stats.put("NEW", adoptionInquiryService.getInquiryCountByShelterIdAndStatus(shelterId, AdoptionInquiry.InquiryStatus.NEW));
            stats.put("CONTACTED", adoptionInquiryService.getInquiryCountByShelterIdAndStatus(shelterId, AdoptionInquiry.InquiryStatus.CONTACTED));
            stats.put("IN_REVIEW", adoptionInquiryService.getInquiryCountByShelterIdAndStatus(shelterId, AdoptionInquiry.InquiryStatus.IN_REVIEW));
            stats.put("APPROVED", adoptionInquiryService.getInquiryCountByShelterIdAndStatus(shelterId, AdoptionInquiry.InquiryStatus.APPROVED));
            stats.put("REJECTED", adoptionInquiryService.getInquiryCountByShelterIdAndStatus(shelterId, AdoptionInquiry.InquiryStatus.REJECTED));
            stats.put("COMPLETED", adoptionInquiryService.getInquiryCountByShelterIdAndStatus(shelterId, AdoptionInquiry.InquiryStatus.COMPLETED));
            
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
    
    @PutMapping("/adoption-inquiries/{id}/respond")
    public ResponseEntity<?> respondToInquiry(@PathVariable @Positive Long id,
                                             @Valid @RequestBody InquiryResponseRequestDto responseDto) {
        try {
            AdoptionInquiryResponseDto inquiry = adoptionInquiryService.respondToInquiry(id, responseDto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Response sent successfully");
            response.put("data", inquiry);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @PutMapping("/adoption-inquiries/{id}/status")
    public ResponseEntity<?> updateInquiryStatus(@PathVariable @Positive Long id,
                                                @RequestParam AdoptionInquiry.InquiryStatus status) {
        try {
            AdoptionInquiryResponseDto inquiry = adoptionInquiryService.updateInquiryStatus(id, status);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Inquiry status updated successfully");
            response.put("data", inquiry);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @DeleteMapping("/adoption-inquiries/{id}")
    public ResponseEntity<?> deleteInquiry(@PathVariable @Positive Long id) {
        try {
            adoptionInquiryService.deleteInquiry(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Adoption inquiry deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
