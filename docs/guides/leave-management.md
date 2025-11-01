# Leave Management System Documentation

## Overview

The Leave Management System is a comprehensive module within the HRMS backend that handles employee leave applications, approvals, and tracking. It provides a complete solution for managing various types of leave with proper validation, approval workflows, and balance tracking.

## Features

### Core Features
- ✅ **Leave Application**: Employees can apply for different types of leave
- ✅ **Leave Balance Tracking**: Automatic tracking of annual leave allocation and usage
- ✅ **Approval Workflow**: Manager approval/rejection system
- ✅ **Leave Cancellation**: Employees can cancel pending applications
- ✅ **Multiple Leave Types**: Support for various leave categories
- ✅ **Date Validation**: Prevents invalid date ranges and overlaps
- ✅ **Balance Validation**: Ensures sufficient leave balance for annual leaves

### Business Rules
- Each employee gets **15 annual leaves** per year
- Maximum **3 pending applications** per employee
- Cannot apply for leave in the past
- Cannot have overlapping leave periods
- Only annual, personal, and emergency leaves count against balance
- Leave balance is automatically updated when approved

## System Architecture

### Components

#### 1. Entities
- **Leave**: Stores leave applications and their details
- **LeaveBalance**: Tracks annual leave allocation and usage

#### 2. Data Access Layer
- **LeaveDao**: Repository for leave operations
- **LeaveBalanceDao**: Repository for leave balance management

#### 3. Business Layer
- **LeaveService**: Interface defining business operations
- **LeaveManager**: Implementation with business logic

#### 4. API Layer
- **LeaveController**: REST endpoints for leave operations

## Leave Types

### Annual Leave
- **Description**: Regular vacation leave
- **Balance Impact**: Counts against annual leave balance
- **Default Allocation**: 15 days per year
- **Validation**: Requires sufficient balance

### Sick Leave
- **Description**: Medical leave for illness
- **Balance Impact**: Does not count against annual balance
- **Validation**: No balance check required
- **Documentation**: May require medical certificates

### Personal Leave
- **Description**: Personal time off
- **Balance Impact**: Counts against annual leave balance
- **Validation**: Requires sufficient balance

### Emergency Leave
- **Description**: Urgent personal matters
- **Balance Impact**: Counts against annual leave balance
- **Validation**: Requires sufficient balance
- **Approval**: May require immediate approval

### Maternity Leave
- **Description**: Leave for new mothers
- **Balance Impact**: Does not count against annual balance
- **Validation**: No balance check required
- **Duration**: Typically longer periods

### Paternity Leave
- **Description**: Leave for new fathers
- **Balance Impact**: Does not count against annual balance
- **Validation**: No balance check required
- **Duration**: Typically shorter periods

## Leave Status Flow

```
PENDING → APPROVED
    ↓
REJECTED
    ↓
CANCELLED (by employee)
```

### Status Descriptions

#### PENDING
- Initial status when leave is applied
- Awaiting manager approval
- Can be cancelled by employee
- Counts towards pending limit (max 3)

#### APPROVED
- Leave has been approved by manager
- Balance is deducted (for applicable leave types)
- Cannot be cancelled
- Employee can take the leave

#### REJECTED
- Leave has been rejected by manager
- No balance deduction
- Cannot be cancelled
- Includes rejection reason

#### CANCELLED
- Leave was cancelled by employee
- Only possible for PENDING status
- No balance deduction
- Frees up pending slot

## API Endpoints

### Employee Endpoints

#### Apply for Leave
```http
POST /api/leaves/apply
```
**Parameters:**
- `employeeId` (int): Employee ID
- `startDate` (string): Start date (YYYY-MM-DD)
- `endDate` (string): End date (YYYY-MM-DD)
- `leaveType` (string): Leave type
- `reason` (string, optional): Reason for leave

**Example:**
```bash
curl -X POST "http://localhost:8080/api/leaves/apply?employeeId=1&startDate=2024-02-01&endDate=2024-02-05&leaveType=ANNUAL&reason=Vacation"
```

#### Get Leave Balance
```http
GET /api/leaves/balance
```
**Parameters:**
- `employeeId` (int): Employee ID
- `year` (int, optional): Year (defaults to current year)

#### Get Remaining Balance
```http
GET /api/leaves/remaining
```
**Parameters:**
- `employeeId` (int): Employee ID
- `year` (int, optional): Year (defaults to current year)

