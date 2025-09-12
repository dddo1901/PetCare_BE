# Veterinarian Registration API Test Examples

## Base URL
```
POST http://localhost:8080/api/auth/register
Content-Type: application/json
```

## 1. Test Case: Đăng ký Veterinarian thành công - Dữ liệu đầy đủ

### Request Body:
```json
{
    "email": "dr.nguyen@vetclinic.com",
    "password": "SecurePassword123!",
    "fullName": "Dr. Nguyen Van An",
    "phoneNumber": "0123456789",
    "role": "VETERINARIAN",
    "address": "123 Nguyen Trai, District 1, Ho Chi Minh City",
    "profileImageUrl": "https://example.com/images/dr-nguyen.jpg",
    "bio": "Experienced veterinarian specializing in small animals and emergency care",
    "licenseNumber": "VET2024001",
    "experienceYears": 8,
    "specializations": [
        "Small Animal Medicine",
        "Emergency Care",
        "Surgery"
    ],
    "clinicName": "An Vet Clinic",
    "clinicAddress": "456 Le Van Sy, District 3, Ho Chi Minh City",
    "availableFromTime": "08:00:00",
    "availableToTime": "18:00:00",
    "availableDays": [
        "MONDAY",
        "TUESDAY",
        "WEDNESDAY",
        "THURSDAY",
        "FRIDAY"
    ],
    "consultationFee": 200000.0,
    "isAvailableForEmergency": true
}
```

### Expected Response:
```json
{
    "status": "success",
    "message": "Registration successful. Please check your email for OTP verification.",
    "data": {
        "userId": 1,
        "email": "dr.nguyen@vetclinic.com",
        "fullName": "Dr. Nguyen Van An",
        "role": "VETERINARIAN",
        "isVerified": false
    }
}
```

---

## 2. Test Case: Đăng ký Veterinarian thành công - Dữ liệu tối thiểu

### Request Body:
```json
{
    "email": "dr.tran@email.com",
    "password": "Password123!",
    "fullName": "Dr. Tran Thi Bich",
    "phoneNumber": "0987654321",
    "role": "VETERINARIAN",
    "address": "789 Truong Chinh, Tan Binh District, Ho Chi Minh City",
    "licenseNumber": "VET2024002",
    "experienceYears": 3,
    "specializations": ["General Practice"],
    "clinicName": "Bich Pet Clinic",
    "clinicAddress": "101 Pham Van Dong, Thu Duc City",
    "consultationFee": 150000.0
}
```

### Expected Response:
```json
{
    "status": "success",
    "message": "Registration successful. Please check your email for OTP verification.",
    "data": {
        "userId": 2,
        "email": "dr.tran@email.com",
        "fullName": "Dr. Tran Thi Bich",
        "role": "VETERINARIAN",
        "isVerified": false
    }
}
```

---

## 3. Test Case: Lỗi - Email đã tồn tại

### Request Body:
```json
{
    "email": "dr.nguyen@vetclinic.com",
    "password": "AnotherPassword123!",
    "fullName": "Dr. Le Van Cuong",
    "phoneNumber": "0911223344",
    "role": "VETERINARIAN",
    "address": "555 Vo Van Tan, District 3, Ho Chi Minh City",
    "licenseNumber": "VET2024003",
    "experienceYears": 5,
    "specializations": ["Dermatology"],
    "clinicName": "Cuong Vet Center",
    "clinicAddress": "666 Cach Mang Thang 8, District 10",
    "consultationFee": 180000.0
}
```

### Expected Response:
```json
{
    "status": "error",
    "message": "Email already exists",
    "data": null
}
```

---

## 4. Test Case: Lỗi - Thiếu trường bắt buộc (licenseNumber)

