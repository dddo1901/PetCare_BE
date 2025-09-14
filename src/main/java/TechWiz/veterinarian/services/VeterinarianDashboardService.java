package TechWiz.veterinarian.services;

import TechWiz.veterinarian.dto.VeterinarianDashboardResponse;
import TechWiz.petOwner.models.PetOwnerAppointment;
import TechWiz.veterinarian.models.VeterinarianMedicalRecord;
import TechWiz.veterinarian.repositories.VeterinarianAppointmentRepository;
import TechWiz.veterinarian.repositories.VeterinarianMedicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VeterinarianDashboardService {
    
    @Autowired
    private VeterinarianAppointmentRepository appointmentRepository;
    
    @Autowired
    private VeterinarianMedicalRecordRepository medicalRecordRepository;
    
    public VeterinarianDashboardResponse getDashboardData(Long vetId) {
        VeterinarianDashboardResponse response = new VeterinarianDashboardResponse();
        
        // Appointment statistics
        response.setTotalAppointments(getTotalAppointments(vetId));
        response.setPendingAppointments(getPendingAppointments(vetId));
        response.setConfirmedAppointments(getConfirmedAppointments(vetId));
        response.setCompletedAppointments(getCompletedAppointments(vetId));
        response.setCancelledAppointments(getCancelledAppointments(vetId));
        
        // Medical record statistics
        response.setTotalMedicalRecords(getTotalMedicalRecords(vetId));
        
        // Today and upcoming appointments
        response.setTodayAppointments(getTodayAppointmentsCount(vetId));
        response.setUpcomingAppointments(getUpcomingAppointmentsCount(vetId));
        
        // Financial data
        response.setTotalRevenue(getTotalRevenue(vetId));
        response.setMonthlyRevenue(getMonthlyRevenue(vetId));
        response.setAverageConsultationFee(getAverageConsultationFee(vetId));
        
        // Recent activities
        response.setRecentAppointments(getRecentAppointments(vetId));
        response.setRecentMedicalRecords(getRecentMedicalRecords(vetId));
        response.setUpcomingAppointmentsList(getUpcomingAppointmentsList(vetId));
        
        return response;
    }
    
    private Long getTotalAppointments(Long vetId) {
        return appointmentRepository.countByVetIdAndStatus(vetId, PetOwnerAppointment.AppointmentStatus.PENDING) +
               appointmentRepository.countByVetIdAndStatus(vetId, PetOwnerAppointment.AppointmentStatus.CONFIRMED) +
               appointmentRepository.countByVetIdAndStatus(vetId, PetOwnerAppointment.AppointmentStatus.COMPLETED) +
               appointmentRepository.countByVetIdAndStatus(vetId, PetOwnerAppointment.AppointmentStatus.CANCELLED);
    }
    
    private Long getPendingAppointments(Long vetId) {
        return appointmentRepository.countByVetIdAndStatus(vetId, PetOwnerAppointment.AppointmentStatus.PENDING);
    }
    
    private Long getConfirmedAppointments(Long vetId) {
        return appointmentRepository.countByVetIdAndStatus(vetId, PetOwnerAppointment.AppointmentStatus.CONFIRMED);
    }
    
    private Long getCompletedAppointments(Long vetId) {
        return appointmentRepository.countByVetIdAndStatus(vetId, PetOwnerAppointment.AppointmentStatus.COMPLETED);
    }
    
    private Long getCancelledAppointments(Long vetId) {
        return appointmentRepository.countByVetIdAndStatus(vetId, PetOwnerAppointment.AppointmentStatus.CANCELLED);
    }
    
    private Long getTotalMedicalRecords(Long vetId) {
        return medicalRecordRepository.countByVetIdAndType(vetId, VeterinarianMedicalRecord.RecordType.EXAMINATION) +
               medicalRecordRepository.countByVetIdAndType(vetId, VeterinarianMedicalRecord.RecordType.TREATMENT) +
               medicalRecordRepository.countByVetIdAndType(vetId, VeterinarianMedicalRecord.RecordType.SURGERY) +
               medicalRecordRepository.countByVetIdAndType(vetId, VeterinarianMedicalRecord.RecordType.VACCINATION) +
               medicalRecordRepository.countByVetIdAndType(vetId, VeterinarianMedicalRecord.RecordType.FOLLOW_UP);
    }
    
    private Long getTodayAppointmentsCount(Long vetId) {
        return (long) appointmentRepository.findTodayAppointments(vetId, LocalDateTime.now()).size();
    }
    
    private Long getUpcomingAppointmentsCount(Long vetId) {
        return (long) appointmentRepository.findUpcomingAppointments(vetId, LocalDateTime.now()).size();
    }
    
    private Double getTotalRevenue(Long vetId) {
        Double revenue = medicalRecordRepository.calculateTotalRevenueByVetId(vetId);
        return revenue != null ? revenue : 0.0;
    }
    
    private Double getMonthlyRevenue(Long vetId) {
        LocalDateTime now = LocalDateTime.now();
        Double revenue = medicalRecordRepository.calculateMonthlyRevenueByVetId(vetId, now.getYear(), now.getMonthValue());
        return revenue != null ? revenue : 0.0;
    }
    
    private Double getAverageConsultationFee(Long vetId) {
        // This would require a custom query to calculate average consultation fee
        // For now, return a placeholder
        return 0.0;
    }
    
    private List<VeterinarianDashboardResponse.RecentAppointment> getRecentAppointments(Long vetId) {
        // Get appointments from the last 7 days
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        List<PetOwnerAppointment> appointments = appointmentRepository.findAppointmentsInDateRange(vetId, since, LocalDateTime.now());
        
        return appointments.stream()
                .limit(5) // Limit to 5 recent appointments
                .map(appointment -> {
                    VeterinarianDashboardResponse.RecentAppointment recent = new VeterinarianDashboardResponse.RecentAppointment();
                    recent.setId(appointment.getId());
                    recent.setPetName("Pet Name"); // TODO: Get from pet service
                    recent.setOwnerName("Owner Name"); // TODO: Get from owner service
                    recent.setType(appointment.getType());
                    recent.setStatus(appointment.getStatus().name());
                    recent.setAppointmentDateTime(appointment.getAppointmentDateTime());
                    return recent;
                })
                .collect(java.util.stream.Collectors.toList());
    }
    
    private List<VeterinarianDashboardResponse.RecentMedicalRecord> getRecentMedicalRecords(Long vetId) {
        List<VeterinarianMedicalRecord> records = medicalRecordRepository.findRecentMedicalRecords(vetId, LocalDateTime.now().minusDays(7));
        
        return records.stream()
                .limit(5) // Limit to 5 recent records
                .map(record -> {
                    VeterinarianDashboardResponse.RecentMedicalRecord recent = new VeterinarianDashboardResponse.RecentMedicalRecord();
                    recent.setId(record.getId());
                    recent.setPetName("Pet Name"); // TODO: Get from pet service
                    recent.setDiagnosis(record.getDiagnosis());
                    recent.setType(record.getType().name());
                    recent.setRecordDate(record.getRecordDate());
                    return recent;
                })
                .collect(java.util.stream.Collectors.toList());
    }
    
    private List<VeterinarianDashboardResponse.UpcomingAppointment> getUpcomingAppointmentsList(Long vetId) {
        List<PetOwnerAppointment> appointments = appointmentRepository.findUpcomingAppointments(vetId, LocalDateTime.now());
        
        return appointments.stream()
                .limit(7) // Next 7 appointments
                .map(appointment -> {
                    VeterinarianDashboardResponse.UpcomingAppointment upcoming = new VeterinarianDashboardResponse.UpcomingAppointment();
                    upcoming.setId(appointment.getId());
                    upcoming.setPetName("Pet Name"); // TODO: Get from pet service
                    upcoming.setOwnerName("Owner Name"); // TODO: Get from owner service
                    upcoming.setType(appointment.getType());
                    upcoming.setAppointmentDateTime(appointment.getAppointmentDateTime());
                    upcoming.setLocation("Clinic Location"); // TODO: Get from vet profile
                    return upcoming;
                })
                .collect(java.util.stream.Collectors.toList());
    }
}