#### Get Employee Leaves
```http
GET /api/leaves/employee
```
**Parameters:**
- `employeeId` (int): Employee ID

#### Cancel Leave
```http
PUT /api/leaves/cancel
```
**Parameters:**
- `leaveId` (int): Leave ID
- `employeeId` (int): Employee ID

### Manager Endpoints

#### Approve Leave
```http
PUT /api/leaves/approve
```
**Parameters:**
- `leaveId` (int): Leave ID
- `approvedBy` (int): Approver ID

#### Reject Leave
```http
PUT /api/leaves/reject
```
**Parameters:**
- `leaveId` (int): Leave ID
- `approvedBy` (int): Approver ID
- `rejectionReason` (string): Reason for rejection

#### Get Pending Leaves
```http
GET /api/leaves/pending
```

## Database Schema

### Leave Table
```sql
CREATE TABLE leaves (
    id SERIAL PRIMARY KEY,
    employee_id INTEGER NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    leave_type VARCHAR(20) NOT NULL,
    reason TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    applied_date DATE NOT NULL DEFAULT CURRENT_DATE,
    approved_by INTEGER,
    approved_date DATE,
    rejection_reason TEXT,
    FOREIGN KEY (employee_id) REFERENCES users(id)
);
```

### Leave Balance Table
```sql
CREATE TABLE leave_balances (
    id SERIAL PRIMARY KEY,
    employee_id INTEGER NOT NULL,
    year INTEGER NOT NULL,
    total_annual_leaves INTEGER NOT NULL DEFAULT 15,
    used_leaves INTEGER NOT NULL DEFAULT 0,
    remaining_leaves INTEGER NOT NULL DEFAULT 15,
    created_date DATE NOT NULL DEFAULT CURRENT_DATE,
    updated_date DATE NOT NULL DEFAULT CURRENT_DATE,
    FOREIGN KEY (employee_id) REFERENCES users(id),
    UNIQUE(employee_id, year)
);
```

## Business Logic Implementation

### Leave Application Validation

#### Date Validation
```java
// Cannot apply for past dates
if (startDate.isBefore(LocalDate.now())) {
    return new ErrorResult("Start date cannot be in the past");
}

// End date must be after start date
if (endDate.isBefore(startDate)) {
    return new ErrorResult("End date cannot be before start date");
}
```

#### Balance Validation
```java
// Check balance for annual leaves
if (leaveType == Leave.LeaveType.ANNUAL) {
    LeaveBalance balance = getOrCreateLeaveBalance(employeeId, currentYear);
    if (balance.getRemainingLeaves() < leaveDays) {
        return new ErrorResult("Insufficient leave balance");
    }
}
```

#### Overlap Validation
```java
// Check for overlapping leaves
List<Leave> overlappingLeaves = leaveDao.getByEmployeeIdAndDateRange(
    employeeId, startDate, endDate);
if (!overlappingLeaves.isEmpty()) {
    return new ErrorResult("You already have a leave application for this period");
}
```

#### Pending Limit Validation
```java
// Check pending limit
int pendingCount = leaveDao.countPendingLeavesByEmployeeId(employeeId);
if (pendingCount >= 3) {
    return new ErrorResult("You cannot have more than 3 pending leave applications");
}
```

### Balance Management

#### Automatic Balance Creation
```java
private LeaveBalance getOrCreateLeaveBalance(int employeeId, int year) {
    Optional<LeaveBalance> balanceOpt = leaveBalanceDao.getByEmployee_IdAndYear(employeeId, year);
    if (balanceOpt.isPresent()) {
        return balanceOpt.get();
    }
    
    // Create new balance for the year
    User employee = userService.getById(employeeId).getData();
    LeaveBalance newBalance = new LeaveBalance(employee, year);
    return leaveBalanceDao.save(newBalance);
}
```

#### Balance Update on Approval
```java
// Update balance when leave is approved
if (leave.getLeaveType() == Leave.LeaveType.ANNUAL) {
    int year = leave.getStartDate().getYear();
    LeaveBalance balance = getOrCreateLeaveBalance(leave.getEmployee().getId(), year);
    
    long leaveDays = ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate()) + 1;
    balance.updateUsedLeaves((int) leaveDays);
    leaveBalanceDao.save(balance);
}
```

## Usage Examples

### 1. Employee Applying for Annual Leave

