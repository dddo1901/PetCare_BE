package TechWiz.admin.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import TechWiz.admin.models.ChatMessage;
import TechWiz.admin.models.SenderRole;
import TechWiz.admin.models.dto.SendMessageRequest;
import TechWiz.admin.repositories.ChatMessageRepository;
import TechWiz.auths.models.User;
import TechWiz.auths.models.dto.ApiResponse;
import TechWiz.auths.repositories.UserRepository;

@Service
@Transactional
public class ChatService {
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public ApiResponse sendMessage(SendMessageRequest request, Long senderId) {
        try {
            Optional<User> senderOptional = userRepository.findById(senderId);
            
            if (senderOptional.isEmpty()) {
                return ApiResponse.error("Sender not found!");
            }
            
            User sender = senderOptional.get();
            
            // Check if receiver exists (if not broadcast message)
            if (request.getReceiverId() != null) {
                Optional<User> receiverOptional = userRepository.findById(request.getReceiverId());
                if (receiverOptional.isEmpty()) {
                    return ApiResponse.error("Receiver not found!");
                }
            }
            
            ChatMessage message = new ChatMessage();
            message.setSenderId(senderId);
            message.setSenderName(sender.getFullName());
            message.setSenderRole(SenderRole.valueOf(sender.getRole().name()));
            message.setReceiverId(request.getReceiverId());
            message.setMessage(request.getMessage());
            message.setIsRead(false);
            message.setCreatedAt(LocalDateTime.now());
            
            ChatMessage savedMessage = chatMessageRepository.save(message);
            
            String messageType = request.getReceiverId() != null ? "Message" : "Broadcast message";
            return ApiResponse.success(messageType + " sent successfully!", savedMessage);
            
        } catch (Exception e) {
            return ApiResponse.error("Failed to send message: " + e.getMessage());
        }
    }
    
    public ApiResponse getConversation(Long userId1, Long userId2) {
        try {
            List<ChatMessage> messages = chatMessageRepository.findConversationBetweenUsers(userId1, userId2);
            
            return ApiResponse.success("Conversation retrieved successfully", messages);
            
        } catch (Exception e) {
            return ApiResponse.error("Failed to retrieve conversation: " + e.getMessage());
        }
    }
    
    public ApiResponse getMessagesForUser(Long userId, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ChatMessage> messages = chatMessageRepository.findMessagesForUser(userId, pageable);
            
            return ApiResponse.success("Messages retrieved successfully", messages.getContent());
            
        } catch (Exception e) {
            return ApiResponse.error("Failed to retrieve messages: " + e.getMessage());
        }
    }
    
    public ApiResponse getBroadcastMessages() {
        try {
            List<ChatMessage> messages = chatMessageRepository.findByReceiverIdIsNullOrderByCreatedAtDesc();
            
            return ApiResponse.success("Broadcast messages retrieved successfully", messages);
            
        } catch (Exception e) {
            return ApiResponse.error("Failed to retrieve broadcast messages: " + e.getMessage());
        }
    }
    
    public ApiResponse getUnreadMessages(Long userId) {
        try {
            List<ChatMessage> messages = chatMessageRepository.findUnreadMessagesForUser(userId);
            
            return ApiResponse.success("Unread messages retrieved successfully", messages);
            
        } catch (Exception e) {
            return ApiResponse.error("Failed to retrieve unread messages: " + e.getMessage());
        }
    }
    
    public ApiResponse markMessagesAsRead(Long userId, Long senderId) {
        try {
            chatMessageRepository.markMessagesAsRead(userId, senderId);
            
            return ApiResponse.success("Messages marked as read!");
            
        } catch (Exception e) {
            return ApiResponse.error("Failed to mark messages as read: " + e.getMessage());
        }
    }
    
    public ApiResponse getUnreadCount(Long userId) {
        try {
            Long count = chatMessageRepository.countUnreadMessagesForUser(userId);
            
            return ApiResponse.success("Unread count retrieved successfully", count);
            
        } catch (Exception e) {
            return ApiResponse.error("Failed to get unread count: " + e.getMessage());
        }
    }
    
    public ApiResponse getMessagesByRole(SenderRole role, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ChatMessage> messages = chatMessageRepository.findBySenderRoleOrderByCreatedAtDesc(role, pageable);
            
            return ApiResponse.success("Messages by role retrieved successfully", messages.getContent());
            
        } catch (Exception e) {
            return ApiResponse.error("Failed to retrieve messages by role: " + e.getMessage());
        }
    }
}
