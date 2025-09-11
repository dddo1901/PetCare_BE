# 🧪 TEST APIs PetCare Authentication System

## 🚀 Server Information
- **Base URL:** http://localhost:8080
- **Status:** ✅ Running (đã thấy log "Started PetCareEbApplication")

---

## 📋 Test Checklist

### ✅ 1. Health Check APIs

#### Test Server Health
```bash
curl -X GET http://localhost:8080/api/test/health
```

#### Test Auth Service
```bash
curl -X GET http://localhost:8080/api/auth/test
```

#### Test Available Roles
```bash
curl -X GET http://localhost:8080/api/test/roles
```

---

### ✅ 2. Registration Tests

#### 2.1 Register PET_OWNER (Minimal data)
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "petowner@test.com",
    "password": "password123",
    "fullName": "Nguyen Van A",
    "phoneNumber": "0123456789",
    "role": "PET_OWNER",
    "address": "123 ABC Street, Ho Chi Minh City"
  }'
```

#### 2.2 Register PET_OWNER (Full data)
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "petowner.full@test.com",
    "password": "password123",
    "fullName": "Nguyen Van B",
    "phoneNumber": "0123456780",
    "role": "PET_OWNER",
    "address": "456 XYZ Street, Ho Chi Minh City",
    "profileImageUrl": "https://example.com/avatar.jpg",
    "bio": "Yêu thích nuôi thú cưng, đặc biệt là chó và mèo",
    "emergencyContactName": "Nguyen Thi C",
    "emergencyContactPhone": "0987654321",
    "allowAccountSharing": true
  }'
```

#### 2.3 Register VETERINARIAN
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "vet@test.com",
    "password": "password123",
    "fullName": "Dr. Tran Van D",
    "phoneNumber": "0123456781",
    "role": "VETERINARIAN",
    "address": "789 Vet Street, Ho Chi Minh City",
    "licenseNumber": "VET123456",
    "experienceYears": 10,
    "specializations": ["Small Animals", "Surgery", "Emergency Care"],
    "clinicName": "Pet Care Clinic",
    "clinicAddress": "101 Clinic Street, District 1",
    "availableFromTime": "08:00:00",
    "availableToTime": "17:00:00",
    "availableDays": ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"],
    "consultationFee": 200000.0,
    "isAvailableForEmergency": true,
    "profileImageUrl": "https://example.com/doctor.jpg",
    "bio": "Bác sĩ thú y với 10 năm kinh nghiệm"
  }'
```

#### 2.4 Register SHELTER
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "shelter@test.com",
    "password": "password123",
    "fullName": "Le Thi E",
    "phoneNumber": "0123456782",
    "role": "SHELTER",
    "address": "202 Shelter Street, Ho Chi Minh City",
    "shelterName": "Happy Pet Shelter",
    "contactPersonName": "Le Thi E",
    "registrationNumber": "SHELTER2024001",
    "website": "https://happypetshelter.com",
    "description": "Trại cứu hộ chuyên chăm sóc chó mèo bị bỏ rơi",
    "capacity": 100,
    "currentOccupancy": 45,
    "images": ["https://example.com/shelter1.jpg", "https://example.com/shelter2.jpg"],
    "acceptsDonations": true,
    "operatingHours": "8:00 AM - 6:00 PM",
    "profileImageUrl": "https://example.com/shelter.jpg",
    "bio": "Quản lý trại cứu hộ động vật"
  }'
```

#### 2.5 Register ADMIN
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin.test@test.com",
    "password": "password123",
    "fullName": "Admin Test",
    "phoneNumber": "0123456783",
    "role": "ADMIN",
    "address": "Admin Office Address"
  }'
