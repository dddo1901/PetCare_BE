# 📋 POSTMAN TEMPLATES - READY TO COPY & PASTE

## 🎯 COLLECTION: PetCare Authentication

### Base URL: `http://localhost:8080`

---

## 📁 FOLDER 1: Health Check

### ✅ GET Health Check
```
GET {{baseUrl}}/api/test/health
```

### ✅ GET Available Roles
```
GET {{baseUrl}}/api/test/roles
```

---

## 📁 FOLDER 2: Registration

### ✅ POST Register PET_OWNER (Basic)
```
POST {{baseUrl}}/api/auth/register
Content-Type: application/json

{
  "email": "petowner1@test.com",
  "password": "password123",
  "fullName": "Nguyen Van A",
  "phoneNumber": "0123456789",
  "role": "PET_OWNER",
  "address": "123 ABC Street, Ho Chi Minh City"
}
```

### ✅ POST Register PET_OWNER (Full)
```
POST {{baseUrl}}/api/auth/register
Content-Type: application/json

{
  "email": "petowner2@test.com",
  "password": "password123",
  "fullName": "Tran Thi B",
  "phoneNumber": "0987654321",
  "role": "PET_OWNER",
  "address": "456 XYZ Street, Ho Chi Minh City",
  "bio": "Yêu thích nuôi chó Golden Retriever",
  "profileImageUrl": "https://example.com/avatar.jpg",
  "emergencyContactName": "Tran Van C",
  "emergencyContactPhone": "0123987456",
  "allowAccountSharing": true
}
```

### ✅ POST Register VETERINARIAN
```
POST {{baseUrl}}/api/auth/register
Content-Type: application/json

{
  "email": "vet1@test.com",
  "password": "vetpass123",
  "fullName": "Dr. Le Van D",
  "phoneNumber": "0909123456",
  "role": "VETERINARIAN",
  "address": "789 Medical Street, District 1",
  "bio": "Bác sĩ thú y 8 năm kinh nghiệm",
  "profileImageUrl": "https://example.com/doctor.jpg",
  "licenseNumber": "VET2024001",
  "experienceYears": 8,
  "specializations": ["Small Animals", "Surgery", "Emergency Care"],
  "clinicName": "Happy Pet Clinic",
  "clinicAddress": "101 Pet Street, District 3",
  "availableFromTime": "08:00:00",
  "availableToTime": "18:00:00",
  "availableDays": ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"],
  "consultationFee": 300000.0,
  "isAvailableForEmergency": true
}
```

### ✅ POST Register SHELTER
```
POST {{baseUrl}}/api/auth/register
Content-Type: application/json

{
  "email": "shelter1@test.com",
  "password": "shelterpass123",
  "fullName": "Pham Thi E",
  "phoneNumber": "0908765432",
  "role": "SHELTER",
  "address": "202 Animal Rescue Street, Binh Thanh",
  "bio": "Quản lý trại cứu hộ động vật",
  "profileImageUrl": "https://example.com/shelter.jpg",
  "shelterName": "Happy Tail Shelter",
  "contactPersonName": "Pham Thi E",
  "registrationNumber": "SHELTER-HCM-2024-001",
  "website": "https://happytailshelter.org",
  "description": "Trại cứu hộ chó mèo bị bỏ rơi",
  "capacity": 150,
  "currentOccupancy": 82,
  "images": ["https://example.com/shelter1.jpg", "https://example.com/shelter2.jpg"],
  "acceptsDonations": true,
  "operatingHours": "8:00 AM - 5:00 PM (Thứ 2-CN)"
}
```

### ✅ POST Register ADMIN
```
POST {{baseUrl}}/api/auth/register
Content-Type: application/json

{
  "email": "admin1@test.com",
  "password": "adminpass123",
  "fullName": "Nguyen Van Admin",
  "phoneNumber": "0901234567",
  "role": "ADMIN",
  "address": "PetCare Office, District 1"
}
```

---

## 📁 FOLDER 3: OTP Verification

### ✅ POST Verify OTP
```
POST {{baseUrl}}/api/auth/verify-otp
Content-Type: application/json

{
  "email": "petowner1@test.com",
  "otpCode": "123456"
}
```

