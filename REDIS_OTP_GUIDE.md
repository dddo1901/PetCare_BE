# PetCare Redis OTP System - Hướng dẫn

## Cài đặt Redis

### Windows:
1. Tải Redis từ: https://github.com/microsoftarchive/redis/releases
2. Giải nén và chạy: `redis-server.exe`
3. Mặc định chạy ở port 6379

### Hoặc sử dụng Docker:
```bash
docker run -d -p 6379:6379 --name redis redis:latest
```

## Cấu hình Redis trong application.properties

```properties
# Redis Configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.timeout=60000
spring.data.redis.database=0
```

## Cách hoạt động của Redis OTP System

### 1. **Lưu trữ OTP:**
- **Key format:** `otp:{purpose}:{email}`
- **Value:** Mã OTP 6 số
- **TTL:** 10 phút (600 seconds)

**Ví dụ:**
```
Key: "otp:registration:user@example.com"
Value: "123456"
TTL: 600 seconds
```

### 2. **Purpose types:**
- `REGISTRATION` - OTP cho đăng ký tài khoản
- `LOGIN_VERIFICATION` - OTP cho xác thực đăng nhập
- `PASSWORD_RESET` - Token cho reset password

### 3. **Reset Token:**
- **Key format:** `reset_token:{email}`
- **Value:** UUID string
- **TTL:** 1 giờ (3600 seconds)

## API Endpoints

### **Đăng ký và xác nhận OTP:**

1. **Đăng ký:** `POST /api/auth/register`
2. **Xác nhận OTP:** `POST /api/auth/verify-otp`
3. **Gửi lại OTP:** `POST /api/auth/resend-otp?email=user@example.com`

### **Redis Test APIs:**

1. **Generate OTP:** 
   ```
   POST /api/redis/generate-otp?email=test@example.com&purpose=REGISTRATION
   ```

2. **Verify OTP:** 
   ```
   POST /api/redis/verify-otp?email=test@example.com&otp=123456&purpose=REGISTRATION
   ```

3. **Check TTL:** 
   ```
   GET /api/redis/otp-ttl?email=test@example.com&purpose=REGISTRATION
   ```

4. **Delete OTP:** 
   ```
   DELETE /api/redis/delete-otp?email=test@example.com&purpose=REGISTRATION
   ```

## Ưu điểm của Redis OTP System

### **So với Database:**
- ✅ **Hiệu suất cao:** Redis trong memory nên truy xuất nhanh hơn
- ✅ **Auto-expire:** Tự động xóa OTP hết hạn
- ✅ **Giảm tải Database:** Không làm ảnh hưởng đến main database
- ✅ **Scalable:** Dễ dàng scale khi có nhiều user

### **Bảo mật:**
- ✅ **TTL tự động:** OTP tự xóa sau 10 phút
- ✅ **One-time use:** OTP bị xóa ngay sau khi sử dụng
- ✅ **Isolated storage:** Tách biệt với user data chính

## Database Schema Changes

### **User Entity (sau khi chuyển Redis):**
```java
@Entity
public class User {
    // Removed fields:
    // private String otpCode;
    // private LocalDateTime otpExpiry;
    // private String resetPasswordToken;
    // private LocalDateTime resetPasswordTokenExpiry;
    
    // These are now stored in Redis
}
```

## Redis CLI Commands để debug

```bash
# Connect to Redis
redis-cli

# List all keys
KEYS *

# Get OTP value
GET "otp:registration:user@example.com"

# Check TTL
TTL "otp:registration:user@example.com"

# Delete specific OTP
DEL "otp:registration:user@example.com"

# Get all OTP keys
KEYS "otp:*"
```

## Test Flow

### **1. Đăng ký user:**
```json
POST /api/auth/register
{
  "email": "test@example.com",
  "password": "password123",
  "fullName": "Test User",
  "phoneNumber": "0123456789",
  "role": "PET_OWNER",
  "address": "123 Test Street"
}
```

### **2. Kiểm tra OTP trong Redis:**
```bash
redis-cli
GET "otp:registration:test@example.com"
# Output: "123456" (example)
```

### **3. Xác nhận OTP:**
```json
POST /api/auth/verify-otp
{
  "email": "test@example.com",
  "otpCode": "123456"
}
```

### **4. Kiểm tra OTP đã bị xóa:**
```bash
redis-cli
GET "otp:registration:test@example.com"
# Output: (nil)
```

## Troubleshooting

### **Lỗi connection Redis:**
```
Error: Could not connect to Redis
```
**Giải pháp:** Kiểm tra Redis server đang chạy ở port 6379

### **OTP không tồn tại:**
```
"Invalid or expired OTP!"
```
**Nguyên nhân:** OTP đã hết hạn (>10 phút) hoặc đã được sử dụng

### **Memory usage:**
- Mỗi OTP chỉ tốn ~50 bytes trong Redis
- Với 1 triệu user, chỉ tốn ~50MB RAM
