// Test script for ProfileCompletion API
// Chạy script này trong browser console hoặc Node.js

const API_BASE = 'http://localhost:8080/api';

// 1. Đăng ký user mới
async function registerUser() {
    const response = await fetch(`${API_BASE}/auth/register`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            email: 'testuser@example.com',
            password: '123456',
            fullName: 'Test User',
            phoneNumber: '0123456789',
            role: 'SHELTER'
        })
    });
    
    const result = await response.json();
    console.log('Register response:', result);
    return result;
}

// 2. Verify OTP và lấy JWT token
async function verifyOtp(email, otpCode) {
    const response = await fetch(`${API_BASE}/auth/verify-otp`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            email: email,
            otp: otpCode,
            purpose: 'REGISTRATION'
        })
    });
    
    const result = await response.json();
    console.log('Verify OTP response:', result);
    return result.data?.token; // JWT token
}

// 3. Test GET profile endpoint
async function getProfile(token, role = 'shelter') {
    const response = await fetch(`${API_BASE}/profile/${role}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
        }
    });
    
    const result = await response.json();
    console.log(`Get ${role} profile response:`, result);
    return result;
}

// 4. Test UPDATE profile endpoint  
async function updateShelterProfile(token) {
    const response = await fetch(`${API_BASE}/profile/shelter`, {
        method: 'PUT',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            shelterName: 'Test Shelter Name',
            address: '123 Test Address',
            contactPersonName: 'Test Contact Person',
            capacity: 50,
            currentOccupancy: 10,
            acceptsDonations: true,
            operatingHours: 'Mon-Fri 9AM-5PM'
        })
    });
    
    const result = await response.json();
    console.log('Update shelter profile response:', result);
    return result;
}

// 5. Main test function
async function runTests() {
    try {
        console.log('=== Starting API Tests ===');
        
        // Step 1: Register
        console.log('\n1. Testing registration...');
        const registerResult = await registerUser();
        
        if (!registerResult.success) {
            console.error('Registration failed:', registerResult.message);
            return;
        }
        
        // Step 2: Manual OTP input
        console.log('\n2. Please check email for OTP and enter it manually');
        const otp = prompt('Enter OTP from email:');
        
        if (!otp) {
            console.log('No OTP provided, stopping tests');
            return;
        }
        
        // Step 3: Verify OTP and get token
        console.log('\n3. Verifying OTP...');
        const token = await verifyOtp('testuser@example.com', otp);
        
        if (!token) {
            console.error('Failed to get token');
            return;
        }
        
        console.log('JWT Token:', token);
        
        // Step 4: Test GET profile
        console.log('\n4. Testing GET profile...');
        await getProfile(token, 'shelter');
        
        // Step 5: Test UPDATE profile  
        console.log('\n5. Testing UPDATE profile...');
        await updateShelterProfile(token);
        
        console.log('\n=== Tests completed ===');
        
    } catch (error) {
        console.error('Test error:', error);
    }
}

// Export for use
if (typeof module !== 'undefined') {
    module.exports = { runTests, registerUser, verifyOtp, getProfile, updateShelterProfile };
}

console.log('Test functions loaded. Call runTests() to start testing.');