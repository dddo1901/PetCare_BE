# PetCare Backend Application

A comprehensive Spring Boot backend application for pet care management system supporting multiple user roles: Pet Owners, Veterinarians, and Animal Shelters.

## Features

### Authentication & Authorization
- JWT-based authentication
- Role-based access control (Pet Owner, Veterinarian, Shelter)
- Email-based OTP verification
- Password reset functionality
- Redis-based OTP storage

### User Management
- Multi-role user registration
- Profile management for different user types
- Email verification system
- Secure password handling

### API Documentation
- Comprehensive REST API endpoints
- Postman collection included
- Detailed testing guides

## Technology Stack

- **Framework**: Spring Boot 3.x
- **Database**: JPA/Hibernate (configurable)
- **Caching**: Redis
- **Security**: Spring Security with JWT
- **Email**: Spring Mail
- **Build Tool**: Maven
- **Java Version**: 17+

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── TechWiz/
│   │       ├── PetCareEbApplication.java
│   │       ├── auths/
│   │       │   ├── configs/          # Security & configuration classes
│   │       │   ├── controllers/      # REST controllers
│   │       │   ├── models/           # Entity models and DTOs
│   │       │   ├── repositories/     # Data access layer
│   │       │   └── services/         # Business logic layer
│   │       ├── petOwner/
│   │       ├── shelter/
│   │       └── veterinarian/
│   └── resources/
│       └── application.properties
└── test/
```

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Redis server
- MySQL/PostgreSQL (or any JPA-compatible database)

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd PetCare_BE
```

2. Configure database and Redis settings in `application.properties`

3. Run the application:
```bash
# Using Maven wrapper (recommended)
./mvnw spring-boot:run

# Or using Maven directly
mvn spring-boot:run

# Or using the provided batch file (Windows)
start.bat
```

## API Documentation

### Authentication Endpoints
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/verify-otp` - Email verification
- `POST /api/auth/forgot-password` - Request password reset
- `POST /api/auth/reset-password` - Reset password with OTP

### User Management
- `GET /api/user/profile` - Get user profile
- `PUT /api/user/profile` - Update user profile

## Testing

### Postman Collection
Import `PetCare_Postman_Collection.json` into Postman for comprehensive API testing.

### Test Documentation
- `API_TEST_GUIDE.md` - Complete API testing guide
- `OTP_GUIDE.md` - OTP functionality testing
- `REDIS_OTP_GUIDE.md` - Redis OTP implementation details
- `REGISTRATION_EXAMPLES.md` - Registration workflow examples
- `TEST_CHECKLIST.md` - Testing checklist

## Configuration

### Application Properties
Configure the following in `application.properties`:

```properties
# Database configuration
spring.datasource.url=jdbc:mysql://localhost:3306/petcare
spring.datasource.username=your_username
spring.datasource.password=your_password

# Redis configuration
spring.redis.host=localhost
spring.redis.port=6379

# Email configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password

# JWT configuration
jwt.secret=your_jwt_secret
jwt.expiration=86400000
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

Project Link: [https://github.com/yourusername/PetCare_BE](https://github.com/yourusername/PetCare_BE)

## Acknowledgments

- Spring Boot team for the excellent framework
- Redis for caching solutions
- JWT for secure authentication
