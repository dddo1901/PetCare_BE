package TechWiz.shelter.services;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import TechWiz.shelter.models.ShelterPet;
import TechWiz.shelter.repositories.ShelterPetRepository;

@Service
public class DashboardService {

    @Autowired
    private ShelterPetService petService;
    
    @Autowired
    private ShelterPetRepository petRepository;
    
    @Autowired
    private AdoptionInquiryService adoptionInquiryService;

    public Map<String, Object> getShelterDashboardData(Long shelterId) {
        Map<String, Object> dashboardData = new HashMap<>();
        
        // Statistics
        Map<String, Long> stats = new HashMap<>();
        stats.put("adoptablePets", petService.getPetCountByStatus(shelterId, ShelterPet.AdoptionStatus.AVAILABLE));
        stats.put("newAdoptionRequests", adoptionInquiryService.getPendingInquiriesCount(shelterId));
        stats.put("petsAdopted", petService.getPetCountByStatus(shelterId, ShelterPet.AdoptionStatus.ADOPTED));
        stats.put("newNotifications", 0L); // TODO: implement notifications
        
        dashboardData.put("stats", stats);
        
        // Recent Activities
        List<Map<String, Object>> recentActivities = getRecentActivities(shelterId);
        dashboardData.put("recentActivities", recentActivities);
        
        // Upcoming Adoptions
        List<Map<String, Object>> upcomingAdoptions = getUpcomingAdoptions(shelterId);
        dashboardData.put("upcomingAdoptions", upcomingAdoptions);
        
        return dashboardData;
    }
    
    private List<Map<String, Object>> getRecentActivities(Long shelterId) {
        // Get recently added pets
        List<ShelterPet> recentPets = petRepository.findByShelterProfileIdOrderByCreatedAtDesc(shelterId)
                .stream()
                .limit(5)
                .collect(Collectors.toList());
        
        return recentPets.stream().map(pet -> {
            Map<String, Object> activity = new HashMap<>();
            activity.put("action", determineAction(pet));
            activity.put("pet", pet.getName() + " (" + pet.getBreed() + ")");
            activity.put("time", getTimeAgo(pet.getUpdatedAt()));
            return activity;
        }).collect(Collectors.toList());
    }
    
    private List<Map<String, Object>> getUpcomingAdoptions(Long shelterId) {
        // Get pending adoption inquiries
        var pendingInquiries = adoptionInquiryService.getInquiriesByShelterId(shelterId)
                .stream()
                .limit(5)
                .collect(Collectors.toList());
        
        return pendingInquiries.stream().map(inquiry -> {
            Map<String, Object> adoption = new HashMap<>();
            adoption.put("pet", inquiry.getPet().getName());
            adoption.put("adopter", inquiry.getAdopterName());
            adoption.put("date", inquiry.getCreatedAt().toLocalDate().toString());
            adoption.put("time", inquiry.getCreatedAt().toLocalTime().toString().substring(0, 5));
            return adoption;
        }).collect(Collectors.toList());
    }
    
    private String determineAction(ShelterPet pet) {
        long daysSinceCreated = java.time.Duration.between(pet.getCreatedAt(), LocalDateTime.now()).toDays();
        long daysSinceUpdated = java.time.Duration.between(pet.getUpdatedAt(), LocalDateTime.now()).toDays();
        
        if (daysSinceCreated <= 1) {
            return "Added new pet";
        } else if (daysSinceUpdated <= 1 && pet.getAdoptionStatus() == ShelterPet.AdoptionStatus.ADOPTED) {
            return "Confirmed adoption";
        } else if (daysSinceUpdated <= 1) {
            return "Updated status";
        } else {
            return "Added new pet";
        }
    }
    
    private String getTimeAgo(LocalDateTime dateTime) {
        long hours = java.time.Duration.between(dateTime, LocalDateTime.now()).toHours();
        long days = hours / 24;
        
        if (hours < 1) {
            return "Just now";
        } else if (hours < 24) {
            return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        } else {
            return days + " day" + (days > 1 ? "s" : "") + " ago";
        }
    }
}