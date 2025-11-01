# HRMS Backend API Documentation

## Overview
This document provides comprehensive API documentation for the HRMS (Human Resource Management System) backend built with Spring Boot.

## Base URL
```
http://localhost:8080/api
```

## Authentication
Currently, the system uses basic authentication. All endpoints require proper user credentials.

## API Endpoints

### 1. Authentication (`/api/auth`)
- [POST /registerCandidate](#post-registercandidate)
- [POST /registerEmployer](#post-registeremployer)
- [POST /registerCompanyStaff](#post-registercompanystaff)

### 2. Candidates (`/api/candidates`)
- [GET /getAll](#get-candidates-getall)
- [GET /getById](#get-candidates-getbyid)
- [PUT /update](#put-candidates-update)
- [PUT /activate](#put-candidates-activate)
- [GET /getAllByIsActivated](#get-candidates-getallbyisactivated)

### 3. Leave Management (`/api/leaves`)
- [POST /apply](#post-leaves-apply)
- [GET /balance](#get-leaves-balance)
- [GET /remaining](#get-leaves-remaining)
- [GET /employee](#get-leaves-employee)
- [GET /employee/status](#get-leaves-employee-status)
- [GET /employee/type](#get-leaves-employee-type)
- [GET /employee/dateRange](#get-leaves-employee-daterange)
- [GET /employee/year](#get-leaves-employee-year)
- [PUT /approve](#put-leaves-approve)
- [PUT /reject](#put-leaves-reject)
- [PUT /cancel](#put-leaves-cancel)
- [GET /pending](#get-leaves-pending)
- [GET /all](#get-leaves-all)
- [GET /getById](#get-leaves-getbyid)

---

## Authentication Endpoints

### POST /registerCandidate
Register a new candidate.

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "password123",
  "identityNumber": "12345678901",
  "dateOfBirth": "1990-01-01"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Candidate registered successfully"
}
```

### POST /registerEmployer
Register a new employer.

**Request Body:**
```json
{
  "companyName": "Tech Corp",
  "email": "hr@techcorp.com",
  "password": "password123",
  "webAddress": "https://techcorp.com",
  "phoneNumber": "+1234567890"
}
```

### POST /registerCompanyStaff
Register a new company staff member.

**Request Body:**
```json
{
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane.smith@techcorp.com",
  "password": "password123"
}
```

---

## Candidate Endpoints

### GET /candidates/getAll
Get all candidates.

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe",
      "email": "john.doe@example.com",
      "identityNumber": "12345678901",
      "dateOfBirth": "1990-01-01"
    }
  ],
  "message": "All candidates listed successfully"
}
```

### GET /candidates/getById
Get candidate by ID.

**Parameters:**
- `id` (int): Candidate ID

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com"
  },
  "message": "Candidate found successfully"
}
```

### PUT /candidates/update
Update candidate information.

**Request Body:**
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com"
}
```

### PUT /candidates/activate
Activate candidate account.

**Parameters:**
- `code` (string): Activation code

### GET /candidates/getAllByIsActivated
Get candidates by activation status.

**Parameters:**
- `isActivated` (boolean): Activation status

---

## Leave Management Endpoints

### POST /leaves/apply
Apply for leave.

**Parameters:**
- `employeeId` (int): Employee ID
- `startDate` (string): Start date (YYYY-MM-DD)
- `endDate` (string): End date (YYYY-MM-DD)
- `leaveType` (string): Leave type (ANNUAL, SICK, PERSONAL, EMERGENCY, MATERNITY, PATERNITY)
- `reason` (string, optional): Reason for leave

**Example:**
```
POST /api/leaves/apply?employeeId=1&startDate=2024-02-01&endDate=2024-02-05&leaveType=ANNUAL&reason=Vacation
```

**Response:**
```json
{
  "success": true,
  "message": "Leave application submitted successfully"
}
```

### GET /leaves/balance
Get leave balance for an employee.

**Parameters:**
- `employeeId` (int): Employee ID
- `year` (int, optional): Year (defaults to current year)

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "employee": {
      "id": 1,
      "email": "employee@example.com"
    },
    "year": 2024,
    "totalAnnualLeaves": 15,
    "usedLeaves": 5,
    "remainingLeaves": 10
  },
  "message": "Leave balance retrieved successfully"
}
```

### GET /leaves/remaining
Get remaining leave balance.

**Parameters:**
- `employeeId` (int): Employee ID
- `year` (int, optional): Year (defaults to current year)

**Response:**
```json
{
  "success": true,
  "data": 10,
  "message": "Remaining leave balance retrieved successfully"
}
```

### GET /leaves/employee
Get all leaves for an employee.

**Parameters:**
- `employeeId` (int): Employee ID

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "startDate": "2024-02-01",
      "endDate": "2024-02-05",
      "leaveType": "ANNUAL",
      "reason": "Vacation",
      "status": "APPROVED",
      "appliedDate": "2024-01-15"
    }
  ],
  "message": "Employee leaves listed successfully"
}
```

### GET /leaves/employee/status
Get employee leaves by status.

**Parameters:**
- `employeeId` (int): Employee ID
- `status` (string): Leave status (PENDING, APPROVED, REJECTED, CANCELLED)

### GET /leaves/employee/type
Get employee leaves by type.

**Parameters:**
- `employeeId` (int): Employee ID
- `leaveType` (string): Leave type (ANNUAL, SICK, PERSONAL, EMERGENCY, MATERNITY, PATERNITY)

### GET /leaves/employee/dateRange
Get employee leaves by date range.

**Parameters:**
- `employeeId` (int): Employee ID
- `startDate` (string): Start date (YYYY-MM-DD)
- `endDate` (string): End date (YYYY-MM-DD)

### GET /leaves/employee/year
Get employee leaves for a specific year.

**Parameters:**
- `employeeId` (int): Employee ID
- `year` (int): Year

### PUT /leaves/approve
Approve a leave application.

**Parameters:**
- `leaveId` (int): Leave ID
- `approvedBy` (int): Approver ID

**Response:**
```json
{
  "success": true,
  "message": "Leave application approved successfully"
}
```

### PUT /leaves/reject
Reject a leave application.

**Parameters:**
- `leaveId` (int): Leave ID
- `approvedBy` (int): Approver ID
- `rejectionReason` (string): Reason for rejection

### PUT /leaves/cancel
Cancel a pending leave application.

**Parameters:**
- `leaveId` (int): Leave ID
- `employeeId` (int): Employee ID

### GET /leaves/pending
Get all pending leave applications.

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 2,
      "employee": {
        "id": 1,
        "email": "employee@example.com"
      },
      "startDate": "2024-03-01",
      "endDate": "2024-03-03",
      "leaveType": "SICK",
      "reason": "Medical appointment",
      "status": "PENDING",
      "appliedDate": "2024-02-20"
    }
  ],
  "message": "All pending leaves listed successfully"
}
```

### GET /leaves/all
Get all leave applications.

### GET /leaves/getById
Get leave by ID.

**Parameters:**
- `id` (int): Leave ID

---

## Error Responses

All endpoints may return error responses in the following format:

```json
{
  "success": false,
  "message": "Error description",
  "data": null
}
```

### Common HTTP Status Codes
- `200 OK` - Request successful
- `400 Bad Request` - Invalid request parameters
- `401 Unauthorized` - Authentication required
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

---

## Leave Management Business Rules

### Leave Types
- **ANNUAL**: Regular vacation leave (counts against annual balance)
- **SICK**: Medical leave (does not count against annual balance)
- **PERSONAL**: Personal leave (counts against annual balance)
- **EMERGENCY**: Emergency leave (counts against annual balance)
- **MATERNITY**: Maternity leave (does not count against annual balance)
- **PATERNITY**: Paternity leave (does not count against annual balance)

### Leave Status
- **PENDING**: Awaiting approval
- **APPROVED**: Approved by manager
- **REJECTED**: Rejected by manager
- **CANCELLED**: Cancelled by employee

### Business Rules
1. Each employee gets 15 annual leaves per year
2. Only annual, personal, and emergency leaves count against the annual balance
3. Maximum 3 pending leave applications per employee
4. Cannot apply for leave in the past
5. Cannot have overlapping leave periods
6. Only pending leaves can be cancelled
7. Leave balance is automatically updated when leave is approved
