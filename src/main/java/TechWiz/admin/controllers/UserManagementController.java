package TechWiz.admin.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import TechWiz.admin.models.dto.UpdateUserStatusRequest;
import TechWiz.admin.services.UserManagementService;
import TechWiz.auths.models.Role;
import TechWiz.auths.models.dto.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserManagementController {
    
    @Autowired
    private UserManagementService userManagementService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse response = userManagementService.getAllUsers(page, size);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getUsersByRole(
            @PathVariable Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse response = userManagementService.getUsersByRole(role, page, size);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long userId) {
        ApiResponse response = userManagementService.getUserById(userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ApiResponse response = userManagementService.searchUsers(keyword, page, size);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateUserStatus(
            @Valid @RequestBody UpdateUserStatusRequest request,
            HttpServletRequest httpRequest) {
        
        // Get current user info from JWT filter
        Long currentUserId = (Long) httpRequest.getAttribute("userId");
        
        // Prevent admin from deactivating themselves
        if (request.getUserId().equals(currentUserId) && !request.getIsActive()) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error("You cannot deactivate your own account!")
            );
        }
        
        ApiResponse response = userManagementService.updateUserStatus(request);
        return ResponseEntity.ok(response);
    }
}
