# API Test Examples - Login Authentication

## Base URL
```
http://localhost:8080
```

## 1. Login API (POST /api/auth/login)

### 1.1 Login Step 1 - Send OTP (Verified Account)
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
-H "Content-Type: application/json" \
-d '{
  "email": "petowner@example.com",
  "password": "SecurePass123!"
}'
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Please check your email for OTP verification code.",
  "data": null
}
```

### 1.2 Login Step 2 - Verify OTP and Get Token
```bash
curl -X POST "http://localhost:8080/api/auth/verify-otp" \
-H "Content-Type: application/json" \
-d '{
  "email": "petowner@example.com",
  "otpCode": "123456"
}'
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Login successful!",
  "data": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwZXRvd25lckBleGFtcGxlLmNvbSIsInVzZXJJZCI6MSwidXNlclJvbGUiOiJQRVRfT1dORVIiLCJpYXQiOjE2ODk2NzIwMDAsImV4cCI6MTY4OTc1ODQwMH0.xyz..."
}
```

### 1.3 Login Different User Roles

**Pet Owner Login - Step 1:**
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
-H "Content-Type: application/json" \
-d '{
  "email": "petowner@example.com",
  "password": "SecurePass123!"
}'
```

**Veterinarian Login - Step 1:**
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
-H "Content-Type: application/json" \
-d '{
  "email": "vet@example.com",
  "password": "VetPass123!"
}'
```

**Shelter Login - Step 1:**
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
-H "Content-Type: application/json" \
-d '{
  "email": "shelter@example.com",
  "password": "ShelterPass123!"
}'
```

**All Step 1 responses:**
```json
{
  "success": true,
  "message": "Please check your email for OTP verification code.",
  "data": null
}
```

**Step 2 - Verify OTP (for all roles):**
```bash
curl -X POST "http://localhost:8080/api/auth/verify-otp" \
-H "Content-Type: application/json" \
-d '{
  "email": "your_email@example.com",
  "otpCode": "123456"
}'
```

## 2. Error Cases

### 2.1 Invalid Email or Password
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
-H "Content-Type: application/json" \
-d '{
  "email": "wrong@example.com",
  "password": "WrongPassword!"
}'
```

**Expected Response:**
```json
{
  "success": false,
  "message": "Invalid email or password!",
  "data": null
}
```

### 2.2 Correct Email, Wrong Password
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
-H "Content-Type: application/json" \
-d '{
  "email": "petowner@example.com",
  "password": "WrongPassword123!"
}'
```

**Expected Response:**
```json
{
  "success": false,
  "message": "Invalid email or password!",
  "data": null
}
```

### 2.3 Login with Unverified Email (Now removed - all logins require OTP)
This case no longer applies as all login attempts now send OTP verification codes regardless of email verification status.

### 2.4 Login with Deactivated Account
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
-H "Content-Type: application/json" \
-d '{
  "email": "deactivated@example.com",
  "password": "Test123!"
}'
```

**Expected Response:**
```json
{
  "success": false,
  "message": "Your account has been deactivated. Please contact support for assistance.",
  "data": null
}
```

### 2.5 Missing Required Fields

**Missing Email:**
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
-H "Content-Type: application/json" \
-d '{
  "password": "Test123!"
}'
```

**Expected Response:**
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "email": "Email is required"
  }
}
```

**Missing Password:**
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
-H "Content-Type: application/json" \
-d '{
  "email": "test@example.com"
}'
```

**Expected Response:**
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "password": "Password is required"
  }
}
```

**Invalid Email Format:**
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
-H "Content-Type: application/json" \
-d '{
  "email": "invalid-email",
  "password": "Test123!"
}'
```

**Expected Response:**
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "email": "Email should be valid"
  }
}
```

## 3. Complete Login Flow Test (New 2-Step Process)

### Step 1: Register User
```bash
curl -X POST "http://localhost:8080/api/auth/register" \
-H "Content-Type: application/json" \
-d '{
  "email": "newuser@example.com",
  "password": "NewUser123!",
  "fullName": "New User",
  "phoneNumber": "0123456789",
  "role": "PET_OWNER",
  "address": "123 Test Street"
}'
```

### Step 2: Verify Registration OTP
```bash
curl -X POST "http://localhost:8080/api/auth/verify-otp" \
-H "Content-Type: application/json" \
-d '{
  "email": "newuser@example.com",
  "otpCode": "123456"
}'
```

### Step 3: Login - Request OTP
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
-H "Content-Type: application/json" \
-d '{
  "email": "newuser@example.com",
  "password": "NewUser123!"
}'
```

**Response:**
```json
{
  "success": true,
  "message": "Please check your email for OTP verification code.",
  "data": null
}
```

### Step 4: Verify Login OTP and Get Token
```bash
curl -X POST "http://localhost:8080/api/auth/verify-otp" \
-H "Content-Type: application/json" \
-d '{
  "email": "newuser@example.com",
  "otpCode": "654321"
}'
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful!",
  "data": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJuZXd1c2VyQGV4YW1wbGUuY29tIiwidXNlcklkIjoxLCJ1c2VyUm9sZSI6IlBFVF9PV05FUiIsImlhdCI6MTY4OTY3MjAwMCwiZXhwIjoxNjg5NzU4NDAwfQ.xyz..."
}
```

