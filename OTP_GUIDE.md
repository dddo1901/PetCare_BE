# PetCare Authentication System - Hướng dẫn sử dụng OTP

## Quy trình xác nhận OTP khi đăng ký tài khoản

### 1. **Đăng ký tài khoản**
**API:** `POST /api/auth/register`

**Request body:**
```json
{
  "email": "user@example.com",
  "password": "password123",
  "fullName": "Nguyen Van A",
  "phoneNumber": "0123456789",
  "role": "PET_OWNER",
  "address": "123 ABC Street, Ho Chi Minh City"
}
```

**Response thành công:**
```json
{
  "success": true,
  "message": "Registration successful! Please check your email for OTP verification.",
  "data": null
}
```

**Sau khi đăng ký:**
- Hệ thống tạo user với `isEmailVerified = false` và `isActive = false`
- Sinh OTP 6 số (ví dụ: 123456) với thời gian hết hạn 10 phút
- Gửi email chứa OTP đến địa chỉ email của user

### 2. **Xác nhận OTP**
**API:** `POST /api/auth/verify-otp`

**Request body:**
```json
{
  "email": "user@example.com",
  "otpCode": "123456"
}
```

**Response thành công:**
```json
{
  "success": true,
  "message": "Email verified successfully!",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "userId": 1,
    "email": "user@example.com",
    "fullName": "Nguyen Van A",
    "role": "PET_OWNER",
    "isEmailVerified": true
  }
}
```

**Sau khi xác nhận OTP thành công:**
- User được kích hoạt: `isEmailVerified = true`, `isActive = true`
- OTP bị xóa khỏi database
- Gửi email chào mừng đến user
- Trả về JWT token để user có thể đăng nhập ngay

### 3. **Gửi lại OTP (nếu cần)**
**API:** `POST /api/auth/resend-otp?email=user@example.com`

**Response:**
```json
{
  "success": true,
  "message": "New OTP sent to your email!",
  "data": null
}
```

### 4. **Đăng nhập sau khi đã xác nhận**
**API:** `POST /api/auth/login`

**Request body:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

## Các role user và profile tương ứng:

### **PET_OWNER**
- Tạo `PetOwnerProfile` với address
- Có thể quản lý pet, đặt lịch hẹn, mua sản phẩm

### **VETERINARIAN**
- Tạo `VeterinarianProfile` với thông tin bác sĩ
- Fields: address, licenseNumber, clinicName, specializations, v.v.

### **SHELTER**
- Tạo `ShelterProfile` với thông tin shelter
- Fields: shelterName, contactPersonName, registrationNumber, v.v.

### **ADMIN**
- Không tạo profile bổ sung
- Có quyền quản lý toàn bộ hệ thống

## Email Templates:

### **OTP Email:**
- Subject: "PetCare - Email Verification OTP"
- Chứa mã OTP 6 số
- Thời gian hết hạn: 10 phút

### **Welcome Email:**
- Được gửi sau khi xác nhận OTP thành công
- Subject: "Welcome to PetCare!"

### **Password Reset Email:**
- Subject: "PetCare - Password Reset Request"
- Chứa reset token với thời gian hết hạn 1 giờ

## Lỗi thường gặp:

1. **"Invalid OTP or email!"** - OTP không đúng hoặc email không tồn tại
2. **"OTP has expired. Please request a new one."** - OTP đã hết hạn (>10 phút)
3. **"Email is already verified!"** - Tài khoản đã được xác nhận rồi
4. **"User not found!"** - Email không tồn tại trong hệ thống

## Cấu hình Email:

Trong `application.properties`:
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=nthieu18@gmail.com
spring.mail.password=crcg hbpt mxog wnxo  # App Password của Gmail
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

## Test với Postman:

1. **Đăng ký:** POST `/api/auth/register`
2. **Kiểm tra email** nhận được OTP
3. **Xác nhận OTP:** POST `/api/auth/verify-otp`
4. **Đăng nhập:** POST `/api/auth/login` (với JWT token)

## Database Schema:

**Table `users`:**
- `otp_code`: Mã OTP 6 số
- `otp_expiry`: Thời gian hết hạn OTP
- `is_email_verified`: Trạng thái xác nhận email
- `is_active`: Trạng thái kích hoạt tài khoản
