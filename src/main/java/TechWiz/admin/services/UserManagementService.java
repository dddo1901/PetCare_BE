package TechWiz.admin.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import TechWiz.admin.models.dto.UpdateUserStatusRequest;
import TechWiz.admin.models.dto.UserManagementResponse;
import TechWiz.auths.models.Role;
import TechWiz.auths.models.User;
import TechWiz.auths.models.dto.ApiResponse;
import TechWiz.auths.repositories.UserRepository;

@Service
@Transactional
public class UserManagementService {
    
    @Autowired
    private UserRepository userRepository;
    
    public ApiResponse getAllUsers(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<User> userPage = userRepository.findAllByOrderByCreatedAtDesc(pageable);
            
            List<UserManagementResponse> users = userPage.getContent().stream()
                .map(this::convertToUserManagementResponse)
                .collect(Collectors.toList());
            
            return ApiResponse.success("Users retrieved successfully", users);
            
        } catch (Exception e) {
            return ApiResponse.error("Failed to retrieve users: " + e.getMessage());
        }
    }
    
    public ApiResponse getUsersByRole(Role role, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<User> userPage = userRepository.findByRoleOrderByCreatedAtDesc(role, pageable);
            
            List<UserManagementResponse> users = userPage.getContent().stream()
                .map(this::convertToUserManagementResponse)
                .collect(Collectors.toList());
            
            return ApiResponse.success("Users retrieved successfully", users);
            
        } catch (Exception e) {
            return ApiResponse.error("Failed to retrieve users by role: " + e.getMessage());
        }
    }
    
    public ApiResponse updateUserStatus(UpdateUserStatusRequest request) {
        try {
            Optional<User> userOptional = userRepository.findById(request.getUserId());
            
            if (userOptional.isEmpty()) {
                return ApiResponse.error("User not found!");
            }
            
            User user = userOptional.get();
            
            // Cannot deactivate admin users
            if (user.getRole() == Role.ADMIN) {
                return ApiResponse.error("Cannot modify admin user status!");
            }
            
            user.setIsActive(request.getIsActive());
            userRepository.save(user);
            
            String action = request.getIsActive() ? "activated" : "deactivated";
            return ApiResponse.success("User " + action + " successfully!");
            
        } catch (Exception e) {
            return ApiResponse.error("Failed to update user status: " + e.getMessage());
        }
    }
    
    public ApiResponse getUserById(Long userId) {
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            
            if (userOptional.isEmpty()) {
                return ApiResponse.error("User not found!");
            }
            
            UserManagementResponse userResponse = convertToUserManagementResponse(userOptional.get());
            return ApiResponse.success("User retrieved successfully", userResponse);
            
        } catch (Exception e) {
            return ApiResponse.error("Failed to retrieve user: " + e.getMessage());
        }
    }
    
    public ApiResponse searchUsers(String keyword, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<User> userPage = userRepository.searchUsers(keyword, pageable);
            
            List<UserManagementResponse> users = userPage.getContent().stream()
                .map(this::convertToUserManagementResponse)
                .collect(Collectors.toList());
            
            return ApiResponse.success("Search completed successfully", users);
            
        } catch (Exception e) {
            return ApiResponse.error("Failed to search users: " + e.getMessage());
        }
    }
    
    private UserManagementResponse convertToUserManagementResponse(User user) {
        return new UserManagementResponse(
            user.getId(),
            user.getEmail(),
            user.getFullName(),
            user.getPhoneNumber(),
            user.getRole().name(),
            user.getIsActive(),
            user.getIsEmailVerified(),
            user.getCreatedAt(),
            user.getLastLogin()
        );
    }
}
