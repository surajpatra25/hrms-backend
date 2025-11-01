# Entity Documentation

## Overview
This document describes all entities in the HRMS backend system, their relationships, and database schema.

## Core Entities

### User (Base Entity)
**Package:** `io.kodlama.hrms.core.entities`

The base user entity that all user types extend from.

**Fields:**
- `id` (int): Primary key
- `email` (String): User email address
- `password` (String): Encrypted password

---

## User Type Entities

### Candidate
**Package:** `io.kodlama.hrms.entities.concretes`
**Table:** `candidates`
**Extends:** User

Represents job candidates in the system.

**Fields:**
- `id` (int): Primary key (inherited from User)
- `email` (String): Email address (inherited from User)
- `password` (String): Password (inherited from User)
- `firstName` (String): First name
- `lastName` (String): Last name
- `identityNumber` (String): National identity number
- `dateOfBirth` (LocalDate): Date of birth

**Relationships:**
- One-to-One with `UserActivation`

### Employer
**Package:** `io.kodlama.hrms.entities.concretes`
**Table:** `employers`
**Extends:** User

Represents employers/companies in the system.

**Fields:**
- `id` (int): Primary key (inherited from User)
- `email` (String): Email address (inherited from User)
- `password` (String): Password (inherited from User)
- `companyName` (String): Company name
- `webAddress` (String): Company website
- `phoneNumber` (String): Contact phone number

**Relationships:**
- One-to-One with `UserActivation`
- One-to-Many with `UserConfirmation`

### CompanyStaff
**Package:** `io.kodlama.hrms.entities.concretes`
**Table:** `company_staffs`
**Extends:** User

Represents company staff members.

**Fields:**
- `id` (int): Primary key (inherited from User)
- `email` (String): Email address (inherited from User)
- `password` (String): Password (inherited from User)
- `firstName` (String): First name
- `lastName` (String): Last name

---

## Leave Management Entities

### Leave
**Package:** `io.kodlama.hrms.entities.concretes`
**Table:** `leaves`

Represents leave applications in the system.

**Fields:**
- `id` (int): Primary key
- `employee` (User): Employee who applied for leave
- `startDate` (LocalDate): Leave start date
- `endDate` (LocalDate): Leave end date
- `leaveType` (LeaveType): Type of leave (enum)
- `reason` (String): Reason for leave
- `status` (LeaveStatus): Current status (enum)
- `appliedDate` (LocalDate): Date when leave was applied
- `approvedBy` (Integer): ID of person who approved/rejected
- `approvedDate` (LocalDate): Date when leave was approved/rejected
- `rejectionReason` (String): Reason for rejection (if rejected)

**Enums:**
- `LeaveType`: ANNUAL, SICK, PERSONAL, EMERGENCY, MATERNITY, PATERNITY
- `LeaveStatus`: PENDING, APPROVED, REJECTED, CANCELLED

**Relationships:**
- Many-to-One with `User` (employee)

### LeaveBalance
**Package:** `io.kodlama.hrms.entities.concretes`
**Table:** `leave_balances`

Tracks annual leave allocation and usage per employee per year.

**Fields:**
- `id` (int): Primary key
- `employee` (User): Employee
- `year` (int): Year for which balance is tracked
- `totalAnnualLeaves` (int): Total annual leaves allocated (default: 15)
- `usedLeaves` (int): Number of leaves used
- `remainingLeaves` (int): Number of leaves remaining
- `createdDate` (LocalDate): When balance record was created
- `updatedDate` (LocalDate): When balance was last updated

**Relationships:**
- Many-to-One with `User` (employee)

---

## Job Management Entities

### JobPosting
**Package:** `io.kodlama.hrms.entities.concretes`
**Table:** `job_postings`

Represents job postings created by employers.

**Fields:**
- `id` (int): Primary key
- `jobTitle` (JobTitle): Job title
- `jobDescription` (String): Job description
- `city` (City): Job location
- `minSalary` (int): Minimum salary
- `maxSalary` (int): Maximum salary
- `openPositions` (int): Number of open positions
- `applicationDeadline` (LocalDate): Application deadline
- `isActive` (boolean): Whether posting is active
- `createdDate` (LocalDate): When posting was created
- `employer` (Employer): Employer who created the posting

**Relationships:**
- Many-to-One with `JobTitle`
- Many-to-One with `City`
- Many-to-One with `Employer`

### JobTitle
**Package:** `io.kodlama.hrms.entities.concretes`
**Table:** `job_titles`

Represents different job titles/positions.

**Fields:**
- `id` (int): Primary key
- `title` (String): Job title name

### City
**Package:** `io.kodlama.hrms.entities.concretes`
**Table:** `cities`

Represents cities where jobs are located.

**Fields:**
- `id` (int): Primary key
- `name` (String): City name

---

## Resume Management Entities

### Resume
**Package:** `io.kodlama.hrms.entities.concretes`
**Table:** `resumes`

