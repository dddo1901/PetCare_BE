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

import TechWiz.shelter.models.Shelter;
import TechWiz.shelter.services.ShelterService;
import TechWiz.shelter.dto.*;

@RestController
@RequestMapping("/api/shelter")
@Validated
@CrossOrigin(origins = "*")
public class ShelterController {
    
    @Autowired
    private ShelterService shelterService;
    
    @PostMapping("/register")
    public ResponseEntity<?> registerShelter(@Valid @RequestBody ShelterRegistrationRequestDto requestDto) {
        try {
            ShelterResponseDto shelter = shelterService.registerShelter(requestDto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Shelter registered successfully. Waiting for verification.");
            response.put("data", shelter);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getShelterById(@PathVariable @Positive Long id) {
        try {
            ShelterResponseDto shelter = shelterService.getShelterById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", shelter);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getShelterByEmail(@PathVariable String email) {
        try {
            ShelterResponseDto shelter = shelterService.getShelterByEmail(email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", shelter);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    @GetMapping("/verified")
    public ResponseEntity<?> getVerifiedShelters() {
        try {
            List<ShelterResponseDto> shelters = shelterService.getVerifiedShelters();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", shelters);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllShelters(
            @RequestParam(required = false) String shelterName,
            @RequestParam(required = false) Shelter.ShelterStatus status,
            @RequestParam(required = false) Boolean isVerified,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<ShelterResponseDto> shelters = shelterService.getAllShelters(
                shelterName, status, isVerified, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", shelters.getContent());
            response.put("currentPage", shelters.getNumber());
            response.put("totalPages", shelters.getTotalPages());
            response.put("totalElements", shelters.getTotalElements());
            response.put("size", shelters.getSize());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateShelter(@PathVariable @Positive Long id,
                                          @Valid @RequestBody ShelterRegistrationRequestDto requestDto) {
        try {
            ShelterResponseDto shelter = shelterService.updateShelter(id, requestDto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Shelter updated successfully");
            response.put("data", shelter);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateShelterStatus(@PathVariable @Positive Long id,
                                                @RequestParam Shelter.ShelterStatus status) {
        try {
            ShelterResponseDto shelter = shelterService.updateShelterStatus(id, status);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Shelter status updated successfully");
            response.put("data", shelter);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @PutMapping("/{id}/verify")
    public ResponseEntity<?> verifyShelter(@PathVariable @Positive Long id,
                                          @RequestParam boolean verified) {
        try {
            ShelterResponseDto shelter = shelterService.verifyShelter(id, verified);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", verified ? "Shelter verified successfully" : "Shelter verification removed");
            response.put("data", shelter);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShelter(@PathVariable @Positive Long id) {
        try {
            shelterService.deleteShelter(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Shelter deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