```

---

### ✅ 3. OTP Testing (Redis)

#### 3.1 Check OTP in Redis (if Redis CLI available)
```bash
# Connect to Redis and check OTP
redis-cli
KEYS "otp:*"
GET "otp:registration:petowner@test.com"
TTL "otp:registration:petowner@test.com"
```

#### 3.2 Generate Test OTP (Redis Test API)
```bash
curl -X POST "http://localhost:8080/api/redis/generate-otp?email=test@example.com&purpose=REGISTRATION"
```

#### 3.3 Verify Test OTP
```bash
curl -X POST "http://localhost:8080/api/redis/verify-otp?email=test@example.com&otp=123456&purpose=REGISTRATION"
```

#### 3.4 Check OTP TTL
```bash
curl -X GET "http://localhost:8080/api/redis/otp-ttl?email=test@example.com&purpose=REGISTRATION"
```

---

### ✅ 4. OTP Verification

#### 4.1 Verify OTP for PET_OWNER
```bash
curl -X POST http://localhost:8080/api/auth/verify-otp \
  -H "Content-Type: application/json" \
  -d '{
    "email": "petowner@test.com",
    "otpCode": "REPLACE_WITH_ACTUAL_OTP"
  }'
```

#### 4.2 Resend OTP
```bash
curl -X POST "http://localhost:8080/api/auth/resend-otp?email=petowner@test.com"
```

---

### ✅ 5. Login Tests

#### 5.1 Login PET_OWNER
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "petowner@test.com",
    "password": "password123"
  }'
```

#### 5.2 Login VETERINARIAN
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "vet@test.com",
    "password": "password123"
  }'
```

---

### ✅ 6. Profile Tests (Requires JWT Token)

#### 6.1 Get PET_OWNER Profile
```bash
curl -X GET http://localhost:8080/api/user/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

#### 6.2 Get VETERINARIAN Profile
```bash
curl -X GET http://localhost:8080/api/user/profile \
  -H "Authorization: Bearer YOUR_VET_JWT_TOKEN_HERE"
```

---

### ✅ 7. Password Reset Tests

#### 7.1 Forgot Password
```bash
curl -X POST http://localhost:8080/api/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "petowner@test.com"
  }'
```

#### 7.2 Reset Password
```bash
curl -X POST http://localhost:8080/api/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "petowner@test.com",
    "resetToken": "RESET_TOKEN_FROM_EMAIL",
    "newPassword": "newpassword123"
  }'
```

---

### ✅ 8. Validation Tests (Should return errors)

#### 8.1 Register without required fields
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "invalid@test.com",
    "password": "password123"
  }'
```

#### 8.2 Register VETERINARIAN without license
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "vet.invalid@test.com",
    "password": "password123",
    "fullName": "Dr. Invalid",
    "phoneNumber": "0123456789",
    "role": "VETERINARIAN",
    "address": "Test Address"
  }'
```

#### 8.3 Register SHELTER without shelter name
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "shelter.invalid@test.com",
    "password": "password123",
    "fullName": "Shelter Invalid",
    "phoneNumber": "0123456789",
    "role": "SHELTER",
    "address": "Test Address"
  }'
```

---

## 📝 Expected Responses

### ✅ Successful Registration
```json
{
  "success": true,
  "message": "Registration successful! Please check your email for OTP verification.",
  "data": null
}
```

### ✅ Successful OTP Verification
```json
{
  "success": true,
  "message": "Email verified successfully!",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "userId": 1,
    "email": "petowner@test.com",
    "fullName": "Nguyen Van A",
    "role": "PET_OWNER",
    "isEmailVerified": true
  }
}
```

### ✅ Profile Response (PET_OWNER)
```json
{
  "success": true,
  "message": "User profile retrieved successfully",
  "data": {
    "userId": 1,
    "email": "petowner@test.com",
    "fullName": "Nguyen Van A",
    "role": "PET_OWNER",
    "isActive": true,
    "isEmailVerified": true,
    "profile": {
      "profileId": 1,
      "address": "123 ABC Street, Ho Chi Minh City",
      "emergencyContactName": "Nguyen Thi C",
      "emergencyContactPhone": "0987654321",
      "profileImageUrl": "https://example.com/avatar.jpg",
      "bio": "Yêu thích nuôi thú cưng",
      "allowAccountSharing": true
    }
  }
}
```

---

## 🔧 Troubleshooting

### If Redis is not running:
```bash
# Windows
redis-server.exe

# Docker
docker run -d -p 6379:6379 --name redis redis:latest
```

### If OTP not received:
- Check email configuration in application.properties
- Check Redis connection
- Use Redis test APIs to debug

### Database issues:
- The datetime warning is not critical, app is running fine
- To fix: Clean database or set proper default values
