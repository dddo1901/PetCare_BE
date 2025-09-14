package TechWiz.petOwner.services;

import TechWiz.auths.models.VeterinarianProfile;
import TechWiz.veterinarian.repositories.VeterinarianProfileRepository;
import TechWiz.petOwner.models.PetOwnerAppointment;
import TechWiz.petOwner.repositories.PetOwnerAppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PetOwnerWorkingHoursService {

    @Autowired
    private VeterinarianProfileRepository veterinarianProfileRepository;

    @Autowired
    private PetOwnerAppointmentRepository appointmentRepository;

    public Map<String, Object> getVetWorkingHours(Long vetId) {
        try {
            Optional<VeterinarianProfile> vetProfileOpt = veterinarianProfileRepository.findById(vetId);
            if (vetProfileOpt.isPresent()) {
                VeterinarianProfile vetProfile = vetProfileOpt.get();
                
                Map<String, Object> workingHours = new HashMap<>();
                workingHours.put("vetId", vetId);
                workingHours.put("clinicName", vetProfile.getClinicName());
                workingHours.put("clinicAddress", vetProfile.getClinicAddress());
                workingHours.put("availableFromTime", vetProfile.getAvailableFromTime());
                workingHours.put("availableToTime", vetProfile.getAvailableToTime());
                workingHours.put("availableDays", vetProfile.getAvailableDays());
                workingHours.put("consultationFee", vetProfile.getConsultationFee());
                
                return workingHours;
            } else {
                throw new RuntimeException("Veterinarian profile not found");
            }
        } catch (Exception e) {
            System.err.println("Error retrieving working hours for vet " + vetId + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve working hours: " + e.getMessage());
        }
    }

    public Map<String, Object> getAvailableTimeSlots(Long vetId, LocalDate appointmentDate) {
        try {
            System.out.println("=== DEBUG: Getting available slots for vet " + vetId + " on " + appointmentDate + " ===");
            
            // Get vet working hours
            Map<String, Object> workingHours = getVetWorkingHours(vetId);
            System.out.println("Working hours data: " + workingHours);
            
            // Check if vet works on this day
            String availableDaysString = (String) workingHours.get("availableDays");
            System.out.println("Available days string: " + availableDaysString);
            
            // Parse available days - handle both correct format and legacy format
            List<String> availableDays = new ArrayList<>();
            if (availableDaysString != null && !availableDaysString.trim().isEmpty()) {
                // If it's the legacy format "11.11.11.11.11", assume vet works all days
                if (availableDaysString.contains("11.11.11.11.11") || availableDaysString.contains("11")) {
                    availableDays = Arrays.asList("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY");
                    System.out.println("Detected legacy format, assuming vet works all days");
                } else {
                    // Normal comma-separated format
                    availableDays = Arrays.asList(availableDaysString.split(",")).stream()
                            .map(String::trim)
                            .collect(Collectors.toList());
                }
            }
            
            String dayOfWeek = appointmentDate.getDayOfWeek().name();
            
            System.out.println("Available days (parsed): " + availableDays);
            System.out.println("Requested day: " + dayOfWeek);
            
            if (!availableDays.contains(dayOfWeek)) {
                System.out.println("Vet not available on " + dayOfWeek);
                return Map.of(
                    "availableSlots", new ArrayList<>(),
                    "message", "Veterinarian is not available on " + dayOfWeek
                );
            }
            
            // Get working hours
            LocalTime fromTime = (LocalTime) workingHours.get("availableFromTime");
            LocalTime toTime = (LocalTime) workingHours.get("availableToTime");
            
            System.out.println("From time: " + fromTime + ", To time: " + toTime);
            
            if (fromTime == null || toTime == null) {
                System.out.println("Working hours not set");
                return Map.of(
                    "availableSlots", new ArrayList<>(),
                    "message", "Working hours not set for this veterinarian"
                );
            }
            
            // Generate time slots (30-minute intervals)
            List<Map<String, Object>> allSlots = generateTimeSlots(fromTime, toTime);
            System.out.println("Generated " + allSlots.size() + " time slots");
            
            // Get confirmed appointments for this date
            LocalDateTime startOfDay = appointmentDate.atStartOfDay();
            LocalDateTime endOfDay = appointmentDate.atTime(23, 59, 59);
            
            List<PetOwnerAppointment> confirmedAppointments = appointmentRepository
                .findByVetIdAndAppointmentDateTimeBetweenAndStatus(vetId, startOfDay, endOfDay, PetOwnerAppointment.AppointmentStatus.CONFIRMED);
            
            System.out.println("Found " + confirmedAppointments.size() + " confirmed appointments");
            
            // Create set of booked times
            Set<String> bookedTimes = new HashSet<>();
            for (PetOwnerAppointment appointment : confirmedAppointments) {
                LocalTime appointmentTime = appointment.getAppointmentDateTime().toLocalTime();
                String timeSlot = formatTimeSlot(appointmentTime);
                bookedTimes.add(timeSlot);
                System.out.println("Booked time: " + timeSlot);
            }
            
            // Filter available slots
            List<Map<String, Object>> availableSlots = new ArrayList<>();
            for (Map<String, Object> slot : allSlots) {
                String timeSlot = (String) slot.get("timeSlot");
                if (!bookedTimes.contains(timeSlot)) {
                    slot.put("available", true);
                    availableSlots.add(slot);
                } else {
                    slot.put("available", false);
                    slot.put("reason", "Already booked");
                    availableSlots.add(slot);
                }
            }
            
            System.out.println("Final available slots: " + availableSlots.size());
            System.out.println("=== END DEBUG ===");
            
            return Map.of(
                "availableSlots", availableSlots,
                "workingHours", Map.of(
                    "from", fromTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                    "to", toTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                ),
                "totalSlots", allSlots.size(),
                "availableCount", availableSlots.stream().mapToInt(slot -> (Boolean) slot.get("available") ? 1 : 0).sum(),
                "bookedCount", bookedTimes.size()
            );
            
        } catch (Exception e) {
            System.err.println("Error retrieving available time slots for vet " + vetId + " on " + appointmentDate + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve available time slots: " + e.getMessage());
        }
    }

    private List<Map<String, Object>> generateTimeSlots(LocalTime fromTime, LocalTime toTime) {
        List<Map<String, Object>> slots = new ArrayList<>();
        
        LocalTime currentTime = fromTime;
        while (currentTime.isBefore(toTime)) {
            Map<String, Object> slot = new HashMap<>();
            slot.put("timeSlot", formatTimeSlot(currentTime));
            slot.put("displayTime", currentTime.format(DateTimeFormatter.ofPattern("HH:mm")));
            slot.put("available", true);
            slots.add(slot);
            
            currentTime = currentTime.plusMinutes(60);
        }
        
        return slots;
    }

    private String formatTimeSlot(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}