Represents candidate resumes.

**Fields:**
- `id` (int): Primary key
- `candidate` (Candidate): Candidate who owns the resume
- `coverLetter` (CoverLetter): Cover letter (optional)
- `githubLink` (String): GitHub profile link
- `linkedinLink` (String): LinkedIn profile link
- `createdDate` (LocalDate): When resume was created

**Relationships:**
- Many-to-One with `Candidate`
- One-to-One with `CoverLetter`
- One-to-Many with `Education`
- One-to-Many with `Experience`
- One-to-Many with `Language`
- One-to-Many with `Skill`
- One-to-Many with `Link`

### Education
**Package:** `io.kodlama.hrms.entities.concretes`
**Table:** `educations`

Represents educational background.

**Fields:**
- `id` (int): Primary key
- `resume` (Resume): Resume this education belongs to
- `schoolName` (String): School/university name
- `department` (String): Department/major
- `startDate` (LocalDate): Start date
- `endDate` (LocalDate): End date (null if ongoing)
- `isGraduated` (boolean): Whether graduated

### Experience
**Package:** `io.kodlama.hrms.entities.concretes`
**Table:** `experiences`

Represents work experience.

**Fields:**
- `id` (int): Primary key
- `resume` (Resume): Resume this experience belongs to
- `companyName` (String): Company name
- `position` (String): Job position
- `startDate` (LocalDate): Start date
- `endDate` (LocalDate): End date (null if current)
- `isStillWorking` (boolean): Whether still working there
- `jobDescription` (String): Job description

### Skill
**Package:** `io.kodlama.hrms.entities.concretes`
**Table:** `skills`

Represents candidate skills.

**Fields:**
- `id` (int): Primary key
- `resume` (Resume): Resume this skill belongs to
- `skillName` (String): Skill name

### Language
**Package:** `io.kodlama.hrms.entities.concretes`
**Table:** `languages`

Represents language skills.

**Fields:**
- `id` (int): Primary key
- `resume` (Resume): Resume this language belongs to
- `languageName` (String): Language name
- `languageLevel` (LanguageLevel): Proficiency level

### LanguageLevel
**Package:** `io.kodlama.hrms.entities.concretes`
**Table:** `language_levels`

Represents language proficiency levels.

**Fields:**
- `id` (int): Primary key
- `level` (String): Level name (e.g., "Beginner", "Intermediate", "Advanced")

---

## System Management Entities

### UserActivation
**Package:** `io.kodlama.hrms.entities.concretes`
**Table:** `user_activations`

Handles user account activation.

**Fields:**
- `id` (int): Primary key
- `user` (User): User to be activated
- `activationCode` (String): Activation code
- `isActivated` (boolean): Whether account is activated
- `activationDate` (LocalDateTime): When account was activated

### UserConfirmation
**Package:** `io.kodlama.hrms.entities.concretes`
**Table:** `user_confirmations`

Handles user confirmation processes.

**Fields:**
- `id` (int): Primary key
- `user` (User): User being confirmed
- `companyStaff` (CompanyStaff): Staff member confirming
- `userConfirmationType` (UserConfirmationType): Type of confirmation
- `isConfirmed` (boolean): Whether confirmed
- `isConfirmedDate` (LocalDateTime): When confirmed

### UserConfirmationType
**Package:** `io.kodlama.hrms.entities.concretes`
**Table:** `user_confirmation_types`

Types of user confirmations.

**Fields:**
- `id` (int): Primary key
- `type` (String): Confirmation type name

---

## Entity Relationships Diagram

```
User (Base)
├── Candidate
│   ├── Resume
│   │   ├── Education
│   │   ├── Experience
│   │   ├── Skill
│   │   ├── Language
│   │   └── Link
│   └── UserActivation
├── Employer
│   ├── JobPosting
│   ├── UserActivation
│   └── UserConfirmation
└── CompanyStaff
    └── UserConfirmation

Leave Management:
├── Leave (references User as employee)
└── LeaveBalance (references User as employee)

System Entities:
├── UserActivation (references User)
├── UserConfirmation (references User, CompanyStaff, UserConfirmationType)
├── UserConfirmationType
├── JobTitle
├── City
├── LanguageLevel
└── CoverLetter
```

---

## Database Schema Notes

### Primary Keys
- All entities use auto-incrementing integer primary keys
- User subclasses use `@PrimaryKeyJoinColumn` to reference the base User table

### Foreign Keys
- All foreign key relationships are properly defined with `@JoinColumn`
- Cascade operations are configured appropriately for parent-child relationships

### Indexes
- Consider adding indexes on frequently queried fields:
  - `leaves.employee_id`
  - `leaves.status`
  - `leaves.start_date`
  - `leave_balances.employee_id`
  - `leave_balances.year`

### Constraints
- Email addresses should be unique across all user types
- Identity numbers should be unique for candidates
- Leave dates should have proper validation (start_date <= end_date)
- Leave balance should not go negative
