package TechWiz.veterinarian.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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
}
