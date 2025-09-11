# 🎯 VÍ DỤ TẠO TÀI KHOẢN VÀ VERIFY OTP

## 📋 Quy trình test:
1. **Tạo tài khoản** → Nhận response success
2. **Kiểm tra email** → Lấy mã OTP (6 số)
3. **Verify OTP** → Nhận JWT token
4. **Test JWT token** → Lấy thông tin profile

---

## 🔥 1. PET OWNER - Tạo tài khoản cơ bản

### Step 1: Đăng ký PET OWNER
```json
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "petowner@example.com",
  "password": "password123",
  "fullName": "Nguyen Van A",
  "phoneNumber": "0123456789",
  "role": "PET_OWNER",
  "address": "123 ABC Street, Ho Chi Minh City",
  "bio": "Yêu thích nuôi chó và mèo",
  "profileImageUrl": "https://example.com/avatar1.jpg"
}
```

### Expected Response:
```json
{
  "success": true,
  "message": "Registration successful! Please check your email for OTP verification.",
  "data": null
}
```

### Step 2: Verify OTP cho PET OWNER
```json
POST http://localhost:8080/api/auth/verify-otp
Content-Type: application/json

{
  "email": "petowner@example.com",
  "otpCode": "123456"
}
```

### Expected Response:
```json
{
  "success": true,
  "message": "Email verified successfully!",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwZXRvd25lckBleGFtcGxlLmNvbSIsInVzZXJJZCI6MSwidXNlciI6IlBFVF9PV05FUiIsImlhdCI6MTY5NDQxMjAwMCwiZXhwIjoxNjk0NDk4NDAwfQ...",
    "tokenType": "Bearer",
    "userId": 1,
    "email": "petowner@example.com",
    "fullName": "Nguyen Van A",
    "role": "PET_OWNER",
    "isEmailVerified": true
  }
}
```

---

## 🔥 2. PET OWNER - Tạo tài khoản đầy đủ thông tin

### Step 1: Đăng ký PET OWNER với emergency contact
```json
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "petowner.full@example.com",
  "password": "password123",
  "fullName": "Tran Thi B",
  "phoneNumber": "0987654321",
  "role": "PET_OWNER",
  "address": "456 XYZ Street, Ho Chi Minh City",
  "bio": "Có kinh nghiệm 5 năm nuôi chó Golden Retriever",
  "profileImageUrl": "https://example.com/avatar2.jpg",
  "emergencyContactName": "Tran Van C",
  "emergencyContactPhone": "0123987456",
  "allowAccountSharing": true
}
```

---

## 🔥 3. VETERINARIAN - Bác sĩ thú y

### Step 1: Đăng ký VETERINARIAN
```json
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "vet@example.com",
  "password": "vetpassword123",
  "fullName": "Dr. Le Van D",
  "phoneNumber": "0909123456",
  "role": "VETERINARIAN",
  "address": "789 Medical Street, District 1, Ho Chi Minh City",
  "bio": "Bác sĩ thú y chuyên khoa phẫu thuật với 8 năm kinh nghiệm",
  "profileImageUrl": "https://example.com/doctor1.jpg",
  "licenseNumber": "VET2024001",
  "experienceYears": 8,
  "specializations": ["Small Animals", "Surgery", "Emergency Care", "Dental Care"],
  "clinicName": "Happy Pet Veterinary Clinic",
  "clinicAddress": "101 Pet Care Street, District 3, Ho Chi Minh City",
  "availableFromTime": "08:00:00",
  "availableToTime": "18:00:00",
  "availableDays": ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"],
  "consultationFee": 300000.0,
  "isAvailableForEmergency": true
}
```

### Step 2: Verify OTP cho VETERINARIAN
```json
POST http://localhost:8080/api/auth/verify-otp
Content-Type: application/json

{
  "email": "vet@example.com",
  "otpCode": "654321"
}
```

---

## 🔥 4. SHELTER - Trại cứu hộ động vật

### Step 1: Đăng ký SHELTER
```json
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "shelter@example.com",
  "password": "shelterpass123",
  "fullName": "Pham Thi E",
  "phoneNumber": "0908765432",
  "role": "SHELTER",
  "address": "202 Animal Rescue Street, Binh Thanh District, Ho Chi Minh City",
  "bio": "Quản lý trại cứu hộ với tâm huyết cứu giúp các bé thú cưng bị bỏ rơi",
  "profileImageUrl": "https://example.com/shelter-manager.jpg",
  "shelterName": "Happy Tail Animal Shelter",
  "contactPersonName": "Pham Thi E",
  "registrationNumber": "SHELTER-HCM-2024-001",
  "website": "https://happytailshelter.org",
  "description": "Trại cứu hộ phi lợi nhuận chuyên cứu giúp chó mèo bị bỏ rơi, tìm gia đình mới cho các bé. Hiện tại đang chăm sóc hơn 80 bé chó mèo.",
  "capacity": 150,
  "currentOccupancy": 82,
  "images": [
    "https://example.com/shelter-front.jpg",
    "https://example.com/shelter-dogs.jpg",
    "https://example.com/shelter-cats.jpg"
  ],
  "acceptsDonations": true,
  "operatingHours": "Thứ 2-CN: 8:00 AM - 5:00 PM"
}
```

### Step 2: Verify OTP cho SHELTER
```json
POST http://localhost:8080/api/auth/verify-otp
Content-Type: application/json

{
  "email": "shelter@example.com",
  "otpCode": "789012"
}
```

---

## 🔥 5. ADMIN - Quản trị viên

