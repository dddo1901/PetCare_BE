package TechWiz.veterinarian.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import TechWiz.petOwner.models.PetOwnerNotification;
import TechWiz.petOwner.repositories.PetOwnerNotificationRepository;

import java.time.LocalDateTime;

@Service
public class NotificationService {

    @Autowired
    private PetOwnerNotificationRepository notificationRepository;

    public void createAppointmentNotification(Long ownerId, String title, String message, String type) {
        try {
            PetOwnerNotification notification = new PetOwnerNotification();
            notification.setOwnerId(ownerId);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setType(type);
            notification.setIsRead(false);
            notification.setCreatedAt(LocalDateTime.now());
            
            notificationRepository.save(notification);
            System.out.println("Notification created for owner: " + ownerId);
        } catch (Exception e) {
            System.err.println("Failed to create notification for owner: " + ownerId + ", Error: " + e.getMessage());
        }
    }
}
