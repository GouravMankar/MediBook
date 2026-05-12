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

````text
src/
 ├── main/
 │   ├── java/com/medibook/auth/
 │   │   ├── config/
 │   │   ├── controller/
 │   │   ├── dto/
 │   │   ├── entity/
 │   │   ├── exception/
 │   │   ├── repository/
 │   │   ├── security/
 │   │   ├── service/
 │   │   └── client/
 │   └── resources/
 │
 └── test/
text````
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
- Jenkins
- SonarQube

---

## Project Structure

```text
src/
 ├── main/
 │   ├── java/com/medibook/appointment/
 │   │   ├── client/
 │   │   ├── config/
 │   │   ├── controller/
 │   │   ├── dto/
 │   │   ├── entity/
 │   │   ├── exception/
 │   │   ├── repository/
 │   │   ├── service/
 │   │   └── service/impl/
 │   └── resources/
 │
 └── test/

---
````
