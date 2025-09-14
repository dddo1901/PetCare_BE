package TechWiz.veterinarian.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VeterinarianDashboardResponse {
    
    // Statistics
    private Long totalAppointments;
    private Long pendingAppointments;
    private Long confirmedAppointments;
    private Long completedAppointments;
    private Long cancelledAppointments;
    
    private Long totalMedicalRecords;
    private Long todayAppointments;
    private Long upcomingAppointments;
    
    // Financial
    private Double totalRevenue;
    private Double monthlyRevenue;
    private Double averageConsultationFee;
    
    // Recent activities
    private List<RecentAppointment> recentAppointments;
    private List<RecentMedicalRecord> recentMedicalRecords;
    
    // Upcoming appointments (next 7 days)
    private List<UpcomingAppointment> upcomingAppointmentsList;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentAppointment {
        private Long id;
        private String petName;
        private String ownerName;
        private String type;
        private String status;
        private LocalDateTime appointmentDateTime;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentMedicalRecord {
        private Long id;
        private String petName;
        private String diagnosis;
        private String type;
        private LocalDateTime recordDate;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpcomingAppointment {
        private Long id;
        private String petName;
        private String ownerName;
        private String type;
        private LocalDateTime appointmentDateTime;
        private String location;
    }
}
