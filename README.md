# MediBook Online Appointment Booking System

# 1. MediBook Auth Service

Authentication and authorization microservice for the MediBook healthcare platform.

This service is responsible for secure user authentication, JWT token generation,
role-based authorization, OTP verification, password reset functionality,
and user identity management across the MediBook microservices ecosystem.

---

## Project Objective

The Auth Service provides a centralized authentication mechanism
for all MediBook platform users including:

- Patients
- Providers/Doctors
- Administrators

The service ensures secure communication between microservices
using JWT-based authentication and Spring Security.

---

## Core Features

- User Registration
- User Login Authentication
- JWT Token Generation & Validation
- Role-Based Authorization
- Secure Password Encryption
- Forgot Password Functionality
- OTP Verification System
- Password Reset Mechanism
- Spring Security Integration
- Swagger API Documentation
- RESTful API Architecture

---

## Architectural Goals

This microservice is designed with scalability, maintainability,
and clean architecture principles in mind.

Key architectural objectives include:

- Stateless Authentication
- Secure Token-Based Authorization
- Microservice-Oriented Design
- Separation of Concerns
- Centralized Authentication Logic
- Reusable Security Components
- Clean REST API Structure
- Exception Handling Standardization

---

## Technologies Used

### Backend

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- Maven

### Database

- MySQL

### Authentication & Security

- JWT (JSON Web Token)
- BCrypt Password Encoder

### API Documentation

- Swagger / OpenAPI

### Communication

- REST APIs
- OpenFeign Client

### DevOps & Deployment

- Docker
- Git & GitHub

---

## Project Structure

```text
src/
 |-- main/
 |   |-- java/com/medibook/auth/
 |   |   |-- config/
 |   |   |-- controller/
 |   |   |-- dto/
 |   |   |-- entity/
 |   |   |-- exception/
 |   |   |-- repository/
 |   |   |-- security/
 |   |   |-- service/
 |   |   `-- client/
 |   `-- resources/
 |
 `-- test/
```

---

# 2. MediBook Appointment Service

Appointment management microservice for the MediBook healthcare platform.

This service is responsible for booking appointments, cancelling appointments,
rescheduling appointments, updating appointment status, and managing appointment
history for patients and healthcare providers.

The Appointment Service works with the Schedule Service to block and release
provider availability slots during appointment booking and cancellation.

---

## Project Objective

The Appointment Service provides a centralized appointment management system
for the MediBook platform.

It supports appointment workflows for:

- Patients
- Providers/Doctors
- Administrators

The service ensures that appointment booking, cancellation, rescheduling,
and status updates are handled in a clean and consistent manner across
the MediBook microservices ecosystem.

---

## Core Features

- Book Appointments
- Cancel Appointments
- Reschedule Appointments
- Complete Appointments
- Update Appointment Status
- View Patient Appointments
- View Provider Appointments
- View Provider Appointments by Date
- View Upcoming Patient Appointments
- Count Provider Appointments
- Prevent Booking of Past Appointment Slots
- Schedule Slot Blocking Integration
- Schedule Slot Release on Cancellation
- RESTful API Architecture
- Swagger API Documentation
- Centralized Exception Handling

---

## Architectural Goals

This microservice is designed with scalability, maintainability,
and clean architecture principles in mind.

Key architectural objectives include:

- Microservice-Oriented Design
- Separation of Appointment Business Logic
- Clean REST API Structure
- Schedule Service Integration
- Consistent Appointment Status Management
- Reusable DTO-Based Communication
- Exception Handling Standardization
- Role-Aware API Gateway Integration
- Testable Service and Controller Layers

---

## Technologies Used

### Backend

- Java 17
- Spring Boot
- Spring Data JPA
- Hibernate
- Maven

### Database

- MySQL

### Security

- Spring Security
- JWT-Based Authentication through API Gateway

### API Documentation

- Swagger / OpenAPI

### Communication

- REST APIs
- OpenFeign Client

### Service Discovery

- Eureka Client

### Testing

- JUnit 5
- Mockito
- MockMvc
- Spring Boot Test
- JaCoCo

### DevOps & Deployment

- Docker
- Git & GitHub
- SonarQube

---

## Project Structure

```text
src/
 |-- main/
 |   |-- java/com/medibook/appointment/
 |   |   |-- client/
 |   |   |-- config/
 |   |   |-- controller/
 |   |   |-- dto/
 |   |   |-- entity/
 |   |   |-- exception/
 |   |   |-- repository/
 |   |   |-- service/
 |   |   `-- service/impl/
 |   `-- resources/
 |
 `-- test/
```

