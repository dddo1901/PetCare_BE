package TechWiz.admin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import TechWiz.admin.models.SenderRole;
import TechWiz.admin.models.dto.SendMessageRequest;
import TechWiz.admin.services.ChatService;
import TechWiz.auths.models.dto.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/chat")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ChatController {
    
    @Autowired
    private ChatService chatService;
    
    @PostMapping("/send")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> sendMessage(
            @Valid @RequestBody SendMessageRequest request,
            HttpServletRequest httpRequest) {
        
        Long senderId = (Long) httpRequest.getAttribute("userId");
        ApiResponse response = chatService.sendMessage(request, senderId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/conversation/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getConversation(
            @PathVariable Long userId,
            HttpServletRequest httpRequest) {
        
        Long currentUserId = (Long) httpRequest.getAttribute("userId");
        ApiResponse response = chatService.getConversation(currentUserId, userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/messages")
    @PreAuthorize("hasAnyRole('ADMIN', 'PET_OWNER', 'VETERINARIAN', 'SHELTER')")
    public ResponseEntity<ApiResponse> getMessagesForUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        ApiResponse response = chatService.getMessagesForUser(userId, page, size);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/broadcast")
    public ResponseEntity<ApiResponse> getBroadcastMessages() {
        ApiResponse response = chatService.getBroadcastMessages();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/unread")
    @PreAuthorize("hasAnyRole('ADMIN', 'PET_OWNER', 'VETERINARIAN', 'SHELTER')")
    public ResponseEntity<ApiResponse> getUnreadMessages(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        ApiResponse response = chatService.getUnreadMessages(userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/unread/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'PET_OWNER', 'VETERINARIAN', 'SHELTER')")
    public ResponseEntity<ApiResponse> getUnreadCount(HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        ApiResponse response = chatService.getUnreadCount(userId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/read/{senderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PET_OWNER', 'VETERINARIAN', 'SHELTER')")
    public ResponseEntity<ApiResponse> markMessagesAsRead(
            @PathVariable Long senderId,
            HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        ApiResponse response = chatService.markMessagesAsRead(userId, senderId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getMessagesByRole(
            @PathVariable SenderRole role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        ApiResponse response = chatService.getMessagesByRole(role, page, size);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/send-to-user")
    @PreAuthorize("hasAnyRole('ADMIN', 'PET_OWNER', 'VETERINARIAN', 'SHELTER')")
    public ResponseEntity<ApiResponse> sendMessageToUser(
            @Valid @RequestBody SendMessageRequest request,
            HttpServletRequest httpRequest) {
        
        Long senderId = (Long) httpRequest.getAttribute("userId");
        ApiResponse response = chatService.sendMessage(request, senderId);
        return ResponseEntity.ok(response);
    }
}