## 4. Using JWT Token for Authenticated Requests

### 4.1 Get User Profile (Protected Endpoint)
```bash
# Use the token from login response
curl -X GET "http://localhost:8080/api/user/profile" \
-H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwZXRvd25lckBleGFtcGxlLmNvbSIsInVzZXJJZCI6MSwidXNlclJvbGUiOiJQRVRfT1dORVIiLCJpYXQiOjE2ODk2NzIwMDAsImV4cCI6MTY4OTc1ODQwMH0.xyz..."
```

### 4.2 Without Token (Should Fail)
```bash
curl -X GET "http://localhost:8080/api/user/profile"
```

**Expected Response:**
```json
{
  "success": false,
  "message": "Access denied. No token provided.",
  "data": null
}
```

## 5. PowerShell Examples (Windows)

### 5.1 Successful Login
```powershell
$body = @{
    email = "petowner@example.com"
    password = "SecurePass123!"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -Body $body -ContentType "application/json"
```

### 5.2 Save Token and Use for Authenticated Request
```powershell
# Step 1: Login to get OTP
$loginBody = @{
    email = "petowner@example.com"
    password = "SecurePass123!"
} | ConvertTo-Json

$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json"

# Step 2: Verify OTP to get token
$otpBody = @{
    email = "petowner@example.com"
    otpCode = "123456"
} | ConvertTo-Json

$otpResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/verify-otp" -Method POST -Body $otpBody -ContentType "application/json"

# Extract token (now directly from data field)
$token = $otpResponse.data

# Use token for authenticated request
$headers = @{
    Authorization = "Bearer $token"
}

Invoke-RestMethod -Uri "http://localhost:8080/api/user/profile" -Method GET -Headers $headers
```

## 6. JavaScript/Fetch Examples

### 6.1 Complete Login Flow
```javascript
// Step 1: Login to get OTP
fetch('http://localhost:8080/api/auth/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    email: 'petowner@example.com',
    password: 'SecurePass123!'
  })
})
.then(response => response.json())
.then(data => {
  console.log('Login response:', data);
  if (data.success) {
    console.log('OTP sent to email');
    // Now verify OTP
    return fetch('http://localhost:8080/api/auth/verify-otp', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        email: 'petowner@example.com',
        otpCode: '123456' // Use actual OTP from email
      })
    });
  }
})
.then(response => response.json())
.then(data => {
  console.log('OTP verification response:', data);
  if (data.success) {
    // Store token (now directly from data field)
    localStorage.setItem('authToken', data.data);
  }
});
```

### 6.2 Using Token for Authenticated Request
```javascript
const token = localStorage.getItem('authToken');

fetch('http://localhost:8080/api/user/profile', {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
})
.then(response => response.json())
.then(data => {
  console.log('Profile data:', data);
});
```

## 7. Response Fields Explanation

### Step 1 - Login Request Response:
```json
{
  "success": true,                                    // Login credentials valid
  "message": "Please check your email for OTP verification code.", // OTP sent message
  "data": null                                        // No data at this step
}
```

### Step 2 - OTP Verification Success Response:
```json
{
  "success": true,              // OTP verification status
  "message": "Login successful!", // Success message
  "data": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJwZXRvd25lckBleGFtcGxlLmNvbSIsInVzZXJJZCI6MSwidXNlclJvbGUiOiJQRVRfT1dORVIiLCJpYXQiOjE2ODk2NzIwMDAsImV4cCI6MTY4OTc1ODQwMH0.xyz..."
  // data contains only the JWT token string
}
```

### JWT Token Structure (when decoded):
```json
{
  "sub": "petowner@example.com",  // Subject (email)
  "userId": 1,                    // User ID
  "userRole": "PET_OWNER",       // User role
  "iat": 1689672000,             // Issued at timestamp
  "exp": 1689758400              // Expiration timestamp
}
```

## 8. Testing Checklist

### Login Step 1 (Password Verification):
- [ ] Valid email and password → OTP sent message
- [ ] Invalid email → Error message
- [ ] Invalid password → Error message
- [ ] Deactivated account → Specific error message
- [ ] Missing email field → Validation error
- [ ] Missing password field → Validation error
- [ ] Invalid email format → Validation error

### Login Step 2 (OTP Verification):
- [ ] Valid OTP → Success with token (only token string in data)
- [ ] Invalid OTP → Error message
- [ ] Expired OTP → Error message
- [ ] Different user roles can login correctly
- [ ] JWT token works for protected endpoints
- [ ] Response data contains only JWT token string
- [ ] Deactivated account blocked even with valid OTP

## 9. Common Issues and Solutions

### Issue: "Invalid email or password"
- Check if user is registered
- Verify password is correct
- Ensure email format is valid

### Issue: "Please verify your email first"
- User needs to complete OTP verification
- Check email for OTP code
- Use `/api/auth/verify-otp` endpoint

### Issue: "Account is deactivated"
- Contact admin to reactivate account
- Check user status in database

### Issue: Token expired
- Login again to get new token
- Check token expiration time (default 24 hours)