---

# 3. MediBook Provider Service

Provider profile management microservice for the MediBook healthcare platform.

This service is responsible for creating and managing provider/doctor profiles,
storing professional details, supporting provider search, managing availability,
tracking verification status, and exposing provider data required by patients
during appointment booking.

The Provider Service integrates with the Auth Service to fetch user identity
details and enrich provider responses with provider names.

---

## Project Objective

The Provider Service provides a centralized provider profile system
for the MediBook platform.

It supports provider-related workflows for:

- Patients
- Providers/Doctors
- Administrators

The service allows patients to browse providers by specialization, search
providers by keyword, view provider details, and check consultation information.
It also allows providers and administrators to manage provider profile data.

---

## Core Features

- Provider Profile Registration
- Provider Profile Update
- View Provider by Provider ID
- View Provider by User ID
- Search Providers by Keyword
- Filter Providers by Specialization
- View All Providers
- Provider Availability Management
- Provider Verification / Approval
- Provider Rating Update
- Consultation Fee Management
- Clinic Name and Address Management
- Auth Service Integration for User Details
- DTO-Based Request and Response Handling
- Swagger API Documentation
- RESTful API Architecture
- Centralized Exception Handling

---

## Architectural Goals

This microservice is designed with scalability, maintainability,
and clean architecture principles in mind.

Key architectural objectives include:

- Microservice-Oriented Design
- Separation of Provider Business Logic
- Clean REST API Structure
- Reusable DTO-Based Communication
- Auth Service Integration through Feign Client
- Consistent Provider Profile Management
- Searchable Provider Directory
- Role-Aware API Gateway Integration
- Exception Handling Standardization
- Testable Service and Controller Layers

---

## Technologies Used

### Backend

- Java 17
- Spring Boot
- Spring Data JPA
- Hibernate
- Maven

### Database

- MySQL

### Security

- Spring Security
- JWT-Based Authentication through API Gateway
- Role-Based Access Control

### API Documentation

- Swagger / OpenAPI

### Communication

- REST APIs
- OpenFeign Client

### Service Discovery

- Eureka Client

### Testing

- JUnit 5
- Mockito
- MockMvc
- Spring Boot Test
- JaCoCo

### DevOps & Deployment

- Docker
- Git & GitHub
- SonarQube

---

## Main Responsibilities

- Store provider professional profile information
- Link provider profiles with Auth Service users
- Return provider names by fetching user data from Auth Service
- Support public provider browsing for patients and guests
- Allow providers/admins to update provider profile details
- Allow admins to approve and delete provider profiles
- Maintain provider availability and average rating information

---

## Important API Endpoints

```text
POST   /providers/register
GET    /providers/{id}
GET    /providers/user/{userId}
PUT    /providers/{id}
DELETE /providers/{id}
PUT    /providers/{id}/approve
PUT    /providers/{id}/availability
PUT    /providers/{id}/rating
GET    /providers/specialization/{specialization}
GET    /providers/search?keyword=value
GET    /providers/getall
```

---

## Provider Profile Data

Provider profiles include:

- Provider ID
- User ID
- Provider Name
- Specialization
- Qualification
- Experience Years
- Bio
- Clinic Name
- Clinic Address
- Consultation Fee
- Average Rating
- Availability Status
- Verification Status

---

## Project Structure