### Step 1: Đăng ký ADMIN
```json
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "admin@example.com",
  "password": "adminpassword123",
  "fullName": "Nguyen Van Admin",
  "phoneNumber": "0901234567",
  "role": "ADMIN",
  "address": "PetCare Head Office, District 1, Ho Chi Minh City"
}
```

### Step 2: Verify OTP cho ADMIN
```json
POST http://localhost:8080/api/auth/verify-otp
Content-Type: application/json

{
  "email": "admin@example.com",
  "otpCode": "345678"
}
```

---

## 🔥 6. TEST PROFILE SAU KHI VERIFY

### Get Profile với JWT Token
```json
GET http://localhost:8080/api/user/profile
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwZXRvd25lckBleGFtcGxlLmNvbSIsInVzZXJJZCI6MSwidXNlciI6IlBFVF9PV05FUiIsImlhdCI6MTY5NDQxMjAwMCwiZXhwIjoxNjk0NDk4NDAwfQ...
```

### Expected Response cho PET_OWNER:
```json
{
  "success": true,
  "message": "User profile retrieved successfully",
  "data": {
    "userId": 1,
    "email": "petowner@example.com",
    "fullName": "Nguyen Van A",
    "phoneNumber": "0123456789",
    "role": "PET_OWNER",
    "isActive": true,
    "isEmailVerified": true,
    "createdAt": "2025-09-11T10:30:00",
    "profile": {
      "profileId": 1,
      "address": "123 ABC Street, Ho Chi Minh City",
      "emergencyContactName": null,
      "emergencyContactPhone": null,
      "profileImageUrl": "https://example.com/avatar1.jpg",
      "bio": "Yêu thích nuôi chó và mèo",
      "allowAccountSharing": false,
      "createdAt": "2025-09-11T10:30:00",
      "updatedAt": "2025-09-11T10:30:00"
    }
  }
}
```

### Expected Response cho VETERINARIAN:
```json
{
  "success": true,
  "message": "User profile retrieved successfully",
  "data": {
    "userId": 2,
    "email": "vet@example.com",
    "fullName": "Dr. Le Van D",
    "phoneNumber": "0909123456",
    "role": "VETERINARIAN",
    "isActive": true,
    "isEmailVerified": true,
    "createdAt": "2025-09-11T10:35:00",
    "profile": {
      "profileId": 1,
      "address": "789 Medical Street, District 1, Ho Chi Minh City",
      "licenseNumber": "VET2024001",
      "experienceYears": 8,
      "specializations": ["Small Animals", "Surgery", "Emergency Care", "Dental Care"],
      "clinicName": "Happy Pet Veterinary Clinic",
      "clinicAddress": "101 Pet Care Street, District 3, Ho Chi Minh City",
      "availableFromTime": "08:00:00",
      "availableToTime": "18:00:00",
      "availableDays": ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"],
      "profileImageUrl": "https://example.com/doctor1.jpg",
      "bio": "Bác sĩ thú y chuyên khoa phẫu thuật với 8 năm kinh nghiệm",
      "consultationFee": 300000.0,
      "isAvailableForEmergency": true,
      "isProfileComplete": true,
      "createdAt": "2025-09-11T10:35:00",
      "updatedAt": "2025-09-11T10:35:00"
    }
  }
}
```

---

## 🔥 7. TEST CÁC TRƯỜNG HỢP LỖI

### 7.1 Đăng ký thiếu thông tin bắt buộc
```json
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "invalid@example.com",
  "password": "password123",
  "role": "VETERINARIAN"
}
```

### Expected Error Response:
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "fullName": "Full name is required",
    "phoneNumber": "Phone number is required",
    "address": "Address is required",
    "licenseNumber": "License number is required for veterinarians"
  }
}
```

### 7.2 Verify OTP sai
```json
POST http://localhost:8080/api/auth/verify-otp
Content-Type: application/json

{
  "email": "petowner@example.com",
  "otpCode": "000000"
}
```

### Expected Error Response:
```json
{
  "success": false,
  "message": "Invalid or expired OTP!",
  "data": null
}
```

### 7.3 Đăng ký email đã tồn tại
```json
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "petowner@example.com",
  "password": "password123",
  "fullName": "Another User",
  "phoneNumber": "0123456789",
  "role": "PET_OWNER",
  "address": "Some Address"
}
```

### Expected Error Response:
```json
{
  "success": false,
  "message": "Email is already registered!",
  "data": null
}
```

---

## 🔥 8. TEST RESEND OTP

### Gửi lại OTP
```json
POST http://localhost:8080/api/auth/resend-otp?email=petowner@example.com
```

### Expected Response:
```json
{
  "success": true,
  "message": "New OTP sent to your email!",
  "data": null
}
```

---

## 📝 GHI CHÚ QUAN TRỌNG:

### 🔑 Lấy OTP Code:
- **Trong development**: Check email để lấy mã OTP 6 số
- **Hoặc sử dụng Redis CLI**: 
  ```bash
  redis-cli
  KEYS "otp:*"
  GET "otp:registration:petowner@example.com"
  ```

### 🔑 Sử dụng JWT Token:
- Copy token từ response của verify-otp
- Paste vào header Authorization: Bearer {token}
- Token có thời hạn 24 giờ (86400000ms)

### 🔑 Email Template:
- Subject: "PetCare - Email Verification OTP"
- Body chứa mã OTP 6 số
- OTP hết hạn sau 10 phút

### 🔑 Role-specific Validation:
- **PET_OWNER**: Chỉ cần basic info + address
- **VETERINARIAN**: Bắt buộc có licenseNumber
- **SHELTER**: Bắt buộc có shelterName + contactPersonName
- **ADMIN**: Chỉ cần basic info + address