```bash
# Apply for 5-day annual leave
curl -X POST "http://localhost:8080/api/leaves/apply" \
  -d "employeeId=1" \
  -d "startDate=2024-02-01" \
  -d "endDate=2024-02-05" \
  -d "leaveType=ANNUAL" \
  -d "reason=Family vacation"
```

### 2. Check Leave Balance

```bash
# Get current year balance
curl "http://localhost:8080/api/leaves/balance?employeeId=1"

# Get specific year balance
curl "http://localhost:8080/api/leaves/balance?employeeId=1&year=2024"
```

### 3. Manager Approving Leave

```bash
# Approve leave application
curl -X PUT "http://localhost:8080/api/leaves/approve" \
  -d "leaveId=1" \
  -d "approvedBy=2"
```

### 4. Get Employee Leave History

```bash
# Get all leaves for employee
curl "http://localhost:8080/api/leaves/employee?employeeId=1"

# Get only pending leaves
curl "http://localhost:8080/api/leaves/employee/status?employeeId=1&status=PENDING"

# Get only annual leaves
curl "http://localhost:8080/api/leaves/employee/type?employeeId=1&leaveType=ANNUAL"
```

## Error Handling

### Common Error Scenarios

#### Insufficient Balance
```json
{
  "success": false,
  "message": "Insufficient leave balance. Remaining: 3 days",
  "data": null
}
```

#### Invalid Date Range
```json
{
  "success": false,
  "message": "Start date cannot be in the past",
  "data": null
}
```

#### Overlapping Leaves
```json
{
  "success": false,
  "message": "You already have a leave application for this period",
  "data": null
}
```

#### Pending Limit Exceeded
```json
{
  "success": false,
  "message": "You cannot have more than 3 pending leave applications",
  "data": null
}
```

## Performance Considerations

### Database Optimization

#### Indexes
```sql
-- Index for employee leaves
CREATE INDEX idx_leaves_employee_id ON leaves(employee_id);
CREATE INDEX idx_leaves_status ON leaves(status);
CREATE INDEX idx_leaves_start_date ON leaves(start_date);

-- Index for leave balances
CREATE INDEX idx_leave_balances_employee_year ON leave_balances(employee_id, year);
```

#### Query Optimization
- Use pagination for large result sets
- Implement caching for frequently accessed data
- Optimize date range queries

### Caching Strategy
- Cache leave balances for active employees
- Cache leave types and statuses
- Implement Redis for distributed caching

## Security Considerations

### Access Control
- Employees can only access their own leaves
- Managers can approve/reject leaves
- Admin users have full access

### Data Validation
- Input sanitization for all text fields
- Date validation to prevent SQL injection
- Type validation for enums

### Audit Trail
- Log all leave applications
- Track approval/rejection actions
- Maintain change history

## Testing

### Unit Tests
- Test business logic validation
- Test balance calculations
- Test date range validations

### Integration Tests
- Test API endpoints
- Test database operations
- Test approval workflows

### Performance Tests
- Test with large datasets
- Test concurrent operations
- Test response times

## Future Enhancements

### Planned Features
- **Leave Templates**: Predefined leave types with specific rules
- **Holiday Calendar**: Integration with company holidays
- **Email Notifications**: Automatic notifications for status changes
- **Mobile App**: Native mobile application
- **Reporting**: Advanced leave analytics and reports
- **Workflow Engine**: Configurable approval workflows
- **Integration**: Integration with payroll systems

### Technical Improvements
- **Microservices**: Split into separate microservice
- **Event Sourcing**: Implement event-driven architecture
- **GraphQL**: Add GraphQL API layer
- **Real-time Updates**: WebSocket support for real-time updates

## Troubleshooting

### Common Issues

#### Leave Balance Not Updating
- Check if leave was approved
- Verify leave type counts against balance
- Check database transaction logs

#### Date Validation Errors
- Ensure dates are in correct format (YYYY-MM-DD)
- Check timezone settings
- Verify date parsing logic

#### Permission Errors
- Verify user roles and permissions
- Check authentication status
- Validate user ID in requests

### Debug Mode
Enable debug logging:
```properties
logging.level.io.kodlama.hrms.business.concretes.LeaveManager=DEBUG
logging.level.io.kodlama.hrms.dataAccess.abstracts.LeaveDao=DEBUG
```

## Support

For technical support or questions about the leave management system:
1. Check this documentation
2. Review API documentation in `docs/api/`
3. Check application logs
4. Create an issue in the project repository
