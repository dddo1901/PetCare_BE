package TechWiz.veterinarian.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import TechWiz.petOwner.models.PetOwnerHealthRecord;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service("veterinarianEmailService")
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendAppointmentNotification(String toEmail, String ownerName, String petName, 
                                          String appointmentType, String appointmentDate, 
                                          String appointmentTime, String location, String action) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("[FurShield] Appointment " + action + " - " + petName);
            
            String body = buildEmailBody(ownerName, petName, appointmentType, appointmentDate, appointmentTime, location, action);
            message.setText(body);
            
            mailSender.send(message);
            System.out.println("Email sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send email to: " + toEmail + ", Error: " + e.getMessage());
        }
    }

    private String buildEmailBody(String ownerName, String petName, String appointmentType, 
                                String appointmentDate, String appointmentTime, String location, String action) {
        StringBuilder body = new StringBuilder();
        body.append("Dear ").append(ownerName).append(",\n\n");
        
        switch (action.toLowerCase()) {
            case "approved":
                body.append("Great news! Your appointment request has been APPROVED by our veterinarian.\n\n");
                break;
            case "rejected":
                body.append("We regret to inform you that your appointment request has been REJECTED.\n\n");
                break;
            case "rescheduled":
                body.append("Your appointment has been RESCHEDULED by our veterinarian.\n\n");
                break;
            default:
                body.append("Your appointment status has been updated.\n\n");
        }
        
        body.append("Appointment Details:\n");
        body.append("• Pet: ").append(petName).append("\n");
        body.append("• Type: ").append(appointmentType).append("\n");
        body.append("• Date: ").append(appointmentDate).append("\n");
        body.append("• Time: ").append(appointmentTime).append("\n");
        body.append("• Location: ").append(location).append("\n\n");
        
        if (action.toLowerCase().equals("approved")) {
            body.append("Please arrive 10 minutes before your scheduled time.\n");
            body.append("If you need to reschedule, please contact us at least 24 hours in advance.\n\n");
        } else if (action.toLowerCase().equals("rejected")) {
            body.append("Please contact us to discuss alternative arrangements.\n\n");
        } else if (action.toLowerCase().equals("rescheduled")) {
            body.append("Please confirm your availability for the new time.\n\n");
        }
        
        body.append("Thank you for choosing FurShield Pet Care!\n\n");
        body.append("Best regards,\n");
        body.append("FurShield Team");
        
        return body.toString();
    }
    
    public void sendNewAppointmentNotificationToVet(String vetEmail, String vetName, String ownerName, 
                                                   String petName, String appointmentType, 
                                                   String appointmentDate, String appointmentTime, 
                                                   String location, String reason) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(vetEmail);
            message.setSubject("[FurShield] New Appointment Request - " + petName);
            
            String body = buildVetEmailBody(vetName, ownerName, petName, appointmentType, 
                                          appointmentDate, appointmentTime, location, reason);
            message.setText(body);
            
            mailSender.send(message);
            System.out.println("New appointment email sent successfully to vet: " + vetEmail);
        } catch (Exception e) {
            System.err.println("Failed to send new appointment email to vet: " + vetEmail + ", Error: " + e.getMessage());
        }
    }
    
    private String buildVetEmailBody(String vetName, String ownerName, String petName, 
                                   String appointmentType, String appointmentDate, 
                                   String appointmentTime, String location, String reason) {
        StringBuilder body = new StringBuilder();
        body.append("Dear Dr. ").append(vetName).append(",\n\n");
        body.append("You have received a NEW APPOINTMENT REQUEST from a pet owner.\n\n");
        
        body.append("📋 APPOINTMENT DETAILS:\n");
        body.append("• Pet Owner: ").append(ownerName).append("\n");
        body.append("• Pet Name: ").append(petName).append("\n");
        body.append("• Appointment Type: ").append(appointmentType).append("\n");
        body.append("• Date: ").append(appointmentDate).append("\n");
        body.append("• Time: ").append(appointmentTime).append("\n");
        body.append("• Location: ").append(location).append("\n");
        
        if (reason != null && !reason.trim().isEmpty()) {
            body.append("• Reason: ").append(reason).append("\n");
        }
        
        body.append("\n");
        body.append("🔔 ACTION REQUIRED:\n");
        body.append("Please log in to your FurShield dashboard to:\n");
        body.append("• Review the appointment details\n");
        body.append("• Approve or reject the request\n");
        body.append("• Add any notes or reschedule if needed\n\n");
        
        body.append("⏰ IMPORTANT:\n");
        body.append("Please respond to this appointment request within 24 hours.\n\n");
        
        body.append("Thank you for using FurShield Pet Care!\n\n");
        body.append("Best regards,\n");
        body.append("FurShield Team");
        
        return body.toString();
    }
    
    // Send new appointment notification to vet with health records
    public void sendNewAppointmentNotificationToVetWithHealthRecords(String vetEmail, String vetName, String ownerName,
                                                                   String petName, String appointmentType,
                                                                   String appointmentDate, String appointmentTime,
                                                                   String location, String reason, List<PetOwnerHealthRecord> healthRecords) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(vetEmail);
            message.setSubject("🐾 New Appointment Request - " + petName + " (" + appointmentType + ")");
            message.setText(buildVetEmailBodyWithHealthRecords(vetName, ownerName, petName, appointmentType, 
                                                              appointmentDate, appointmentTime, location, reason, healthRecords));
            
            mailSender.send(message);
            System.out.println("Email with health records sent successfully to vet: " + vetEmail);
        } catch (Exception e) {
            System.err.println("Error sending email with health records to vet: " + e.getMessage());
        }
    }
    
    // Build email body with health records for vet
    private String buildVetEmailBodyWithHealthRecords(String vetName, String ownerName, String petName,
                                                    String appointmentType, String appointmentDate,
                                                    String appointmentTime, String location, String reason,
                                                    List<PetOwnerHealthRecord> healthRecords) {
        StringBuilder emailBody = new StringBuilder();
        
        emailBody.append("Dear Dr. ").append(vetName).append(",\n\n");
        emailBody.append("You have received a new appointment request with detailed health records:\n\n");
        
        // Appointment details
        emailBody.append("📅 APPOINTMENT DETAILS:\n");
        emailBody.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        emailBody.append("👤 Owner: ").append(ownerName).append("\n");
        emailBody.append("🐾 Pet: ").append(petName).append("\n");
        emailBody.append("🏥 Type: ").append(appointmentType).append("\n");
        emailBody.append("📅 Date: ").append(appointmentDate).append("\n");
        emailBody.append("⏰ Time: ").append(appointmentTime).append("\n");
        emailBody.append("📍 Location: ").append(location).append("\n");
        if (reason != null && !reason.trim().isEmpty()) {
            emailBody.append("📝 Reason: ").append(reason).append("\n");
        }
        emailBody.append("\n");
        
        // Health records section
        if (healthRecords != null && !healthRecords.isEmpty()) {
            emailBody.append("🏥 PET HEALTH RECORDS:\n");
            emailBody.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            
            for (int i = 0; i < healthRecords.size(); i++) {
                PetOwnerHealthRecord record = healthRecords.get(i);
                emailBody.append("\n").append(i + 1).append(". ").append(record.getType()).append("\n");
                emailBody.append("   📅 Date: ").append(record.getRecordDate().format(formatter)).append("\n");
                if (record.getDescription() != null && !record.getDescription().trim().isEmpty()) {
                    emailBody.append("   📋 Description: ").append(record.getDescription()).append("\n");
                }
                if (record.getVet() != null && !record.getVet().trim().isEmpty()) {
                    emailBody.append("   👨‍⚕️ Vet: ").append(record.getVet()).append("\n");
                }
                if (record.getClinic() != null && !record.getClinic().trim().isEmpty()) {
                    emailBody.append("   🏥 Clinic: ").append(record.getClinic()).append("\n");
                }
                if (record.getCost() != null) {
                    emailBody.append("   💰 Cost: $").append(record.getCost()).append("\n");
                }
                if (record.getNotes() != null && !record.getNotes().trim().isEmpty()) {
                    emailBody.append("   📝 Notes: ").append(record.getNotes()).append("\n");
                }
                emailBody.append("\n");
            }
        } else {
            emailBody.append("🏥 PET HEALTH RECORDS:\n");
            emailBody.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            emailBody.append("No health records available for this pet.\n\n");
        }
        
        emailBody.append("Please review the appointment details and health records before confirming.\n\n");
        emailBody.append("Best regards,\n");
        emailBody.append("FurShield Pet Care System");
        
        return emailBody.toString();
    }
}
