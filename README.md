# MediBook Online Appointment Booking System
# MediBook Auth Service

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