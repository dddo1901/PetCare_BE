# PetCare Registration Examples - Tạo tài khoản với profile đầy đủ

## 1. Đăng ký PET OWNER

```json
POST /api/auth/register
{
  "email": "petowner@example.com",
  "password": "password123",
  "fullName": "Nguyen Van A",
  "phoneNumber": "0123456789",
  "role": "PET_OWNER",
  "address": "123 ABC Street, Ho Chi Minh City",
  "profileImageUrl": "https://example.com/avatar.jpg",
  "bio": "Yêu thích nuôi thú cưng, đặc biệt là chó và mèo",
  "emergencyContactName": "Nguyen Thi B",
  "emergencyContactPhone": "0987654321",
  "allowAccountSharing": true
}
```

**Response sau khi đăng ký thành công:**
```json
{
  "success": true,
  "message": "Registration successful! Please check your email for OTP verification.",
  "data": null
}
```

## 2. Đăng ký VETERINARIAN

```json
POST /api/auth/register
{
  "email": "vet@example.com",
  "password": "password123",
  "fullName": "Dr. Tran Van C",
  "phoneNumber": "0123456789",
  "role": "VETERINARIAN",
  "address": "456 XYZ Street, Ho Chi Minh City",
  "profileImageUrl": "https://example.com/doctor.jpg",
  "bio": "Bác sĩ thú y với 10 năm kinh nghiệm",
  "licenseNumber": "VET123456",
  "experienceYears": 10,
  "specializations": ["Small Animals", "Surgery", "Emergency Care"],
  "clinicName": "Pet Care Clinic",
  "clinicAddress": "789 Pet Street, District 1",
  "availableFromTime": "08:00:00",
  "availableToTime": "17:00:00",
  "availableDays": ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"],
  "consultationFee": 200000.0,
  "isAvailableForEmergency": true
}
```

## 3. Đăng ký SHELTER

```json
POST /api/auth/register
{
  "email": "shelter@example.com",
  "password": "password123",
  "fullName": "Le Thi D",
  "phoneNumber": "0123456789",
  "role": "SHELTER",
  "address": "789 Shelter Street, Ho Chi Minh City",
  "profileImageUrl": "https://example.com/shelter.jpg",
  "bio": "Quản lý trại cứu hộ động vật",
  "shelterName": "Happy Pet Shelter",
  "contactPersonName": "Le Thi D",
  "registrationNumber": "SHELTER2024001",
  "website": "https://happypetshelter.com",
  "description": "Trại cứu hộ chuyên chăm sóc chó mèo bị bỏ rơi",
  "capacity": 100,
  "currentOccupancy": 45,
  "images": ["https://example.com/shelter1.jpg", "https://example.com/shelter2.jpg"],
  "acceptsDonations": true,
  "operatingHours": "8:00 AM - 6:00 PM"
}
```

## 4. Đăng ký ADMIN

```json
POST /api/auth/register
{
  "email": "admin@example.com",
  "password": "password123",
  "fullName": "Admin User",
  "phoneNumber": "0123456789",
  "role": "ADMIN",
  "address": "Admin Office Address"
}
```

## 5. Verify OTP cho tất cả role

```json
POST /api/auth/verify-otp
{
  "email": "petowner@example.com",  // Thay email tương ứng
  "otpCode": "123456"               // OTP nhận được qua email
}
```

**Response sau khi verify thành công:**
```json
{
  "success": true,
  "message": "Email verified successfully!",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "userId": 1,
    "email": "petowner@example.com",
    "fullName": "Nguyen Van A",
    "role": "PET_OWNER",
    "isEmailVerified": true
  }
}
```

## 6. Lấy thông tin profile đầy đủ

```json
GET /api/user/profile
Headers: {
  "Authorization": "Bearer eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response cho PET_OWNER:**
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
      "emergencyContactName": "Nguyen Thi B",
      "emergencyContactPhone": "0987654321",
      "profileImageUrl": "https://example.com/avatar.jpg",
      "bio": "Yêu thích nuôi thú cưng, đặc biệt là chó và mèo",
      "allowAccountSharing": true,
      "createdAt": "2025-09-11T10:30:00",
      "updatedAt": "2025-09-11T10:30:00"
    }
  }
}
```

**Response cho VETERINARIAN:**
```json
{
  "success": true,
  "message": "User profile retrieved successfully",
  "data": {
    "userId": 2,
    "email": "vet@example.com",
    "fullName": "Dr. Tran Van C",
    "phoneNumber": "0123456789",
    "role": "VETERINARIAN",
    "isActive": true,
    "isEmailVerified": true,
    "createdAt": "2025-09-11T10:30:00",
    "profile": {
      "profileId": 1,
      "address": "456 XYZ Street, Ho Chi Minh City",
      "licenseNumber": "VET123456",
      "experienceYears": 10,
      "specializations": ["Small Animals", "Surgery", "Emergency Care"],
      "clinicName": "Pet Care Clinic",
      "clinicAddress": "789 Pet Street, District 1",
      "availableFromTime": "08:00:00",
      "availableToTime": "17:00:00",
      "availableDays": ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"],
      "profileImageUrl": "https://example.com/doctor.jpg",
      "bio": "Bác sĩ thú y với 10 năm kinh nghiệm",
      "consultationFee": 200000.0,
      "isAvailableForEmergency": true,
      "isProfileComplete": true,
      "createdAt": "2025-09-11T10:30:00",
      "updatedAt": "2025-09-11T10:30:00"
    }
  }
}
```

## Validation Rules:

### PET_OWNER:
- ✅ `address` là required
- ✅ Nếu có `emergencyContactName` thì phải có `emergencyContactPhone` và ngược lại

### VETERINARIAN:
- ✅ `licenseNumber` là required
- ✅ `experienceYears` phải >= 0
- ✅ `availableFromTime` phải < `availableToTime`
- ✅ `consultationFee` phải >= 0

### SHELTER:
- ✅ `shelterName` là required
- ✅ `contactPersonName` là required
- ✅ `capacity` phải >= 1
- ✅ `currentOccupancy` phải >= 0
- ✅ `currentOccupancy` không được > `capacity`

### Tất cả role:
- ✅ `email`, `password`, `fullName`, `phoneNumber`, `address` là required
- ✅ Email phải valid format
- ✅ Phone number phải đúng format
