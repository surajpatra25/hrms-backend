# HR Policy API Documentation

## Overview
The HR Policy API provides endpoints for managing company HR policies. It allows uploading, listing, downloading, and deleting HR policy documents.

## Base URL
```
/api/hrpolicies
```

## Endpoints

### 1. Upload HR Policy
Upload a new HR policy document (PDF only).

**Endpoint:** `POST /api/hrpolicies/upload`

**Content-Type:** `multipart/form-data`

**Parameters:**
- `policyName` (required): Unique name for the policy
- `file` (required): PDF file to upload
- `description` (optional): Description of the policy

**Example Request (using cURL):**
```bash
curl -X POST http://localhost:8080/api/hrpolicies/upload \
  -F "policyName=Leave Policy" \
  -F "file=@/path/to/leave_policy.pdf" \
  -F "description=Company leave policy for 2025"
```

**Success Response:**
```json
{
  "success": true,
  "message": "HR Policy uploaded successfully"
}
```

**Error Responses:**
- Policy with same name already exists
- Invalid file format (only PDF allowed)
- File size exceeds limit (10MB)
- Empty file or policy name

---

### 2. List All HR Policies
Get a list of all HR policies with their metadata.

**Endpoint:** `GET /api/hrpolicies`

**Example Request:**
```bash
curl -X GET http://localhost:8080/api/hrpolicies
```

**Success Response:**
```json
{
  "data": [
    {
      "id": 1,
      "policyName": "Leave Policy",
      "fileName": "leave_policy.pdf",
      "uploadedDate": "2025-11-09T10:30:00",
      "description": "Company leave policy for 2025",
      "fileSize": 245678,
      "filePath": "./policy-files/Leave_Policy.pdf"
    },
    {
      "id": 2,
      "policyName": "Remote Work Policy",
      "fileName": "remote_work.pdf",
      "uploadedDate": "2025-11-09T11:15:00",
      "description": "Guidelines for remote work",
      "fileSize": 189432,
      "filePath": "./policy-files/Remote_Work_Policy.pdf"
    }
  ],
  "success": true,
  "message": "All HR Policies listed successfully"
}
```

---

### 3. Get Policy Details by Name
Get detailed information about a specific policy.

**Endpoint:** `GET /api/hrpolicies/{policyName}`

**Example Request:**
```bash
curl -X GET http://localhost:8080/api/hrpolicies/Leave%20Policy
```

**Success Response:**
```json
{
  "data": {
    "id": 1,
    "policyName": "Leave Policy",
    "fileName": "leave_policy.pdf",
    "uploadedDate": "2025-11-09T10:30:00",
    "description": "Company leave policy for 2025",
    "fileSize": 245678,
    "filePath": "./policy-files/Leave_Policy.pdf"
  },
  "success": true,
  "message": "HR Policy found successfully"
}
```

---

### 4. Download HR Policy
Download a specific HR policy file. Requires both policy name and file name for validation.

**Endpoint:** `GET /api/hrpolicies/download`

**Parameters:**
- `policyName` (required): Name of the policy
- `fileName` (required): Original file name of the policy

**Example Request:**
```bash
curl -X GET "http://localhost:8080/api/hrpolicies/download?policyName=Leave%20Policy&fileName=leave_policy.pdf" \
  --output leave_policy.pdf
```

**Response:**
- Binary PDF file with appropriate headers
- Content-Type: `application/pdf`
- Content-Disposition: `attachment; filename="original_filename.pdf"`

**Error Responses:**
- 404: Policy not found
- 400: File name does not match the policy
- 400: Failed to read policy file

---

### 5. Delete HR Policy
Delete a specific HR policy (both database record and physical file).

**Endpoint:** `DELETE /api/hrpolicies/{policyName}`

**Example Request:**
```bash
curl -X DELETE http://localhost:8080/api/hrpolicies/Leave%20Policy
```

**Success Response:**
```json
{
  "success": true,
  "message": "HR Policy deleted successfully"
}
```

**Error Response:**
```json
{
  "success": false,
  "message": "HR Policy not found"
}
```

---

## Configuration

### Application Properties
The following properties can be configured in `application.properties`:

