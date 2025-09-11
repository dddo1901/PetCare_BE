package TechWiz.auths.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import TechWiz.auths.models.dto.ApiResponse;
import TechWiz.auths.services.OtpService;

@RestController
@RequestMapping("/api/redis")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RedisTestController {

    @Autowired
    private OtpService otpService;

    @PostMapping("/generate-otp")
    public ApiResponse generateTestOtp(@RequestParam String email, @RequestParam String purpose) {
        try {
            String otp = otpService.generateAndStoreOtp(email, purpose);
            return ApiResponse.success("OTP generated successfully!", otp);
        } catch (Exception e) {
            return ApiResponse.error("Failed to generate OTP: " + e.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    public ApiResponse verifyTestOtp(@RequestParam String email, @RequestParam String otp, @RequestParam String purpose) {
        try {
            boolean isValid = otpService.verifyOtp(email, otp, purpose);
            if (isValid) {
                return ApiResponse.success("OTP verified successfully!");
            } else {
                return ApiResponse.error("Invalid or expired OTP!");
            }
        } catch (Exception e) {
            return ApiResponse.error("Failed to verify OTP: " + e.getMessage());
        }
    }

    @GetMapping("/otp-ttl")
    public ApiResponse getOtpTtl(@RequestParam String email, @RequestParam String purpose) {
        try {
            long ttl = otpService.getOtpTtl(email, purpose);
            return ApiResponse.success("OTP TTL retrieved", ttl + " seconds");
        } catch (Exception e) {
            return ApiResponse.error("Failed to get OTP TTL: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete-otp")
    public ApiResponse deleteOtp(@RequestParam String email, @RequestParam String purpose) {
        try {
            otpService.deleteOtp(email, purpose);
            return ApiResponse.success("OTP deleted successfully!");
        } catch (Exception e) {
            return ApiResponse.error("Failed to delete OTP: " + e.getMessage());
        }
    }
}