### Request Body:
```json
{
    "email": "dr.invalid@email.com",
    "password": "Password123!",
    "fullName": "Dr. Invalid Test",
    "phoneNumber": "0123456789",
    "role": "VETERINARIAN",
    "address": "123 Test Street",
    "experienceYears": 2,
    "specializations": ["General Practice"],
    "clinicName": "Test Clinic",
    "clinicAddress": "456 Test Address",
    "consultationFee": 100000.0
}
```

### Expected Response:
```json
{
    "status": "error",
    "message": "License number is required for veterinarian registration",
    "data": null
}
```

---

## 5. Test Case: Lỗi - Dữ liệu không hợp lệ (experienceYears âm)

### Request Body:
```json
{
    "email": "dr.error@email.com",
    "password": "Password123!",
    "fullName": "Dr. Error Test",
    "phoneNumber": "0123456789",
    "role": "VETERINARIAN",
    "address": "123 Test Street",
    "licenseNumber": "VET2024004",
    "experienceYears": -1,
    "specializations": ["General Practice"],
    "clinicName": "Error Clinic",
    "clinicAddress": "456 Error Address",
    "consultationFee": 100000.0
}
```

### Expected Response:
```json
{
    "status": "error",
    "message": "Experience years must be positive",
    "data": null
}
```

---

## 6. Test Case: Lỗi - Email không hợp lệ

### Request Body:
```json
{
    "email": "invalid-email-format",
    "password": "Password123!",
    "fullName": "Dr. Email Test",
    "phoneNumber": "0123456789",
    "role": "VETERINARIAN",
    "address": "123 Test Street",
    "licenseNumber": "VET2024005",
    "experienceYears": 3,
    "specializations": ["General Practice"],
    "clinicName": "Email Test Clinic",
    "clinicAddress": "456 Email Test Address",
    "consultationFee": 100000.0
}
```

### Expected Response:
```json
{
    "status": "error",
    "message": "Email should be valid",
    "data": null
}
```

---

## 7. Test Case: Lỗi - Số điện thoại không hợp lệ

### Request Body:
```json
{
    "email": "dr.phone@email.com",
    "password": "Password123!",
    "fullName": "Dr. Phone Test",
    "phoneNumber": "invalid-phone",
    "role": "VETERINARIAN",
    "address": "123 Test Street",
    "licenseNumber": "VET2024006",
    "experienceYears": 3,
    "specializations": ["General Practice"],
    "clinicName": "Phone Test Clinic",
    "clinicAddress": "456 Phone Test Address",
    "consultationFee": 100000.0
}
```

### Expected Response:
```json
{
    "status": "error",
    "message": "Phone number should be valid",
    "data": null
}
```

---

## 8. Test Case: Đăng ký với chuyên môn đặc biệt

### Request Body:
```json
{
    "email": "dr.specialist@vetclinic.com",
    "password": "SpecialPassword123!",
    "fullName": "Dr. Pham Van Duc",
    "phoneNumber": "0908123456",
    "role": "VETERINARIAN",
    "address": "321 Dien Bien Phu, District 1, Ho Chi Minh City",
    "profileImageUrl": "https://example.com/images/dr-duc.jpg",
    "bio": "Specialist in exotic animals and avian medicine with 12 years of experience",
    "licenseNumber": "VET2024007",
    "experienceYears": 12,
    "specializations": [
        "Exotic Animals",
        "Avian Medicine",
        "Wildlife Rehabilitation",
        "Orthopedic Surgery"
    ],
    "clinicName": "Exotic Pet Care Center",
    "clinicAddress": "789 Hai Ba Trung, District 1, Ho Chi Minh City",
    "availableFromTime": "07:00:00",
    "availableToTime": "20:00:00",
    "availableDays": [
        "MONDAY",
        "TUESDAY",
        "WEDNESDAY",
        "THURSDAY",
        "FRIDAY",
        "SATURDAY"
    ],
    "consultationFee": 350000.0,
    "isAvailableForEmergency": true
}
```

