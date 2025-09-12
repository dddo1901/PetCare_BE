package TechWiz.admin.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import TechWiz.admin.models.ChatMessage;
import TechWiz.admin.models.SenderRole;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    // Find conversation between two users
    @Query("SELECT c FROM ChatMessage c WHERE " +
           "(c.senderId = :userId1 AND c.receiverId = :userId2) OR " +
           "(c.senderId = :userId2 AND c.receiverId = :userId1) " +
           "ORDER BY c.createdAt ASC")
    List<ChatMessage> findConversationBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    
    // Find messages for a specific user (sent or received)
    @Query("SELECT c FROM ChatMessage c WHERE c.senderId = :userId OR c.receiverId = :userId " +
           "ORDER BY c.createdAt DESC")
    Page<ChatMessage> findMessagesForUser(@Param("userId") Long userId, Pageable pageable);
    
    // Find broadcast messages (receiverId is null)
    List<ChatMessage> findByReceiverIdIsNullOrderByCreatedAtDesc();
    
    // Find unread messages for a user
    @Query("SELECT c FROM ChatMessage c WHERE c.receiverId = :userId AND c.isRead = false " +
           "ORDER BY c.createdAt DESC")
    List<ChatMessage> findUnreadMessagesForUser(@Param("userId") Long userId);
    
    // Mark messages as read
    @Modifying
    @Query("UPDATE ChatMessage c SET c.isRead = true WHERE c.receiverId = :userId AND c.senderId = :senderId")
    void markMessagesAsRead(@Param("userId") Long userId, @Param("senderId") Long senderId);
    
    // Count unread messages for user
    @Query("SELECT COUNT(c) FROM ChatMessage c WHERE c.receiverId = :userId AND c.isRead = false")
    Long countUnreadMessagesForUser(@Param("userId") Long userId);
    
    // Find messages by sender role
    Page<ChatMessage> findBySenderRoleOrderByCreatedAtDesc(SenderRole senderRole, Pageable pageable);
}
