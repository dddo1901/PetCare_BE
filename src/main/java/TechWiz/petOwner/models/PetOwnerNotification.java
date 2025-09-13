package TechWiz.petOwner.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pet_owner_notifications")
public class PetOwnerNotification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;
    
    @Column(name = "title", nullable = false, length = 255)
    private String title;
    
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "type", length = 50)
    private String type;
    
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    // Constructors
    public PetOwnerNotification() {}
    
    public PetOwnerNotification(Long ownerId, String title, String message, String type) {
        this.ownerId = ownerId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public Boolean getIsRead() {
        return isRead;
    }
    
    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