### Expected Response:
```json
{
    "status": "success",
    "message": "Registration successful. Please check your email for OTP verification.",
    "data": {
        "userId": 3,
        "email": "dr.specialist@vetclinic.com",
        "fullName": "Dr. Pham Van Duc",
        "role": "VETERINARIAN",
        "isVerified": false
    }
}
```

---

## 9. Test Case: Đăng ký với thời gian làm việc cuối tuần

### Request Body:
```json
{
    "email": "dr.weekend@email.com",
    "password": "Weekend123!",
    "fullName": "Dr. Hoang Thi Mai",
    "phoneNumber": "0909876543",
    "role": "VETERINARIAN",
    "address": "147 Nguyen Van Cu, District 5, Ho Chi Minh City",
    "licenseNumber": "VET2024008",
    "experienceYears": 6,
    "specializations": [
        "Emergency Care",
        "Internal Medicine"
    ],
    "clinicName": "24/7 Pet Emergency Center",
    "clinicAddress": "258 Tran Hung Dao, District 5, Ho Chi Minh City",
    "availableFromTime": "00:00:00",
    "availableToTime": "23:59:59",
    "availableDays": [
        "SATURDAY",
        "SUNDAY"
    ],
    "consultationFee": 280000.0,
    "isAvailableForEmergency": true
}
```

### Expected Response:
```json
{
    "status": "success",
    "message": "Registration successful. Please check your email for OTP verification.",
    "data": {
        "userId": 4,
        "email": "dr.weekend@email.com",
        "fullName": "Dr. Hoang Thi Mai",
        "role": "VETERINARIAN",
        "isVerified": false
    }
}
```

---

## Postman Collection Setup

### Variables:
```json
{
    "baseUrl": "http://localhost:8080",
    "contentType": "application/json"
}
```

### Pre-request Script (Optional):
```javascript
// Generate random email for testing
const randomId = Math.floor(Math.random() * 10000);
pm.environment.set("randomEmail", `dr.test${randomId}@vetclinic.com`);
```

### Test Script:
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has correct structure", function () {
    const responseJson = pm.response.json();
    pm.expect(responseJson).to.have.property('status');
    pm.expect(responseJson).to.have.property('message');
});

pm.test("Registration successful", function () {
    const responseJson = pm.response.json();
    if (responseJson.status === 'success') {
        pm.expect(responseJson.data).to.have.property('userId');
        pm.expect(responseJson.data).to.have.property('email');
        pm.expect(responseJson.data.role).to.equal('VETERINARIAN');
        pm.expect(responseJson.data.isVerified).to.equal(false);
    }
});
```

---

## cURL Examples

### Basic Registration:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "dr.curl@email.com",
    "password": "CurlTest123!",
    "fullName": "Dr. Curl Test",
    "phoneNumber": "0123456789",
    "role": "VETERINARIAN",
    "address": "123 Curl Street",
    "licenseNumber": "VET2024009",
    "experienceYears": 4,
    "specializations": ["General Practice"],
    "clinicName": "Curl Clinic",
    "clinicAddress": "456 Curl Address",
    "consultationFee": 120000.0
  }'
```

### Complete Registration with all fields:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "dr.complete@email.com",
    "password": "Complete123!",
    "fullName": "Dr. Complete Test",
    "phoneNumber": "0987654321",
    "role": "VETERINARIAN",
    "address": "789 Complete Street",
    "profileImageUrl": "https://example.com/image.jpg",
    "bio": "Complete test veterinarian",
    "licenseNumber": "VET2024010",
    "experienceYears": 7,
    "specializations": ["Surgery", "Cardiology"],
    "clinicName": "Complete Vet Clinic",
    "clinicAddress": "101 Complete Address",
    "availableFromTime": "09:00:00",
    "availableToTime": "17:00:00",
    "availableDays": ["MONDAY", "WEDNESDAY", "FRIDAY"],
    "consultationFee": 250000.0,
    "isAvailableForEmergency": false
  }'
```
