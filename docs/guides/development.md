# Development Guide

## Overview
This guide provides information for developers working on the HRMS backend project, including coding standards, development workflow, and best practices.

## Development Environment Setup

### Prerequisites
- Java 11 or higher
- Maven 3.6+
- PostgreSQL 12+ or MySQL 8.0+
- IntelliJ IDEA or Eclipse
- Git

### IDE Configuration

#### IntelliJ IDEA
1. Open the project
2. Import Maven dependencies when prompted
3. Configure code style:
   - Go to File → Settings → Editor → Code Style → Java
   - Import the project's code style settings
4. Enable annotation processing:
   - Go to File → Settings → Build → Compiler → Annotation Processors
   - Check "Enable annotation processing"

#### Eclipse
1. Import as Maven project
2. Configure code formatting
3. Install Lombok plugin
4. Configure annotation processing

## Coding Standards

### Java Code Style
- Use 4 spaces for indentation
- Maximum line length: 120 characters
- Use meaningful variable and method names
- Follow camelCase for variables and methods
- Use PascalCase for classes and interfaces

### Package Structure
```
io.kodlama.hrms
├── api.controllers          # REST Controllers
├── business
│   ├── abstracts           # Service Interfaces
│   └── concretes           # Service Implementations
├── dataAccess.abstracts    # Repository Interfaces
├── entities.concretes      # JPA Entities
├── core
│   ├── entities           # Base Entities
│   └── utilities          # Utility Classes
└── config                 # Configuration Classes
```

### Naming Conventions
- **Controllers**: `{Entity}Controller` (e.g., `LeaveController`)
- **Services**: `{Entity}Service` (interface), `{Entity}Manager` (implementation)
- **Repositories**: `{Entity}Dao`
- **Entities**: `{Entity}` (e.g., `Leave`, `LeaveBalance`)
- **DTOs**: `{Entity}Dto` (e.g., `LeaveApplicationDto`)

## Development Workflow

### 1. Branch Strategy
- `main`: Production-ready code
- `develop`: Integration branch for features
- `feature/*`: Feature development branches
- `hotfix/*`: Critical bug fixes

### 2. Commit Messages
Use conventional commit format:
```
type(scope): description

feat(leave): add leave application endpoint
fix(auth): resolve authentication issue
docs(api): update API documentation
refactor(service): improve leave validation logic
```

### 3. Pull Request Process
1. Create feature branch from `develop`
2. Implement changes with tests
3. Update documentation if needed
4. Create pull request to `develop`
5. Code review and approval
6. Merge after CI passes

## Code Quality

### Code Review Checklist
- [ ] Code follows project conventions
- [ ] Proper error handling
- [ ] Input validation
- [ ] Unit tests included
- [ ] Documentation updated
- [ ] No hardcoded values
- [ ] Proper logging
- [ ] Security considerations

### Testing Requirements
- Unit tests for business logic
- Integration tests for API endpoints
- Test coverage minimum 80%
- All tests must pass before merge

## API Development

### Controller Guidelines
```java
@RestController
@RequestMapping("/api/leaves")
@CrossOrigin
public class LeaveController {
    
    @PostMapping("/apply")
    public ResponseEntity<?> applyLeave(@RequestParam int employeeId, 
                                       @RequestParam String startDate, 
                                       @RequestParam String endDate, 
                                       @RequestParam String leaveType, 
                                       @RequestParam(required = false) String reason) {
        // Implementation
    }
}
```

### Service Layer Guidelines
```java
@Service
public class LeaveManager implements LeaveService {
    
    @Override
    public Result applyLeave(int employeeId, LocalDate startDate, 
                           LocalDate endDate, Leave.LeaveType leaveType, String reason) {
        // Business logic implementation
    }
}
```

### Repository Guidelines
```java
public interface LeaveDao extends JpaRepository<Leave, Integer> {
    
    List<Leave> getByEmployee_Id(int employeeId);
    
    @Query("SELECT l FROM Leave l WHERE l.employee.id = :employeeId AND l.status = :status")
    List<Leave> getByEmployeeIdAndStatus(@Param("employeeId") int employeeId, 
                                        @Param("status") Leave.LeaveStatus status);
}
```

## Database Development

### Entity Guidelines
```java
@Entity
@Table(name = "leaves")
public class Leave {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne()
    @JoinColumn(name = "employee_id")
    private User employee;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    // Other fields...
}
```

### Migration Guidelines
- Use Hibernate's `ddl-auto=update` for development
- Create migration scripts for production
- Test migrations on staging environment
- Backup database before major changes

## Error Handling

