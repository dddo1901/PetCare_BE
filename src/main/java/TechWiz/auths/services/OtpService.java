package TechWiz.auths.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // OTP expiry time in minutes
    private static final int OTP_EXPIRY_MINUTES = 10;
    private static final int RESET_TOKEN_EXPIRY_HOURS = 1;

    /**
     * Generate and store OTP for registration/login verification
     */
    public String generateAndStoreOtp(String email, String purpose) {
        String otpCode = generateOTP();
        String key = getOtpKey(email, purpose);
        
        // Store OTP with expiry time
        redisTemplate.opsForValue().set(key, otpCode, OTP_EXPIRY_MINUTES, TimeUnit.MINUTES);
        
        return otpCode;
    }

    /**
     * Verify OTP code
     */
    public boolean verifyOtp(String email, String otpCode, String purpose) {
        String key = getOtpKey(email, purpose);
        String storedOtp = (String) redisTemplate.opsForValue().get(key);
        
        if (storedOtp != null && storedOtp.equals(otpCode)) {
            // Delete OTP after successful verification
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }

    /**
     * Check if OTP exists for email and purpose
     */
    public boolean otpExists(String email, String purpose) {
        String key = getOtpKey(email, purpose);
        return redisTemplate.hasKey(key);
    }

    /**
     * Delete OTP (for cleanup or manual deletion)
     */
    public void deleteOtp(String email, String purpose) {
        String key = getOtpKey(email, purpose);
        redisTemplate.delete(key);
    }

    /**
     * Generate and store reset password token
     */
    public String generateAndStoreResetToken(String email) {
        String resetToken = generateResetToken();
        String key = getResetTokenKey(email);
        
        // Store reset token with expiry time
        redisTemplate.opsForValue().set(key, resetToken, RESET_TOKEN_EXPIRY_HOURS, TimeUnit.HOURS);
        
        return resetToken;
    }

    /**
     * Verify reset password token
     */
    public boolean verifyResetToken(String email, String resetToken) {
        String key = getResetTokenKey(email);
        String storedToken = (String) redisTemplate.opsForValue().get(key);
        
        if (storedToken != null && storedToken.equals(resetToken)) {
            // Delete token after successful verification
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }

    /**
     * Get remaining TTL for OTP in seconds
     */
    public long getOtpTtl(String email, String purpose) {
        String key = getOtpKey(email, purpose);
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    // Private helper methods

    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    private String generateResetToken() {
        return java.util.UUID.randomUUID().toString();
    }

    private String getOtpKey(String email, String purpose) {
        return "otp:" + purpose.toLowerCase() + ":" + email.toLowerCase();
    }

    private String getResetTokenKey(String email) {
        return "reset_token:" + email.toLowerCase();
    }

    // OTP Purpose constants
    public static class Purpose {
        public static final String REGISTRATION = "REGISTRATION";
        public static final String LOGIN_VERIFICATION = "LOGIN_VERIFICATION";
        public static final String PASSWORD_RESET = "PASSWORD_RESET";
    }
}
