package TechWiz.shelter.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import TechWiz.shelter.dto.CareLogRequestDto;
import TechWiz.shelter.dto.CareLogResponseDto;
import TechWiz.shelter.models.CareLog;
import TechWiz.shelter.services.CareLogService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/shelter")
@Validated
@CrossOrigin(origins = "*")
public class CareLogController {
    
    @Autowired
    private CareLogService careLogService;
    
    @PostMapping("/{shelterId}/care-logs")
    public ResponseEntity<?> createCareLog(@PathVariable @Positive Long shelterId,
                                          @Valid @RequestBody CareLogRequestDto requestDto) {
        try {
            CareLogResponseDto careLog = careLogService.createCareLog(shelterId, requestDto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Care log added successfully");
            response.put("data", careLog);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @GetMapping("/care-logs/{id}")
    public ResponseEntity<?> getCareLogById(@PathVariable @Positive Long id) {
        try {
            CareLogResponseDto careLog = careLogService.getCareLogById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", careLog);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    @GetMapping("/{shelterId}/care-logs")
    public ResponseEntity<?> getCareLogsByShelterId(
            @PathVariable @Positive Long shelterId,
            @RequestParam(required = false) Long petId,
            @RequestParam(required = false) CareLog.CareType type,
            @RequestParam(required = false) String staffName,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "careDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateFromParsed = dateFrom != null ? LocalDateTime.parse(dateFrom + " 00:00:00", formatter) : null;
            LocalDateTime dateToParsed = dateTo != null ? LocalDateTime.parse(dateTo + " 23:59:59", formatter) : null;
            
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<CareLogResponseDto> careLogs = careLogService.getCareLogsByShelterId(
                shelterId, petId, type, staffName, dateFromParsed, dateToParsed, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", careLogs.getContent());
            response.put("currentPage", careLogs.getNumber());
            response.put("totalPages", careLogs.getTotalPages());
            response.put("totalElements", careLogs.getTotalElements());
            response.put("size", careLogs.getSize());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/pets/{petId}/care-logs")
    public ResponseEntity<?> getCareLogsByPetId(@PathVariable @Positive Long petId) {
        try {
            List<CareLogResponseDto> careLogs = careLogService.getCareLogsByPetId(petId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", careLogs);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/pets/{petId}/care-logs/type/{type}")
    public ResponseEntity<?> getCareLogsByPetIdAndType(@PathVariable @Positive Long petId,
                                                      @PathVariable CareLog.CareType type) {
        try {
            List<CareLogResponseDto> careLogs = careLogService.getCareLogsByPetIdAndType(petId, type);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", careLogs);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/pets/{petId}/care-logs/today")
    public ResponseEntity<?> getTodayCareLogsByPetId(@PathVariable @Positive Long petId) {
        try {
            List<CareLogResponseDto> careLogs = careLogService.getTodayCareLogsByPetId(petId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", careLogs);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/{shelterId}/care-logs/today/stats")
    public ResponseEntity<?> getTodayCareLogsStats(@PathVariable @Positive Long shelterId) {
        try {
            Map<String, Long> stats = new HashMap<>();
            stats.put("feeding", careLogService.getTodayCareLogsCountByShelterIdAndType(shelterId, CareLog.CareType.FEEDING));
            stats.put("grooming", careLogService.getTodayCareLogsCountByShelterIdAndType(shelterId, CareLog.CareType.GROOMING));
            stats.put("medical", careLogService.getTodayCareLogsCountByShelterIdAndType(shelterId, CareLog.CareType.MEDICAL));
            stats.put("exercise", careLogService.getTodayCareLogsCountByShelterIdAndType(shelterId, CareLog.CareType.EXERCISE));
            stats.put("training", careLogService.getTodayCareLogsCountByShelterIdAndType(shelterId, CareLog.CareType.TRAINING));
            stats.put("other", careLogService.getTodayCareLogsCountByShelterIdAndType(shelterId, CareLog.CareType.OTHER));
            
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
    
    @PutMapping("/care-logs/{id}")
    public ResponseEntity<?> updateCareLog(@PathVariable @Positive Long id,
                                          @Valid @RequestBody CareLogRequestDto requestDto) {
        try {
            CareLogResponseDto careLog = careLogService.updateCareLog(id, requestDto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Care log updated successfully");
            response.put("data", careLog);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @DeleteMapping("/care-logs/{id}")
    public ResponseEntity<?> deleteCareLog(@PathVariable @Positive Long id) {
        try {
            careLogService.deleteCareLog(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Care log deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @GetMapping("/{shelterId}/care-logs/recent")
    public ResponseEntity<?> getRecentCareLogsByShelterId(@PathVariable @Positive Long shelterId,
                                                         @RequestParam(defaultValue = "10") int limit) {
        try {
            List<CareLogResponseDto> careLogs = careLogService.getRecentCareLogsByShelterId(shelterId, limit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", careLogs);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/{shelterId}/care-logs/summary")
    public ResponseEntity<?> getCareLogsSummaryByShelterId(@PathVariable @Positive Long shelterId,
                                                          @RequestParam(required = false) String dateFrom,
                                                          @RequestParam(required = false) String dateTo) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateFromParsed = dateFrom != null ? LocalDateTime.parse(dateFrom + " 00:00:00", formatter) : null;
            LocalDateTime dateToParsed = dateTo != null ? LocalDateTime.parse(dateTo + " 23:59:59", formatter) : null;
            
            Map<String, Object> summary = careLogService.getCareLogsSummary(shelterId, dateFromParsed, dateToParsed);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", summary);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
