package TechWiz.petOwner.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import TechWiz.petOwner.repositories.PetOwnerAppointmentRepository;
import TechWiz.petOwner.repositories.PetOwnerHealthRecordRepository;
import TechWiz.petOwner.repositories.PetOwnerPetRepository;
import TechWiz.petOwner.repositories.PetGalleryRepository;
import TechWiz.petOwner.models.PetOwnerAppointment;
import TechWiz.petOwner.models.PetOwnerHealthRecord;
import TechWiz.petOwner.models.PetOwnerPet;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PetOwnerDashboardService {

    @Autowired
    private PetOwnerPetRepository petRepository;

    @Autowired
    private PetOwnerAppointmentRepository appointmentRepository;

    @Autowired
    private PetOwnerHealthRecordRepository healthRecordRepository;

    @Autowired
    private PetGalleryRepository galleryRepository;

    public Map<String, Object> getDashboardStats(Long ownerId) {
        Map<String, Object> stats = new HashMap<>();

        try {
            // Total Pets
            long totalPets = petRepository.countByOwnerId(ownerId);
            stats.put("totalPets", totalPets);

            // Upcoming Appointments (next 7 days)
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nextWeek = now.plusDays(7);
            long upcomingAppointments = appointmentRepository.countByOwnerIdAndAppointmentDateTimeBetweenAndStatusNot(
                ownerId, now, nextWeek, PetOwnerAppointment.AppointmentStatus.CANCELLED);
            stats.put("upcomingAppointments", upcomingAppointments);

            // Total Health Records
            long totalHealthRecords = healthRecordRepository.countByOwnerId(ownerId);
            stats.put("totalHealthRecords", totalHealthRecords);

            // Total Gallery Photos
            long totalGalleryPhotos = galleryRepository.countByOwnerId(ownerId);
            stats.put("totalGalleryPhotos", totalGalleryPhotos);

            // Recent Appointments (last 30 days)
            LocalDateTime lastMonth = now.minusDays(30);
            long recentAppointments = appointmentRepository.countByOwnerIdAndAppointmentDateTimeBetweenAndStatusNot(
                ownerId, lastMonth, now, PetOwnerAppointment.AppointmentStatus.CANCELLED);
            stats.put("recentAppointments", recentAppointments);

            // Health Status Overview
            List<PetOwnerPet> pets = petRepository.findByOwnerId(ownerId);
            Map<String, Long> healthStatusCount = pets.stream()
                .collect(Collectors.groupingBy(
                    pet -> pet.getHealthStatus() != null ? pet.getHealthStatus().toString() : "UNKNOWN",
                    Collectors.counting()
                ));
            stats.put("healthStatusOverview", healthStatusCount);

            // Pet Age Distribution
            Map<String, Long> ageDistribution = pets.stream()
                .collect(Collectors.groupingBy(
                    pet -> getAgeGroup(pet.getAgeInMonths() != null ? pet.getAgeInMonths().toString() : "0"),
                    Collectors.counting()
                ));
            stats.put("ageDistribution", ageDistribution);

            // Monthly Appointments Trend (last 6 months)
            Map<String, Long> monthlyTrend = getMonthlyAppointmentsTrend(ownerId);
            stats.put("monthlyTrend", monthlyTrend);

        } catch (Exception e) {
            System.err.println("Error getting dashboard stats: " + e.getMessage());
            e.printStackTrace();
        }

        return stats;
    }

    public Map<String, Object> getRecentActivities(Long ownerId) {
        Map<String, Object> activities = new HashMap<>();

        try {
            // Recent Appointments (last 5)
            List<PetOwnerAppointment> recentAppointments = appointmentRepository
                .findTop5ByOwnerIdOrderByAppointmentDateTimeDesc(ownerId);
            
            List<Map<String, Object>> appointmentActivities = recentAppointments.stream()
                .map(this::convertAppointmentToActivity)
                .collect(Collectors.toList());
            activities.put("recentAppointments", appointmentActivities);

            // Recent Health Records (last 5)
            List<PetOwnerHealthRecord> recentHealthRecords = healthRecordRepository
                .findTop5ByOwnerIdOrderByRecordDateDesc(ownerId);
            
            List<Map<String, Object>> healthActivities = recentHealthRecords.stream()
                .map(this::convertHealthRecordToActivity)
                .collect(Collectors.toList());
            activities.put("recentHealthRecords", healthActivities);

            // Recent Pet Updates (last 5)
            List<PetOwnerPet> recentPets = petRepository
                .findTop5ByOwnerIdOrderByUpdatedAtDesc(ownerId);
            
            List<Map<String, Object>> petActivities = recentPets.stream()
                .map(this::convertPetToActivity)
                .collect(Collectors.toList());
            activities.put("recentPetUpdates", petActivities);

        } catch (Exception e) {
            System.err.println("Error getting recent activities: " + e.getMessage());
            e.printStackTrace();
        }

        return activities;
    }

    public Map<String, Object> getAnalytics(Long ownerId) {
        Map<String, Object> analytics = new HashMap<>();

        try {
            // Appointments by Status
            List<PetOwnerAppointment> allAppointments = appointmentRepository.findByOwnerId(ownerId);
            Map<String, Long> appointmentsByStatus = allAppointments.stream()
                .collect(Collectors.groupingBy(
                    apt -> apt.getStatus() != null ? apt.getStatus().toString() : "UNKNOWN",
                    Collectors.counting()
                ));
            analytics.put("appointmentsByStatus", appointmentsByStatus);

            // Health Records by Type
            List<PetOwnerHealthRecord> allHealthRecords = healthRecordRepository.findByOwnerId(ownerId);
            Map<String, Long> healthRecordsByType = allHealthRecords.stream()
                .collect(Collectors.groupingBy(
                    record -> record.getType() != null ? record.getType() : "UNKNOWN",
                    Collectors.counting()
                ));
            analytics.put("healthRecordsByType", healthRecordsByType);

            // Most Active Pet (by appointments)
            Map<Long, Long> petAppointmentCounts = allAppointments.stream()
                .collect(Collectors.groupingBy(
                    PetOwnerAppointment::getPetId,
                    Collectors.counting()
                ));
            
            if (!petAppointmentCounts.isEmpty()) {
                Long mostActivePetId = Collections.max(petAppointmentCounts.entrySet(), 
                    Map.Entry.comparingByValue()).getKey();
                Optional<PetOwnerPet> mostActivePet = petRepository.findById(mostActivePetId);
                if (mostActivePet.isPresent()) {
                    analytics.put("mostActivePet", Map.of(
                        "id", mostActivePet.get().getId(),
                        "name", mostActivePet.get().getName(),
                        "appointmentCount", petAppointmentCounts.get(mostActivePetId)
                    ));
                }
            }

        } catch (Exception e) {
            System.err.println("Error getting analytics: " + e.getMessage());
            e.printStackTrace();
        }

        return analytics;
    }

    private String getAgeGroup(String ageInMonths) {
        if (ageInMonths == null || ageInMonths.isEmpty()) return "UNKNOWN";
        
        try {
            int months = Integer.parseInt(ageInMonths);
            if (months < 12) return "0-1 years";
            else if (months < 24) return "1-2 years";
            else if (months < 60) return "2-5 years";
            else if (months < 120) return "5-10 years";
            else return "10+ years";
        } catch (NumberFormatException e) {
            return "UNKNOWN";
        }
    }

    private Map<String, Long> getMonthlyAppointmentsTrend(Long ownerId) {
        Map<String, Long> trend = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (int i = 5; i >= 0; i--) {
            LocalDateTime monthStart = now.minusMonths(i).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime monthEnd = monthStart.plusMonths(1).minusSeconds(1);
            
            long count = appointmentRepository.countByOwnerIdAndAppointmentDateTimeBetweenAndStatusNot(
                ownerId, monthStart, monthEnd, PetOwnerAppointment.AppointmentStatus.CANCELLED);
            
            String monthKey = monthStart.format(DateTimeFormatter.ofPattern("MMM yyyy"));
            trend.put(monthKey, count);
        }
        
        return trend;
    }

    private Map<String, Object> convertAppointmentToActivity(PetOwnerAppointment appointment) {
        Map<String, Object> activity = new HashMap<>();
        activity.put("id", appointment.getId());
        activity.put("type", "APPOINTMENT");
        activity.put("title", "Appointment - " + appointment.getType());
        activity.put("date", appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        activity.put("status", appointment.getStatus().toString());
        activity.put("petName", "Pet ID: " + appointment.getPetId());
        return activity;
    }

    private Map<String, Object> convertHealthRecordToActivity(PetOwnerHealthRecord record) {
        Map<String, Object> activity = new HashMap<>();
        activity.put("id", record.getId());
        activity.put("type", "HEALTH_RECORD");
        activity.put("title", "Health Record: " + (record.getType() != null ? record.getType() : "General"));
        activity.put("date", record.getRecordDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        activity.put("petName", "Pet ID: " + record.getPetId());
        return activity;
    }

    private Map<String, Object> convertPetToActivity(PetOwnerPet pet) {
        Map<String, Object> activity = new HashMap<>();
        activity.put("id", pet.getId());
        activity.put("type", "PET_UPDATE");
        activity.put("title", "Pet Updated: " + pet.getName());
        activity.put("date", pet.getUpdatedAt() != null ? 
            pet.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : 
            "Unknown");
        activity.put("petName", pet.getName());
        return activity;
    }
}
