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
---

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
---
# 5. MediBook Notification Service

Notification and email communication microservice for the MediBook healthcare platform.

This service is responsible for sending user notifications, appointment-related
messages, OTP emails, password reset communication, and general platform alerts
across the MediBook microservices ecosystem.

The Notification Service works with other MediBook services to deliver important
messages to patients, providers, and administrators through email and internal
notification records.

---

## Project Objective

The Notification Service provides a centralized communication system
for the MediBook platform.

It supports notification workflows for:

- Patients
- Providers/Doctors
- Administrators

The service ensures that appointment updates, OTP messages, password reset
emails, and platform notifications are delivered in a clean and consistent way
across the MediBook microservices ecosystem.

---

## Core Features

- Send Email Notifications
- Send OTP Emails
- Send Password Reset Emails
- Send Appointment Confirmation Messages
- Send Appointment Cancellation Messages
- Send Appointment Status Update Messages
- Store User Notifications
- View Notifications by User
- Mark Notifications as Read
- Bulk Notification Support
- RabbitMQ Message Consumer
- RESTful API Architecture
- Swagger API Documentation
- Centralized Exception Handling

---

## Architectural Goals

This microservice is designed with scalability, maintainability,
and clean architecture principles in mind.

Key architectural objectives include:

- Microservice-Oriented Design
- Centralized Notification Handling
- Separation of Email and Notification Logic
- Clean REST API Structure
- Event-Driven Communication
- RabbitMQ-Based Message Consumption
- Reusable DTO-Based Communication
- Consistent Notification Delivery
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

### Messaging

- RabbitMQ
- Spring AMQP

### Email

- Spring Mail
- SMTP

### Security

- Spring Security
- JWT-Based Authentication through API Gateway
- Role-Based Access Control

### API Documentation

- Swagger / OpenAPI

### Communication

- REST APIs
- RabbitMQ Events

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

- Send transactional emails to MediBook users
- Send OTP emails for verification and password reset
- Store notification records for users
- Provide notification history by user ID
- Mark notifications as read after user interaction
- Consume notification events from RabbitMQ
- Support communication between appointment, auth, payment, and record services
- Centralize notification delivery logic

---

## Important API Endpoints

```text
POST   /notifications/send
POST   /notifications/email
POST   /notifications/bulk
GET    /notifications/user/{userId}
PUT    /notifications/{notificationId}/read
DELETE /notifications/{notificationId}
```

---

## Notification Data

Notifications include:

- Notification ID
- User ID
- Recipient Email
- Subject
- Message
- Notification Type
- Read Status
- Created Date

---

## Notification Flow

```text
Service Event
   |
   | RabbitMQ / REST API
   v
Notification Service
   |
   | Save notification record
   v
Database
   |
   | Send email when required
   v
User Email Inbox
```

---

## Common Notification Types

- OTP Verification
- Password Reset
- Appointment Booking
- Appointment Cancellation
- Appointment Reschedule
- Appointment Completion
- Payment Success
- Payment Refund
- Medical Report Added
- General Platform Alert

---

## Project Structure

```text
src/
 |-- main/
 |   |-- java/com/medibook/notification/
 |   |   |-- config/
 |   |   |-- consumer/
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
# 6. MediBook Record Service

Medical records and medical reports management microservice for the MediBook healthcare platform.

This service is responsible for managing patient medical records, provider-created
medical reports, diagnosis details, prescriptions, notes, and appointment-linked
healthcare documentation across the MediBook microservices ecosystem.

The Record Service allows providers to create medical reports for patients and
allows patients to securely view their own reports and records.

---

## Project Objective

The Record Service provides a centralized medical documentation system
for the MediBook platform.

It supports record-related workflows for:

- Patients
- Providers/Doctors
- Administrators

The service ensures that medical records and reports are stored, retrieved,
and protected in a clean and consistent way across the MediBook microservices
ecosystem.

---

## Core Features

- Create Medical Records
- View Medical Records by Patient
- View Medical Records by Provider
- Create Medical Reports
- View Medical Reports by Patient
- View Medical Reports by Provider
- View Medical Report by Appointment
- Store Diagnosis Information
- Store Prescription Information
- Store Provider Notes
- Track Report Date
- Appointment-Linked Medical Reports
- Patient-Restricted Report Access
- Provider-Restricted Report Creation
- RESTful API Architecture
- Swagger API Documentation
- Centralized Exception Handling

---

## Architectural Goals

This microservice is designed with scalability, maintainability,
and clean architecture principles in mind.

Key architectural objectives include:

- Microservice-Oriented Design
- Separation of Medical Record Business Logic
- Clean REST API Structure
- Secure Patient Medical Data Access
- Provider-Based Report Creation
- Appointment-Linked Healthcare Documentation
- Reusable DTO-Based Communication
- Consistent Medical Report Management
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
- Patient-Owned Data Access Restriction

### API Documentation

- Swagger / OpenAPI

### Communication

- REST APIs

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

- Store patient medical record information
- Store provider-created medical reports
- Link reports with patients, providers, and appointments
- Allow patients to view only their own reports
- Allow providers to create reports for assigned patients
- Maintain diagnosis, prescription, notes, and report date details
- Provide medical documentation APIs for frontend report pages
- Protect sensitive healthcare data through role-aware access control

---

## Important API Endpoints

```text
POST   /records
GET    /records/patient/{patientId}
GET    /records/provider/{providerId}
GET    /records/{recordId}