### Exception Handling
```java
@ExceptionHandler(MethodArgumentNotValidException.class)
@ResponseStatus(HttpStatus.BAD_REQUEST)
public ErrorDataResult<Object> handleValidationException(MethodArgumentNotValidException exceptions) {
    Map<String, String> validationErrors = new HashMap<>();
    for (FieldError fieldError : exceptions.getBindingResult().getFieldErrors()) {
        validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
    }
    return new ErrorDataResult<>(validationErrors);
}
```

### Custom Exceptions
```java
public class LeaveValidationException extends RuntimeException {
    public LeaveValidationException(String message) {
        super(message);
    }
}
```

## Logging

### Logging Levels
- `ERROR`: System errors, exceptions
- `WARN`: Warning conditions
- `INFO`: Important business events
- `DEBUG`: Detailed debugging information

### Logging Guidelines
```java
private static final Logger logger = LoggerFactory.getLogger(LeaveManager.class);

@Override
public Result applyLeave(int employeeId, LocalDate startDate, LocalDate endDate, 
                       Leave.LeaveType leaveType, String reason) {
    logger.info("Leave application started for employee: {}", employeeId);
    
    try {
        // Business logic
        logger.info("Leave application successful for employee: {}", employeeId);
        return new SuccessResult("Leave application submitted successfully");
    } catch (Exception e) {
        logger.error("Leave application failed for employee: {}, error: {}", employeeId, e.getMessage());
        return new ErrorResult("Leave application failed");
    }
}
```

## Security Guidelines

### Input Validation
```java
@Valid @RequestBody LeaveApplicationDto leaveApplication
```

### SQL Injection Prevention
- Use JPA/Hibernate queries
- Use parameterized queries
- Validate all inputs

### Authentication & Authorization
- Implement proper user authentication
- Check user permissions for operations
- Validate user ownership of resources

## Performance Guidelines

### Database Optimization
- Use appropriate indexes
- Optimize queries
- Use pagination for large datasets
- Implement caching where appropriate

### Memory Management
- Avoid memory leaks
- Use appropriate data structures
- Monitor memory usage

## Documentation

### Code Documentation
```java
/**
 * Applies for leave for the specified employee.
 * 
 * @param employeeId The ID of the employee applying for leave
 * @param startDate The start date of the leave
 * @param endDate The end date of the leave
 * @param leaveType The type of leave being applied for
 * @param reason Optional reason for the leave
 * @return Result indicating success or failure
 */
public Result applyLeave(int employeeId, LocalDate startDate, LocalDate endDate, 
                       Leave.LeaveType leaveType, String reason) {
    // Implementation
}
```

### API Documentation
- Use Swagger annotations
- Provide clear descriptions
- Include example requests/responses
- Document error scenarios

## Testing

### Unit Testing
```java
@Test
public void testApplyLeave_Success() {
    // Given
    int employeeId = 1;
    LocalDate startDate = LocalDate.now().plusDays(1);
    LocalDate endDate = LocalDate.now().plusDays(5);
    Leave.LeaveType leaveType = Leave.LeaveType.ANNUAL;
    String reason = "Vacation";
    
    // When
    Result result = leaveService.applyLeave(employeeId, startDate, endDate, leaveType, reason);
    
    // Then
    assertTrue(result.isSuccess());
    assertEquals("Leave application submitted successfully", result.getMessage());
}
```

### Integration Testing
```java
@SpringBootTest
@AutoConfigureTestDatabase
class LeaveControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    public void testApplyLeaveEndpoint() {
        // Test implementation
    }
}
```

## Deployment

### Environment Configuration
- Development: `application-dev.properties`
- Testing: `application-test.properties`
- Production: `application-prod.properties`

### Build Process
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package application
mvn package

# Run application
mvn spring-boot:run
```

## Troubleshooting

### Common Issues
1. **Database Connection**: Check connection parameters
2. **Port Conflicts**: Change server port if needed
3. **Dependency Issues**: Clean and rebuild project
4. **Memory Issues**: Increase JVM heap size

### Debug Mode
Enable debug logging:
```properties
logging.level.io.kodlama.hrms=DEBUG
logging.level.org.springframework.web=DEBUG
```

## Best Practices

### General
- Write clean, readable code
- Follow SOLID principles
- Use design patterns appropriately
- Keep methods small and focused
- Use meaningful names

### Spring Boot Specific
- Use dependency injection
- Leverage Spring Boot auto-configuration
- Use profiles for different environments
- Implement proper error handling

### Database
- Use transactions appropriately
- Optimize queries
- Use appropriate data types
- Implement proper indexing

## Resources

### Documentation
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [JPA/Hibernate Documentation](https://hibernate.org/orm/documentation/)
- [Maven Documentation](https://maven.apache.org/guides/)

### Tools
- [Postman](https://www.postman.com/) - API testing
- [pgAdmin](https://www.pgadmin.org/) - PostgreSQL management
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) - IDE

---

**Happy Coding! 🚀**
