package TechWiz.auths.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import TechWiz.auths.configs.JwtUtils;
import TechWiz.auths.models.*;
import TechWiz.auths.models.dto.*;
import TechWiz.auths.repositories.*;

import java.util.Optional;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetOwnerProfileRepository petOwnerProfileRepository;

    @Autowired
    private VeterinarianProfileRepository veterinarianProfileRepository;

    @Autowired
    private ShelterProfileRepository shelterProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpService otpService;

    public ApiResponse register(RegisterRequest request) {
        try {
            // Check if user already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                return ApiResponse.error("Email is already registered!");
            }

            // Create new user account
            User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getFullName(),
                request.getPhoneNumber(),
                request.getRole()
            );

            // Save user first
            User savedUser = userRepository.save(user);

            // Generate and store OTP in Redis
            String otpCode = otpService.generateAndStoreOtp(
                request.getEmail(), 
                OtpService.Purpose.REGISTRATION
            );

            // Create role-specific profile
            createRoleSpecificProfile(savedUser, request);

            // Send OTP email
            emailService.sendOtpEmail(user.getEmail(), user.getFullName(), otpCode);

            return ApiResponse.success("Registration successful! Please check your email for OTP verification.");

        } catch (Exception e) {
            return ApiResponse.error("Registration failed: " + e.getMessage());
        }
    }

    public ApiResponse login(LoginRequest request) {
        try {
            Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
            
            if (userOptional.isEmpty()) {
                return ApiResponse.error("Invalid email or password!");
            }

            User user = userOptional.get();

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return ApiResponse.error("Invalid email or password!");
            }

            if (!user.getIsEmailVerified()) {
                // Generate new OTP and store in Redis
                String otpCode = otpService.generateAndStoreOtp(
                    user.getEmail(), 
                    OtpService.Purpose.LOGIN_VERIFICATION
                );
                
                emailService.sendOtpEmail(user.getEmail(), user.getFullName(), otpCode);
                return ApiResponse.error("Please verify your email first. New OTP sent to your email.");
            }

            if (!user.getIsActive()) {
                return ApiResponse.error("Account is deactivated. Please contact admin.");
            }

            // Generate JWT token
            String jwt = jwtUtils.generateJwtToken(user.getEmail(), user.getId(), user.getRole().name());

            AuthResponse authResponse = new AuthResponse(
                jwt,
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                user.getIsEmailVerified()
            );

            return ApiResponse.success("Login successful!", authResponse);

        } catch (Exception e) {
            return ApiResponse.error("Login failed: " + e.getMessage());
        }
    }

    public ApiResponse verifyOtp(VerifyOtpRequest request) {
        try {
            // Find user by email
            Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
            
            if (userOptional.isEmpty()) {
                return ApiResponse.error("User not found!");
            }

            User user = userOptional.get();

            // Verify OTP using Redis
            boolean isValidOtp = otpService.verifyOtp(
                request.getEmail(), 
                request.getOtpCode(), 
                OtpService.Purpose.REGISTRATION
            );

            if (!isValidOtp) {
                // Try login verification purpose as fallback
                isValidOtp = otpService.verifyOtp(
                    request.getEmail(), 
                    request.getOtpCode(), 
                    OtpService.Purpose.LOGIN_VERIFICATION
                );
            }

            if (!isValidOtp) {
                return ApiResponse.error("Invalid or expired OTP!");
            }

            // Verify user
            user.setIsEmailVerified(true);
            user.setIsActive(true);
            userRepository.save(user);

            // Send welcome email
            emailService.sendWelcomeEmail(user.getEmail(), user.getFullName(), user.getRole().name());

            // Generate JWT token
            String jwt = jwtUtils.generateJwtToken(user.getEmail(), user.getId(), user.getRole().name());

            AuthResponse authResponse = new AuthResponse(
                jwt,
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                user.getIsEmailVerified()
            );

            return ApiResponse.success("Email verified successfully!", authResponse);

        } catch (Exception e) {
            return ApiResponse.error("OTP verification failed: " + e.getMessage());
        }
    }

    public ApiResponse resendOtp(String email) {
        try {
            Optional<User> userOptional = userRepository.findByEmail(email);
            
            if (userOptional.isEmpty()) {
                return ApiResponse.error("User not found!");
            }

            User user = userOptional.get();

            if (user.getIsEmailVerified()) {
                return ApiResponse.error("Email is already verified!");
            }

            // Generate new OTP and store in Redis
            String otpCode = otpService.generateAndStoreOtp(
                email, 
                OtpService.Purpose.REGISTRATION
            );

            // Send OTP email
            emailService.sendOtpEmail(user.getEmail(), user.getFullName(), otpCode);

            return ApiResponse.success("New OTP sent to your email!");

        } catch (Exception e) {
            return ApiResponse.error("Failed to resend OTP: " + e.getMessage());
        }
    }

    public ApiResponse forgotPassword(ForgotPasswordRequest request) {
        try {
            Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
            
            if (userOptional.isEmpty()) {
                return ApiResponse.error("User not found!");
            }

            User user = userOptional.get();

            if (!user.getIsEmailVerified()) {
                return ApiResponse.error("Please verify your email first!");
            }

            // Generate and store reset token in Redis
            String resetToken = otpService.generateAndStoreResetToken(request.getEmail());

            // Send reset password email
            emailService.sendPasswordResetEmail(user.getEmail(), user.getFullName(), resetToken);

            return ApiResponse.success("Password reset instructions sent to your email!");

        } catch (Exception e) {
            return ApiResponse.error("Failed to process forgot password request: " + e.getMessage());
        }
    }

    public ApiResponse resetPassword(ResetPasswordRequest request) {
        try {
            // Find user by email
            Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
            
            if (userOptional.isEmpty()) {
                return ApiResponse.error("User not found!");
            }

            User user = userOptional.get();

            // Verify reset token using Redis
            boolean isValidToken = otpService.verifyResetToken(
                request.getEmail(), 
                request.getResetToken()
            );

            if (!isValidToken) {
                return ApiResponse.error("Invalid or expired reset token!");
            }

            // Update password
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            return ApiResponse.success("Password reset successfully!");

        } catch (Exception e) {
            return ApiResponse.error("Password reset failed: " + e.getMessage());
        }
    }

    private void createRoleSpecificProfile(User user, RegisterRequest request) {
        switch (user.getRole()) {
            case PET_OWNER:
                createPetOwnerProfile(user, request);
                break;

            case VETERINARIAN:
                createVeterinarianProfile(user, request);
                break;

            case SHELTER:
                createShelterProfile(user, request);
                break;

            case ADMIN:
                // Admin users don't need additional profiles
                break;
        }
    }

    private void createPetOwnerProfile(User user, RegisterRequest request) {
        PetOwnerProfile profile = new PetOwnerProfile();
        profile.setUser(user);
        profile.setAddress(request.getAddress());
        profile.setEmergencyContactName(request.getEmergencyContactName());
        profile.setEmergencyContactPhone(request.getEmergencyContactPhone());
        profile.setProfileImageUrl(request.getProfileImageUrl());
        profile.setBio(request.getBio());
        profile.setAllowAccountSharing(request.getAllowAccountSharing() != null ? 
            request.getAllowAccountSharing() : false);
        
        petOwnerProfileRepository.save(profile);
    }

    private void createVeterinarianProfile(User user, RegisterRequest request) {
        VeterinarianProfile profile = new VeterinarianProfile();
        profile.setUser(user);
        profile.setAddress(request.getAddress());
        profile.setLicenseNumber(request.getLicenseNumber());
        profile.setExperienceYears(request.getExperienceYears());
        profile.setSpecializations(request.getSpecializations());
        profile.setClinicName(request.getClinicName());
        profile.setClinicAddress(request.getClinicAddress());
        profile.setAvailableFromTime(request.getAvailableFromTime());
        profile.setAvailableToTime(request.getAvailableToTime());
        profile.setAvailableDays(request.getAvailableDays());
        profile.setProfileImageUrl(request.getProfileImageUrl());
        profile.setBio(request.getBio());
        profile.setConsultationFee(request.getConsultationFee());
        profile.setIsAvailableForEmergency(request.getIsAvailableForEmergency() != null ? 
            request.getIsAvailableForEmergency() : false);
        
        // Check if profile is complete
        boolean isComplete = profile.getLicenseNumber() != null && 
                           profile.getSpecializations() != null && 
                           !profile.getSpecializations().isEmpty() &&
                           profile.getAvailableFromTime() != null &&
                           profile.getAvailableToTime() != null;
        profile.setIsProfileComplete(isComplete);
        
        veterinarianProfileRepository.save(profile);
    }

    private void createShelterProfile(User user, RegisterRequest request) {
        ShelterProfile profile = new ShelterProfile();
        profile.setUser(user);
        profile.setAddress(request.getAddress());
        profile.setShelterName(request.getShelterName());
        profile.setContactPersonName(request.getContactPersonName());
        profile.setRegistrationNumber(request.getRegistrationNumber());
        profile.setWebsite(request.getWebsite());
        profile.setDescription(request.getDescription());
        profile.setCapacity(request.getCapacity());
        profile.setCurrentOccupancy(request.getCurrentOccupancy() != null ? 
            request.getCurrentOccupancy() : 0);
        profile.setProfileImageUrl(request.getProfileImageUrl());
        profile.setImages(request.getImages());
        profile.setIsVerified(false); // Admin will verify later
        profile.setAcceptsDonations(request.getAcceptsDonations() != null ? 
            request.getAcceptsDonations() : false);
        profile.setOperatingHours(request.getOperatingHours());
        
        shelterProfileRepository.save(profile);
    }

    // generateOTP method removed as it's now handled by OtpService

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}
