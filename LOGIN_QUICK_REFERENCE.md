# 🔐 Quick Login API Test Reference

## 🚀 Quick Test Commands

### 1. Basic Login Test
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
-H "Content-Type: application/json" \
-d '{"email": "test@example.com", "password": "Test123!"}'
```

### 2. Test Different User Types

**Pet Owner Login:**
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
-H "Content-Type: application/json" \
-d '{"email": "petowner@example.com", "password": "SecurePass123!"}'
```

**Veterinarian Login:**
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
-H "Content-Type: application/json" \
-d '{"email": "vet@example.com", "password": "VetPass123!"}'
```

**Shelter Login:**
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
-H "Content-Type: application/json" \
-d '{"email": "shelter@example.com", "password": "ShelterPass123!"}'
```

## 📋 Expected Responses

### ✅ Success (200 OK):
```json
{
  "success": true,
  "message": "OTP sent to your email",
  "data": null
}
```

**Then Verify OTP:**
```bash
curl -X POST "http://localhost:8080/api/auth/verify-otp" \
-H "Content-Type: application/json" \
-d '{"email": "test@example.com", "otp": "123456", "purpose": "LOGIN"}'
```

**OTP Verification Success:**
```json
{
  "success": true,
  "message": "Login successful!",
  "data": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwidXNlcklkIjoxLCJ1c2VyUm9sZSI6IlBFVF9PV05FUiIsImlhdCI6MTY4OTY3MjAwMCwiZXhwIjoxNjg5NzU4NDAwfQ.xyz..."
}
```

### ❌ Invalid Credentials (200 OK):
```json
{
  "success": false,
  "message": "Invalid email or password!",
  "data": null
}
```

### ❌ Unverified Email (200 OK):
```json
{
  "success": false,
  "message": "Please verify your email first. New OTP sent to your email.",
  "data": null
}
```

### ❌ Validation Error (400 Bad Request):
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "email": "Email is required"
  }
}
```

## 🔒 Using JWT Token

### Extract Token and Use for Authenticated Request:
```bash
# 1. Login (sends OTP)
curl -s -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com", "password": "Test123!"}'

# 2. Verify OTP and extract token
TOKEN=$(curl -s -X POST "http://localhost:8080/api/auth/verify-otp" \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com", "otp": "123456", "purpose": "LOGIN"}' | \
  jq -r '.data')

# 3. Use token for protected endpoint
curl -X GET "http://localhost:8080/api/user/profile" \
  -H "Authorization: Bearer $TOKEN"
```

## 🧪 PowerShell Testing

### Basic Login:
```powershell
# Step 1: Login (sends OTP)
$loginBody = @{
    email = "test@example.com"
    password = "Test123!"
} | ConvertTo-Json

$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
$loginResponse

# Step 2: Verify OTP (returns token)
$otpBody = @{
    email = "test@example.com"
    otp = "123456"
    purpose = "LOGIN"
} | ConvertTo-Json

$otpResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/verify-otp" -Method POST -Body $otpBody -ContentType "application/json"
$otpResponse
```

### Save and Use Token:
```powershell
$token = $otpResponse.data
$headers = @{ Authorization = "Bearer $token" }

Invoke-RestMethod -Uri "http://localhost:8080/api/user/profile" -Method GET -Headers $headers
```

## 🔍 Testing Checklist

- [ ] **Login Request** → Returns "OTP sent to your email"
- [ ] **Valid OTP Verification** → Returns JWT token as string
- [ ] **Invalid Email** → Error message, no data
- [ ] **Invalid Password** → Error message, no data  
- [ ] **Invalid OTP** → Error message, no data
- [ ] **Expired OTP** → Error message, no data
- [ ] **Unverified Email** → Error message, OTP resent
- [ ] **Missing Email Field** → Validation error
- [ ] **Missing Password Field** → Validation error
- [ ] **Invalid Email Format** → Validation error
- [ ] **JWT Token Works** → Can access protected endpoints
- [ ] **No Token** → Access denied to protected endpoints

## ⚡ Quick Debug

### Check if User Exists:
```bash
# Try to resend OTP (will fail if user doesn't exist)
curl -X POST "http://localhost:8080/api/auth/resend-otp?email=test@example.com"
```

### Check Token Validity:
```bash
# Decode JWT token (first part is header, second is payload)
echo "TOKEN_HERE" | cut -d. -f2 | base64 -d
```

### Common Issues:
1. **"OTP sent to your email"** → Check email for OTP code
2. **"Invalid email or password"** → User not registered or wrong password  
3. **"Invalid or expired OTP"** → Use correct OTP within 5 minutes
4. **"Please verify your email first"** → Complete registration OTP first
5. **"Account is deactivated"** → Contact admin
6. **400 Validation Error** → Check required fields and format

## 📁 Files Created:
- `LOGIN_API_TEST_EXAMPLES.md` - Detailed examples
- `Login_Postman_Collection.json` - Postman collection for import
- `LOGIN_QUICK_REFERENCE.md` - This quick reference guide