### ✅ POST Resend OTP
```
POST {{baseUrl}}/api/auth/resend-otp?email=petowner1@test.com
```

---

## 📁 FOLDER 4: Login

### ✅ POST Login
```
POST {{baseUrl}}/api/auth/login
Content-Type: application/json

{
  "email": "petowner1@test.com",
  "password": "password123"
}
```

---

## 📁 FOLDER 5: Profile (Protected)

### ✅ GET User Profile
```
GET {{baseUrl}}/api/user/profile
Authorization: Bearer {{token}}
```

---

## 📁 FOLDER 6: Password Reset

### ✅ POST Forgot Password
```
POST {{baseUrl}}/api/auth/forgot-password
Content-Type: application/json

{
  "email": "petowner1@test.com"
}
```

### ✅ POST Reset Password
```
POST {{baseUrl}}/api/auth/reset-password
Content-Type: application/json

{
  "email": "petowner1@test.com",
  "resetToken": "RESET_TOKEN_FROM_EMAIL",
  "newPassword": "newpassword123"
}
```

---

## 📁 FOLDER 7: Redis Testing

### ✅ POST Generate Test OTP
```
POST {{baseUrl}}/api/redis/generate-otp?email=test@example.com&purpose=REGISTRATION
```

### ✅ POST Verify Test OTP
```
POST {{baseUrl}}/api/redis/verify-otp?email=test@example.com&otp=123456&purpose=REGISTRATION
```

### ✅ GET OTP TTL
```
GET {{baseUrl}}/api/redis/otp-ttl?email=test@example.com&purpose=REGISTRATION
```

### ✅ DELETE OTP
```
DELETE {{baseUrl}}/api/redis/delete-otp?email=test@example.com&purpose=REGISTRATION
```

---

## 📁 FOLDER 8: Error Testing

### ✅ POST Register Invalid Email
```
POST {{baseUrl}}/api/auth/register
Content-Type: application/json

{
  "email": "invalid-email",
  "password": "password123",
  "fullName": "Test User",
  "phoneNumber": "0123456789",
  "role": "PET_OWNER",
  "address": "Test Address"
}
```

### ✅ POST Register VET without License
```
POST {{baseUrl}}/api/auth/register
Content-Type: application/json

{
  "email": "vet.invalid@test.com",
  "password": "password123",
  "fullName": "Dr. Invalid",
  "phoneNumber": "0123456789",
  "role": "VETERINARIAN",
  "address": "Test Address"
}
```

### ✅ POST Verify Invalid OTP
```
POST {{baseUrl}}/api/auth/verify-otp
Content-Type: application/json

{
  "email": "petowner1@test.com",
  "otpCode": "000000"
}
```

### ✅ POST Login Invalid Credentials
```
POST {{baseUrl}}/api/auth/login
Content-Type: application/json

{
  "email": "petowner1@test.com",
  "password": "wrongpassword"
}
```

---

## 🔧 POSTMAN VARIABLES

### Environment Variables:
```json
{
  "baseUrl": "http://localhost:8080",
  "token": "YOUR_JWT_TOKEN_HERE"
}
```

### Auto-set Token Script (for verify-otp request):
**Tests Tab:**
```javascript
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    if (jsonData.success && jsonData.data && jsonData.data.token) {
        pm.environment.set("token", jsonData.data.token);
        console.log("Token saved:", jsonData.data.token);
    }
}
```

---

## 📝 TEST SEQUENCE:

### 🔄 Recommended Test Flow:
1. **Health Check** → Verify server is running
2. **Register User** → Choose any role
3. **Check Email** → Get OTP code (6 digits)
4. **Verify OTP** → Get JWT token (auto-saved to {{token}})
5. **Get Profile** → Verify profile data
6. **Login** → Test login with credentials
7. **Error Tests** → Test validation

### 🎯 Success Indicators:
- ✅ Registration: `"success": true, "message": "Registration successful!"`
- ✅ OTP Verify: `"success": true` + JWT token returned
- ✅ Profile: Full profile data with role-specific fields
- ✅ Login: JWT token returned

### ❌ Error Indicators:
- ❌ `"success": false` with error message
- ❌ Validation errors with field-specific messages
- ❌ `"Invalid or expired OTP!"` for wrong OTP
- ❌ `"Email is already registered!"` for duplicate email
