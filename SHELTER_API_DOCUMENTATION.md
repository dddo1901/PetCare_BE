# Shelter Management API Documentation

## Overview
This document provides information about the Shelter Management API endpoints for the PetCare application.

## Base URL
```
http://localhost:8080/api/shelter
```

## API Endpoints

### 1. Shelter Management

#### Register Shelter
- **POST** `/register`
- **Body**: ShelterRegistrationRequestDto
- **Description**: Register a new shelter

#### Get Shelter by ID
- **GET** `/{id}`
- **Description**: Get shelter details by ID

#### Get Shelter by Email
- **GET** `/email/{email}`
- **Description**: Get shelter details by email

#### Get Verified Shelters
- **GET** `/verified`
- **Description**: Get all verified shelters

#### Get All Shelters (with filters)
- **GET** `/all`
- **Query Parameters**: shelterName, status, isVerified, page, size, sortBy, sortDir
- **Description**: Get all shelters with pagination and filters

#### Update Shelter
- **PUT** `/{id}`
- **Body**: ShelterRegistrationRequestDto
- **Description**: Update shelter information

#### Update Shelter Status
- **PUT** `/{id}/status`
- **Query Parameters**: status
- **Description**: Update shelter status (PENDING, ACTIVE, SUSPENDED, INACTIVE)

#### Verify Shelter
- **PUT** `/{id}/verify`
- **Query Parameters**: verified (boolean)
- **Description**: Verify or unverify a shelter

#### Delete Shelter
- **DELETE** `/{id}`
- **Description**: Delete a shelter

### 2. Pet Management

#### Add New Pet
- **POST** `/{shelterId}/pets`
- **Body**: PetRequestDto
- **Description**: Add a new pet to shelter

#### Get Pet by ID
- **GET** `/pets/{id}`
- **Description**: Get pet details by ID

#### Get Pets by Shelter (with filters)
- **GET** `/{shelterId}/pets`
- **Query Parameters**: name, type, breed, adoptionStatus, healthStatus, gender, size, page, pageSize, sortBy, sortDir
- **Description**: Get pets by shelter with pagination and filters

#### Get Available Pets by Shelter
- **GET** `/{shelterId}/pets/available`
- **Description**: Get all available pets for adoption from a shelter

#### Get Available Pets for Adoption (Public)
- **GET** `/pets/available`
- **Query Parameters**: type, breed, gender, size, page, pageSize, sortBy, sortDir
- **Description**: Get all available pets for adoption with filters

#### Update Pet
- **PUT** `/pets/{id}`
- **Body**: PetRequestDto
- **Description**: Update pet information

#### Update Pet Adoption Status
- **PUT** `/pets/{id}/adoption-status`
- **Query Parameters**: adoptionStatus
- **Description**: Update pet adoption status

#### Delete Pet
- **DELETE** `/pets/{id}`
- **Description**: Delete a pet

### 3. Care Log Management

#### Add Care Log
- **POST** `/{shelterId}/care-logs`
- **Body**: CareLogRequestDto
- **Description**: Add a new care log for a pet

#### Get Care Log by ID
- **GET** `/care-logs/{id}`
- **Description**: Get care log details by ID

#### Get Care Logs by Shelter (with filters)
- **GET** `/{shelterId}/care-logs`
- **Query Parameters**: petId, type, staffName, dateFrom, dateTo, page, size, sortBy, sortDir
- **Description**: Get care logs by shelter with pagination and filters

#### Get Care Logs by Pet
- **GET** `/pets/{petId}/care-logs`
- **Description**: Get all care logs for a specific pet

#### Get Care Logs by Pet and Type
- **GET** `/pets/{petId}/care-logs/type/{type}`
- **Description**: Get care logs by pet and care type

#### Get Today's Care Logs by Pet
- **GET** `/pets/{petId}/care-logs/today`
- **Description**: Get today's care logs for a specific pet

#### Get Today's Care Logs Statistics
- **GET** `/{shelterId}/care-logs/today/stats`
- **Description**: Get statistics of today's care logs by type

#### Update Care Log
- **PUT** `/care-logs/{id}`
- **Body**: CareLogRequestDto
- **Description**: Update a care log

#### Delete Care Log
- **DELETE** `/care-logs/{id}`
- **Description**: Delete a care log

### 4. Adoption Inquiry Management

#### Submit Adoption Inquiry
- **POST** `/adoption-inquiries`
- **Body**: AdoptionInquiryRequestDto
- **Description**: Submit an adoption inquiry for a pet

#### Get Inquiry by ID
- **GET** `/adoption-inquiries/{id}`
- **Description**: Get adoption inquiry details by ID

#### Get Inquiries by Shelter (with filters)
- **GET** `/{shelterId}/adoption-inquiries`
- **Query Parameters**: petId, status, adopterName, page, size, sortBy, sortDir
- **Description**: Get adoption inquiries by shelter with pagination and filters

#### Get Inquiries by Pet
- **GET** `/pets/{petId}/adoption-inquiries`
- **Description**: Get all adoption inquiries for a specific pet

#### Get Inquiries by Status
- **GET** `/adoption-inquiries/status/{status}`
- **Description**: Get adoption inquiries by status

#### Get Inquiry Statistics
- **GET** `/{shelterId}/adoption-inquiries/stats`
- **Description**: Get adoption inquiry statistics by status

#### Respond to Inquiry
- **PUT** `/adoption-inquiries/{id}/respond`
- **Body**: InquiryResponseRequestDto
- **Description**: Respond to an adoption inquiry

#### Update Inquiry Status
- **PUT** `/adoption-inquiries/{id}/status`
- **Query Parameters**: status
- **Description**: Update adoption inquiry status

#### Delete Inquiry
- **DELETE** `/adoption-inquiries/{id}`
- **Description**: Delete an adoption inquiry

## Data Models

### Enums

#### Shelter.ShelterStatus
- PENDING
- ACTIVE
- SUSPENDED
- INACTIVE

#### Pet.PetType
- DOG
- CAT
- BIRD
- RABBIT
- OTHER

#### Pet.Gender
- MALE
- FEMALE

#### Pet.Size
- SMALL
- MEDIUM
- LARGE
- EXTRA_LARGE

#### Pet.AdoptionStatus
- AVAILABLE
- PENDING
- ADOPTED
- UNAVAILABLE

#### Pet.HealthStatus
- HEALTHY
- NEEDS_MONITORING
- SICK
- RECOVERING

#### Pet.EnergyLevel
- LOW
- MEDIUM
- HIGH
- VERY_HIGH

#### CareLog.CareType
- FEEDING
- GROOMING
- MEDICAL
- EXERCISE
- TRAINING
- OTHER

#### AdoptionInquiry.InquiryStatus
- NEW
- CONTACTED
- IN_REVIEW
- APPROVED
- REJECTED
- COMPLETED

## Response Format

All API responses follow this format:
```json
{
  "success": boolean,
  "message": "string (optional)",
  "data": object/array (optional),
  "currentPage": number (for paginated responses),
  "totalPages": number (for paginated responses),
  "totalElements": number (for paginated responses),
  "size": number (for paginated responses)
}
```

## Error Handling

All endpoints handle errors gracefully and return appropriate HTTP status codes:
- 200: Success
- 201: Created
- 400: Bad Request
- 404: Not Found
- 500: Internal Server Error