POST   /reports
GET    /reports/patient/{patientId}
GET    /reports/provider/{providerId}
GET    /reports/appointment/{appointmentId}
GET    /reports/{reportId}
```

---

## Medical Record Data

Medical records include:

- Record ID
- Patient ID
- Provider ID
- Appointment ID
- Diagnosis
- Prescription
- Notes
- Created Date
- Updated Date

---

## Medical Report Data

Medical reports include:

- Report ID
- Patient ID
- Provider ID
- Appointment ID
- Diagnosis
- Prescription
- Notes
- Report Date
- Provider Name

---

## Medical Report Flow

```text
Appointment Completed
   |
   | provider creates report
   v
Medical Report Saved
   |
   | patient opens reports section
   v
Patient Views Own Report
```

---

## Access Control Rules

```text
PATIENT
   - Can view only their own records and reports

PROVIDER
   - Can create reports for assigned appointments/patients
   - Can view reports related to their own patients

ADMIN
   - Can manage and monitor medical records where allowed
```

---

## Project Structure

```text
src/
 |-- main/
 |   |-- java/com/medibook/record/
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
# 7. MediBook Record Service

Medical records and medical reports management microservice for the MediBook healthcare platform.

This service is responsible for managing patient medical records, provider-created
medical reports, diagnosis details, prescriptions, notes, and appointment-linked
healthcare documentation across the MediBook microservices ecosystem.

The Record Service allows providers to create medical reports for patients and
allows patients to securely view their own reports and records.

---

## Project Objective

The Record Service provides a centralized medical documentation system
for the MediBook platform.

It supports record-related workflows for:

- Patients
- Providers/Doctors
- Administrators

The service ensures that medical records and reports are stored, retrieved,
and protected in a clean and consistent way across the MediBook microservices
ecosystem.

---

## Core Features

- Create Medical Records
- View Medical Records by Patient
- View Medical Records by Provider
- Create Medical Reports
- View Medical Reports by Patient
- View Medical Reports by Provider
- View Medical Report by Appointment
- Store Diagnosis Information
- Store Prescription Information
- Store Provider Notes
- Track Report Date
- Appointment-Linked Medical Reports
- Patient-Restricted Report Access
- Provider-Restricted Report Creation
- RESTful API Architecture
- Swagger API Documentation
- Centralized Exception Handling

---

## Architectural Goals

This microservice is designed with scalability, maintainability,
and clean architecture principles in mind.

Key architectural objectives include:

- Microservice-Oriented Design
- Separation of Medical Record Business Logic
- Clean REST API Structure
- Secure Patient Medical Data Access
- Provider-Based Report Creation
- Appointment-Linked Healthcare Documentation
- Reusable DTO-Based Communication
- Consistent Medical Report Management
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
- Patient-Owned Data Access Restriction

### API Documentation

- Swagger / OpenAPI

### Communication

- REST APIs

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

- Store patient medical record information
- Store provider-created medical reports
- Link reports with patients, providers, and appointments
- Allow patients to view only their own reports
- Allow providers to create reports for assigned patients
- Maintain diagnosis, prescription, notes, and report date details
- Provide medical documentation APIs for frontend report pages
- Protect sensitive healthcare data through role-aware access control

---

## Important API Endpoints

```text
POST   /records
GET    /records/patient/{patientId}
GET    /records/provider/{providerId}
GET    /records/{recordId}

POST   /reports
GET    /reports/patient/{patientId}
GET    /reports/provider/{providerId}
GET    /reports/appointment/{appointmentId}
GET    /reports/{reportId}
```

---

## Medical Record Data

Medical records include:

- Record ID
- Patient ID
- Provider ID
- Appointment ID
- Diagnosis
- Prescription
- Notes
- Created Date
- Updated Date

---

## Medical Report Data

Medical reports include:

