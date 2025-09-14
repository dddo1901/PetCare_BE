package TechWiz.petOwner.controllers;

import TechWiz.petOwner.services.PetOwnerWorkingHoursService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/pet-owner/working-hours")
@CrossOrigin(origins = "http://localhost:3000")
public class PetOwnerWorkingHoursController {

    @Autowired
    private PetOwnerWorkingHoursService workingHoursService;

    @GetMapping("/vet/{vetId}")
    public ResponseEntity<?> getVetWorkingHours(@PathVariable Long vetId) {
        try {
            Map<String, Object> workingHours = workingHoursService.getVetWorkingHours(vetId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", workingHours,
                "message", "Working hours retrieved successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to retrieve working hours: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/vet/{vetId}/available-slots")
    public ResponseEntity<?> getAvailableTimeSlots(
            @PathVariable Long vetId,
            @RequestParam String date) {
        try {
            LocalDate appointmentDate = LocalDate.parse(date);
            Map<String, Object> availableSlots = workingHoursService.getAvailableTimeSlots(vetId, appointmentDate);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", availableSlots,
                "message", "Available time slots retrieved successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to retrieve available time slots: " + e.getMessage()
            ));
        }
    }
}
