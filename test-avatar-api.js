const http = require('http');

// Test data
const testData = {
  profileImageUrl: "https://example.com/test-avatar.jpg"
};

const postData = JSON.stringify(testData);

const options = {
  hostname: 'localhost',
  port: 8080,
  path: '/api/user/avatar',
  method: 'PUT',
  headers: {
    'Content-Type': 'application/json',
    'Content-Length': Buffer.byteLength(postData),
    'Authorization': 'Bearer your-jwt-token-here' // Replace with actual token
  }
};

const req = http.request(options, (res) => {
  console.log(`STATUS: ${res.statusCode}`);
  console.log(`HEADERS: ${JSON.stringify(res.headers)}`);
  
  let body = '';
  res.on('data', (chunk) => {
    body += chunk;
  });
  
  res.on('end', () => {
    console.log('RESPONSE BODY:');
    console.log(body);
  });
});

req.on('error', (e) => {
  console.error(`Problem with request: ${e.message}`);
});

// Write data to request body
req.write(postData);
req.end();

console.log('Testing API /api/user/avatar...');
console.log('Request data:', testData);