- Report ID
- Patient ID
- Provider ID
- Appointment ID
- Diagnosis
- Prescription
- Notes
- Report Date
- Provider Name

---

## Medical Report Flow

```text
Appointment Completed
   |
   | provider creates report
   v
Medical Report Saved
   |
   | patient opens reports section
   v
Patient Views Own Report
```

---

## Access Control Rules

```text
PATIENT
   - Can view only their own records and reports

PROVIDER
   - Can create reports for assigned appointments/patients
   - Can view reports related to their own patients

ADMIN
   - Can manage and monitor medical records where allowed
```

---

## Project Structure

```text
src/
 |-- main/
 |   |-- java/com/medibook/record/
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
# 8. MediBook Payment Service

Payment and refund management microservice for the MediBook healthcare platform.

This service is responsible for handling appointment payments, creating payment
orders, storing payment transactions, verifying payment status, managing refunds,
and supporting patient/provider dashboard payment summaries.

The Payment Service works with appointment booking and cancellation workflows to
ensure that appointment fees, successful payments, failed payments, and refunds
are tracked consistently across the MediBook platform.

---

## Project Objective

The Payment Service provides a centralized payment management system
for the MediBook platform.

It supports payment-related workflows for:

- Patients
- Providers/Doctors
- Administrators

The service ensures that payments, payment status updates, refund records,
and payment history are handled in a clean and consistent way across
the MediBook microservices ecosystem.

---

## Core Features

- Create Payment Orders
- Process Appointment Payments
- Store Payment Transactions
- Verify Payment Status
- View Payments by Patient
- View Payments by Provider
- View Payment by Appointment
- Handle Payment Success
- Handle Payment Failure
- Process Refunds on Appointment Cancellation
- Track Refund Transactions
- Dashboard Payment Summary Support
- Razorpay Payment Gateway Integration
- RESTful API Architecture
- Swagger API Documentation
- Centralized Exception Handling

---

## Architectural Goals

This microservice is designed with scalability, maintainability,
and clean architecture principles in mind.

Key architectural objectives include:

- Microservice-Oriented Design
- Separation of Payment Business Logic
- Clean REST API Structure
- Reusable DTO-Based Communication
- External Payment Gateway Integration
- Consistent Payment Status Management
- Refund Tracking and Auditability
- Appointment Cancellation Integration
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

### Payment Gateway

- Razorpay

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

- Create and store payment records for appointments
- Integrate with Razorpay for payment order creation
- Track payment success and failure states
- Return payment history for patients and providers
- Support appointment-based payment lookup
- Process refunds when appointments are cancelled
- Maintain refund transaction information
- Provide payment summary data for dashboards
- Keep payment and refund records consistent with appointment status

---

## Important API Endpoints

```text
POST   /payments/create-order
POST   /payments/success
POST   /payments/failure
POST   /payments/refund
GET    /payments/{paymentId}
GET    /payments/appointment/{appointmentId}
GET    /payments/patient/{patientId}
GET    /payments/provider/{providerId}
GET    /payments/dashboard/patient/{patientId}
GET    /payments/dashboard/provider/{providerId}
```

---

## Payment Data

Payment records include:

- Payment ID
- Appointment ID
- Patient ID
- Provider ID
- Amount
- Payment Status
- Payment Method
- Razorpay Order ID
- Razorpay Payment ID
- Transaction Date

---

## Refund Data

Refund records include:

- Refund ID
- Payment ID
- Appointment ID
- Patient ID
- Provider ID
- Refund Amount
- Refund Status
- Refund Reason
- Refund Date

---

## Payment Status Flow

```text
ORDER_CREATED
   |
   | payment completed
   v
SUCCESS
```

```text
ORDER_CREATED
   |
   | payment failed
   v
FAILED
```

```text
SUCCESS
   |
   | appointment cancelled
   v
REFUNDED
```

---

## Dashboard Calculations

The Payment Service supports dashboard values such as:

- Total Amount Spent by Patient
- Total Provider Earnings
- Successful Payment Count
- Failed Payment Count
- Refund Count
- Total Refund Amount
- Recent Payment Transactions
- Recent Refund Transactions

When a patient cancels an appointment, the related amount should be removed
from total spending/earning calculations where required and added to refund
summary values.

---

## Access Control Rules

```text
PATIENT
   - Can create payments for their own appointments
   - Can view their own payment history
   - Can view their own refund history

PROVIDER
   - Can view payments related to their appointments
   - Can view provider dashboard earnings

ADMIN
   - Can monitor payment and refund records where allowed
```

---

## Project Structure

```text
src/
 |-- main/
 |   |-- java/com/medibook/payment/
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
