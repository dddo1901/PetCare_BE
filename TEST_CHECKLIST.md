# 🧪 PetCare API Testing Checklist

## ✅ Prerequisites
- [x] Server đang chạy ở `http://localhost:8080`
- [x] Redis server đang chạy (nếu test OTP)
- [x] Database connected
- [x] Import Postman collection: `PetCare_Postman_Collection.json`

---

## 📋 Test Sequence (Recommended Order)

### **Step 1: Health Checks** ✅
```
[ ] GET /api/test/health
[ ] GET /api/auth/test  
[ ] GET /api/test/roles
```
**Expected:** All return 200 OK với success messages

---

### **Step 2: Registration Tests** 
Chọn 1 role để test đầu tiên:

#### Option A: Test PET_OWNER
```
[ ] POST /api/auth/register (PET_OWNER Minimal)
    Email: petowner@test.com
[ ] Kiểm tra email nhận OTP
[ ] POST /api/auth/verify-otp 
    Input OTP từ email
[ ] Lưu JWT token từ response
```

#### Option B: Test VETERINARIAN  
```
[ ] POST /api/auth/register (VETERINARIAN Full Profile)
    Email: vet@test.com
[ ] Kiểm tra email nhận OTP
[ ] POST /api/auth/verify-otp
[ ] Lưu JWT token từ response
```

#### Option C: Test SHELTER
```  
[ ] POST /api/auth/register (SHELTER Full Profile)
    Email: shelter@test.com
[ ] Kiểm tra email nhận OTP
[ ] POST /api/auth/verify-otp
[ ] Lưu JWT token từ response
```

---

### **Step 3: Profile Tests**
```
[ ] GET /api/user/profile
    Headers: Authorization: Bearer YOUR_JWT_TOKEN
[ ] Verify profile data matches registration input
[ ] Check role-specific profile fields
```

---

### **Step 4: Login Tests**
```
[ ] POST /api/auth/login
    Use registered email/password
[ ] Compare JWT token with verify-otp response
[ ] Test login with wrong password (should fail)
```

---

### **Step 5: OTP Flow Tests**
```
[ ] POST /api/auth/resend-otp
[ ] POST /api/auth/verify-otp (với OTP mới)
[ ] Test expired OTP (chờ >10 phút)
[ ] Test invalid OTP
```

---

### **Step 6: Redis Tests** (Optional)
```
[ ] POST /api/redis/generate-otp
[ ] GET /api/redis/otp-ttl  
[ ] POST /api/redis/verify-otp
[ ] DELETE /api/redis/delete-otp
```

---

### **Step 7: Password Reset**
```
[ ] POST /api/auth/forgot-password
[ ] Kiểm tra email nhận reset token
[ ] POST /api/auth/reset-password 
[ ] POST /api/auth/login (với password mới)
```

---

### **Step 8: Validation Tests** (Should Fail)
```
[ ] Register without required fields
[ ] Register VET without license
[ ] Register SHELTER without shelter name
[ ] Register with invalid email format
[ ] Register with existing email
```

---

## 🎯 Expected Results

### ✅ Successful Registration Response:
```json
{
  "success": true,
  "message": "Registration successful! Please check your email for OTP verification.",
  "data": null
}
```

### ✅ Successful OTP Verification:
```json
{
  "success": true,
  "message": "Email verified successfully!",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "userId": 1,
    "email": "petowner@test.com",
    "fullName": "Nguyen Van A",
    "role": "PET_OWNER",
    "isEmailVerified": true
  }
}
```

### ✅ Profile Response (PET_OWNER example):
```json
{
  "success": true,
  "message": "User profile retrieved successfully",
  "data": {
    "userId": 1,
    "email": "petowner@test.com",
    "role": "PET_OWNER",
    "profile": {
      "profileId": 1,
      "address": "123 ABC Street, Ho Chi Minh City",
      "emergencyContactName": "Nguyen Thi C",
      "emergencyContactPhone": "0987654321",
      "allowAccountSharing": true,
      "bio": "Yêu thích nuôi thú cưng"
    }
  }
}
```

---

## 🚨 Common Issues & Solutions

### **403 Forbidden Error:**
- ✅ Fixed: Updated SecurityConfig to permit test endpoints

### **Email not received:**
- Check spam folder
- Verify email config in application.properties
- Use Redis test APIs to check OTP generation

### **Redis Connection Error:**
- Start Redis: `redis-server` hoặc `docker run -d -p 6379:6379 redis`
- Check Redis config in application.properties

### **Database Issues:**
- MySQL datetime warnings are not critical
- App still runs successfully

---

## 📊 Test Data Summary

| Role | Email | Password | Special Fields |
|------|-------|----------|----------------|
| PET_OWNER | petowner@test.com | password123 | Emergency contact |
| VETERINARIAN | vet@test.com | password123 | License, Specializations |
| SHELTER | shelter@test.com | password123 | Shelter name, Capacity |
| ADMIN | admin.test@test.com | password123 | Basic info only |

---

## 🎉 Success Criteria

- [x] ✅ All health check APIs return 200
- [ ] ✅ At least 1 role registration successful
- [ ] ✅ OTP received via email
- [ ] ✅ OTP verification returns JWT token
- [ ] ✅ Profile API returns complete role-based data
- [ ] ✅ Login works with registered credentials
- [ ] ✅ Validation errors return proper messages

**Ready to start testing!** 🚀