```properties
# HR Policy File Storage Configuration
hrms.policy.upload-dir=./policy-files
hrms.policy.max-file-size=10485760

# Multipart file upload configuration
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

### Configuration Details
- `hrms.policy.upload-dir`: Directory where policy files are stored (default: `./policy-files`)
- `hrms.policy.max-file-size`: Maximum file size in bytes (default: 10MB = 10485760 bytes)
- `spring.servlet.multipart.max-file-size`: Spring's multipart file size limit
- `spring.servlet.multipart.max-request-size`: Maximum request size for multipart uploads

---

## Database Schema

The HR Policy feature uses the following table:

```sql
CREATE TABLE hr_policies (
    id SERIAL PRIMARY KEY,
    policy_name VARCHAR(255) UNIQUE NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    uploaded_date TIMESTAMP NOT NULL,
    description TEXT,
    file_size BIGINT,
    file_path VARCHAR(500) NOT NULL
);
```

---

## File Storage

- **Storage Type:** Local filesystem
- **File Format:** PDF only
- **File Naming:** Policy names are sanitized (special characters replaced with underscores)
- **Default Directory:** `./policy-files` (created automatically if it doesn't exist)
- **Max File Size:** 10MB (configurable)

### Docker Volume Setup

When running in Docker, the policy files are persisted using a Docker volume:

**Docker Compose Configuration:**
```yaml
volumes:
  hrms-policy-files:/app/policy-files
```

This ensures that policy files are:
- Persisted across container restarts
- Not lost when the container is recreated
- Stored outside the container filesystem

**Volume Management Commands:**
```bash
# List volumes
docker volume ls

# Inspect the policy files volume
docker volume inspect hrms-backend_hrms-policy-files

# Backup policy files
docker run --rm -v hrms-backend_hrms-policy-files:/data -v $(pwd):/backup alpine tar czf /backup/policy-files-backup.tar.gz /data

# Restore policy files
docker run --rm -v hrms-backend_hrms-policy-files:/data -v $(pwd):/backup alpine tar xzf /backup/policy-files-backup.tar.gz -C /
```

---

## Validation Rules

1. **Policy Name:**
   - Required field
   - Must be unique
   - Cannot be empty or whitespace

2. **File:**
   - Required field
   - Must be PDF format (checked via extension and content type)
   - Must not exceed maximum file size (default: 10MB)
   - Cannot be empty

3. **Description:**
   - Optional field
   - Can be null or empty

---

## Error Handling

The API returns appropriate HTTP status codes:

- **200 OK**: Successful operation
- **400 Bad Request**: Validation errors, invalid input
- **404 Not Found**: Policy not found
- **500 Internal Server Error**: Server-side errors (file I/O issues, etc.)

All error responses follow the Result pattern with `success: false` and an error message.

---

## Usage Examples

### Example 1: Upload a New Policy
```bash
curl -X POST http://localhost:8080/api/hrpolicies/upload \
  -F "policyName=Dress Code Policy" \
  -F "file=@dress_code.pdf" \
  -F "description=Official dress code guidelines"
```

### Example 2: List All Policies
```bash
curl -X GET http://localhost:8080/api/hrpolicies
```

### Example 3: Download a Policy
```bash
curl -X GET "http://localhost:8080/api/hrpolicies/download?policyName=Dress%20Code%20Policy&fileName=dress_code.pdf" \
  --output dress_code.pdf
```

### Example 4: Delete a Policy
```bash
curl -X DELETE "http://localhost:8080/api/hrpolicies/Dress%20Code%20Policy"
```

---

## Security Considerations

**Current Implementation:**
- No authentication required (as per requirements)
- CORS enabled for cross-origin requests

**Future Enhancements:**
- Add authentication/authorization
- Role-based access control (e.g., only HR can upload/delete)
- Audit logging for policy changes
- File virus scanning before storage

---

## Testing

To test the APIs, you can:

1. Start the application
2. Use tools like Postman, cURL, or any HTTP client
3. Test the upload endpoint with a PDF file
4. Verify the file is stored in the `policy-files` directory
5. Test listing and downloading policies
6. Verify database entries are created correctly

---

## Technical Implementation

### Components Created:

1. **Entity:** `HRPolicy.java` - JPA entity for database mapping
2. **DAO:** `HRPolicyDao.java` - Data access interface
3. **Service Interface:** `HRPolicyService.java` - Business logic interface
4. **Service Implementation:** `HRPolicyManager.java` - Business logic implementation
5. **Controller:** `HRPolicyController.java` - REST API endpoints

### Design Patterns Used:
- Repository Pattern (JPA/DAO)
- Service Layer Pattern
- Result/DataResult Pattern (for consistent API responses)
- Dependency Injection (Spring Framework)