```text
src/
 |-- main/
 |   |-- java/com/medibook/provider/
 |   |   |-- client/
 |   |   |-- config/
 |   |   |-- controller/
 |   |   |-- dto/
 |   |   |-- entity/
 |   |   |-- exception/
 |   |   |-- repository/
 |   |   |-- service/
 |   |   `-- service/impl/
 |   `-- resources/
 |
 `-- test/
```
# 4. MediBook Schedule Service

Provider availability and appointment slot management microservice for the MediBook healthcare platform.

This service is responsible for creating provider availability slots, fetching
available slots, blocking slots during appointment booking, and releasing slots
when appointments are cancelled or rescheduled.

The Schedule Service works closely with the Appointment Service to ensure that
patients can only book valid and available provider time slots.

---

## Project Objective

The Schedule Service provides a centralized scheduling system
for provider availability in the MediBook platform.

It supports schedule-related workflows for:

- Providers/Doctors
- Patients
- Administrators

The service ensures that appointment slots are created, fetched, blocked,
and released in a clean and consistent way across the MediBook microservices
ecosystem.

---

## Core Features

- Create Provider Availability Slots
- View Provider Available Slots
- View Slots by Provider and Date
- Block Slot During Appointment Booking
- Release Slot on Appointment Cancellation
- Prevent Double Booking of Slots
- Track Slot Availability Status
- Provider Schedule Management
- Appointment Service Integration
- RESTful API Architecture
- Swagger API Documentation
- Centralized Exception Handling

---

## Architectural Goals

This microservice is designed with scalability, maintainability,
and clean architecture principles in mind.

Key architectural objectives include:

- Microservice-Oriented Design
- Separation of Scheduling Business Logic
- Clean REST API Structure
- Reusable DTO-Based Communication
- Appointment Service Integration
- Consistent Slot Availability Management
- Prevention of Duplicate Slot Booking
- Role-Aware API Gateway Integration
- Exception Handling Standardization
- Testable Service and Controller Layers

---

## Technologies Used

### Backend

- Java 17
- Spring Boot
- Spring Data JPA
- Hibernate
- Maven

### Database

- MySQL

### Security

- Spring Security
- JWT-Based Authentication through API Gateway
- Role-Based Access Control

### API Documentation

- Swagger / OpenAPI

### Communication

- REST APIs
- OpenFeign Client

### Service Discovery

- Eureka Client

### Testing

- JUnit 5
- Mockito
- MockMvc
- Spring Boot Test
- JaCoCo

### DevOps & Deployment

- Docker
- Git & GitHub
- Jenkins
- SonarQube

---

## Main Responsibilities

- Store provider availability slot information
- Allow providers to create and manage available slots
- Allow patients to view available provider slots
- Block slots when appointments are booked
- Release slots when appointments are cancelled or rescheduled
- Prevent patients from booking already blocked slots
- Maintain accurate slot availability status

---

## Important API Endpoints

```text
POST   /schedules
GET    /schedules/provider/{providerId}
GET    /schedules/provider/{providerId}/date/{date}
GET    /schedules/available/provider/{providerId}
PUT    /schedules/{slotId}/block
PUT    /schedules/{slotId}/release
DELETE /schedules/{slotId}
```

---

## Schedule Slot Data

Schedule slots include:

- Slot ID
- Provider ID
- Available Date
- Start Time
- End Time
- Availability Status
- Created Date

---

## Slot Status Flow

```text
AVAILABLE
   |
   | appointment booked
   v
BLOCKED
   |
   | appointment cancelled / rescheduled
   v
AVAILABLE
```

---

## Project Structure

```text
src/
 |-- main/
 |   |-- java/com/medibook/schedule/
 |   |   |-- config/
 |   |   |-- controller/
 |   |   |-- dto/
 |   |   |-- entity/
 |   |   |-- exception/
 |   |   |-- repository/
 |   |   |-- service/
 |   |   `-- service/impl/
 |   `-- resources/
 |
 `-- test/
```
