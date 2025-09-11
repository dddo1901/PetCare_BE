package TechWiz.auths.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    public void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        
        emailSender.send(message);
    }

    public void sendOtpEmail(String to, String fullName, String otpCode) {
        String subject = "PetCare - Email Verification OTP";
        String htmlBody = buildOtpEmailTemplate(fullName, otpCode);
        
        try {
            sendHtmlMessage(to, subject, htmlBody);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    public void sendPasswordResetEmail(String to, String fullName, String resetToken) {
        String subject = "PetCare - Password Reset Request";
        String htmlBody = buildPasswordResetEmailTemplate(fullName, resetToken);
        
        try {
            sendHtmlMessage(to, subject, htmlBody);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    public void sendWelcomeEmail(String to, String fullName, String role) {
        String subject = "Welcome to PetCare!";
        String htmlBody = buildWelcomeEmailTemplate(fullName, role);
        
        try {
            sendHtmlMessage(to, subject, htmlBody);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send welcome email", e);
        }
    }

    private String buildOtpEmailTemplate(String fullName, String otpCode) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .otp-code { font-size: 24px; font-weight: bold; color: #4CAF50; text-align: center; margin: 20px 0; }
                    .footer { text-align: center; padding: 20px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>PetCare Email Verification</h1>
                    </div>
                    <div class="content">
                        <p>Hello %s,</p>
                        <p>Thank you for registering with PetCare! To complete your account verification, please use the following OTP code:</p>
                        <div class="otp-code">%s</div>
                        <p>This OTP will expire in 10 minutes for security reasons.</p>
                        <p>If you didn't create an account with PetCare, please ignore this email.</p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2025 PetCare. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(fullName, otpCode);
    }

    private String buildPasswordResetEmailTemplate(String fullName, String resetToken) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #FF6B6B; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .reset-token { font-size: 18px; font-weight: bold; color: #FF6B6B; text-align: center; margin: 20px 0; }
                    .footer { text-align: center; padding: 20px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>PetCare Password Reset</h1>
                    </div>
                    <div class="content">
                        <p>Hello %s,</p>
                        <p>You requested to reset your password for your PetCare account. Use the following reset token:</p>
                        <div class="reset-token">%s</div>
                        <p>This reset token will expire in 1 hour for security reasons.</p>
                        <p>If you didn't request a password reset, please ignore this email and your password will remain unchanged.</p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2025 PetCare. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(fullName, resetToken);
    }

    private String buildWelcomeEmailTemplate(String fullName, String role) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .role { font-weight: bold; color: #4CAF50; }
                    .footer { text-align: center; padding: 20px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Welcome to PetCare!</h1>
                    </div>
                    <div class="content">
                        <p>Hello %s,</p>
                        <p>Welcome to PetCare! Your account has been successfully verified and activated.</p>
                        <p>You are registered as: <span class="role">%s</span></p>
                        <p>You can now access all the features available for your account type. We're excited to have you join our community of pet lovers!</p>
                        <p>If you have any questions, feel free to contact our support team.</p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2025 PetCare. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(fullName, role);
    }
}
