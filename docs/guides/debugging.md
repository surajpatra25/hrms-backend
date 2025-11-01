# Debugging Guide

This guide explains how to run and debug the HRMS backend application using different profiles and configurations.

## Profiles Available

The application supports three profiles:

1. **debug** - Full debug logging and diagnostics
2. **dev** - Development environment with moderate logging
3. **prod** - Production environment with minimal logging

## Running with Debug Profile

### Using Maven

#### Run with Debug Profile
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=debug
```

#### Run with JVM Debug Options
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=debug \
    -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### Using JAR File

```bash
java -jar target/hrms-0.0.1-SNAPSHOT.jar --spring.profiles.active=debug
```

With remote debugging enabled:
```bash
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 \
     -jar target/hrms-0.0.1-SNAPSHOT.jar \
     --spring.profiles.active=debug
```

### Using Environment Variable

```bash
# Windows PowerShell
$env:SPRING_PROFILES_ACTIVE="debug"
mvn spring-boot:run

# Windows CMD
set SPRING_PROFILES_ACTIVE=debug
mvn spring-boot:run

# Linux/Mac
export SPRING_PROFILES_ACTIVE=debug
mvn spring-boot:run
```

## IDE Debugging

### IntelliJ IDEA

#### Using Launch Configuration
1. Open **Run/Debug Configurations**
2. Select **Spring Boot** configuration
3. Choose the **HrmsApplication (Debug Profile)** configuration
4. Click **Debug** button

#### Manual Configuration
1. Go to **Run → Edit Configurations**
2. Click **+** → **Spring Boot**
3. Set the following:
   - **Name**: HRMS Debug
   - **Main class**: `io.kodlama.hrms.HrmsApplication`
   - **Active profiles**: `debug`
   - **VM options**: `-Xms512m -Xmx1024m -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005`
   - **Program arguments**: `--spring.profiles.active=debug`

#### Remote Debugging
1. Start the application with remote debug:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
   ```

2. In IntelliJ IDEA:
   - Go to **Run → Edit Configurations**
   - Click **+** → **Remote JVM Debug**
   - Set:
     - **Host**: `localhost`
     - **Port**: `5005`
   - Click **Debug**

### VS Code

#### Using Launch Configuration
1. Open **Run and Debug** view (Ctrl+Shift+D)
2. Select **Spring Boot - HRMS (Debug Profile)** from dropdown
3. Click **Start Debugging** (F5)

#### Manual Configuration
The `.vscode/launch.json` file contains pre-configured debug profiles:
- **Spring Boot - HRMS (Debug Profile)**: Full debug mode
- **Spring Boot - HRMS (Development Profile)**: Dev mode
- **Spring Boot - HRMS (Production Profile)**: Production mode
- **Spring Boot - HRMS (Default)**: Default configuration

### Eclipse

1. Right-click on `HrmsApplication.java`
2. Select **Debug As → Spring Boot App**
3. Add VM arguments:
   ```
   -Xms512m -Xmx1024m -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005
   ```
4. Add Program arguments:
   ```
   --spring.profiles.active=debug
   ```

## Debug Profile Features

The debug profile (`application-debug.properties`) includes:

### Logging Levels
- **Root**: INFO
- **HRMS Package**: DEBUG
- **Spring Framework**: DEBUG
- **Hibernate/SQL**: DEBUG
- **HTTP Requests**: DEBUG

### SQL Logging
- All SQL queries are logged
- SQL parameters are shown
- Query formatting is enabled
- Query comments are included

### Performance Monitoring
- Hibernate statistics enabled
- Query performance tracking
- Transaction logging

### HTTP Request Logging
- All incoming HTTP requests
- Request/response details
- Dispatcher servlet debugging

### Actuator Endpoints
All actuator endpoints are exposed for debugging:
- Health: `http://localhost:8080/actuator/health`
- Info: `http://localhost:8080/actuator/info`
- Metrics: `http://localhost:8080/actuator/metrics`

## Debugging Tips

### 1. Enable Breakpoints
- Set breakpoints in your code
- Use conditional breakpoints for specific conditions
- Use logpoints for logging without stopping execution

### 2. Inspect Variables
- Use **Variables** panel to inspect values
- Use **Watches** to monitor specific variables
- Use **Evaluate Expression** for quick evaluations

### 3. Database Debugging
- Enable SQL logging in debug profile
- Use database tools to inspect queries
- Check Hibernate statistics endpoint

### 4. HTTP Request Debugging
- Use Postman/Insomnia for API testing
- Enable request/response logging
- Check Spring's DispatcherServlet logs

### 5. Transaction Debugging
- Enable transaction logging
- Check transaction boundaries
- Monitor database connections

## Common Debugging Scenarios

### Debugging Leave Management
```java
// Set breakpoint in LeaveManager.applyLeave()
// Inspect:
// - employeeId
// - startDate and endDate
// - leaveType
// - balance calculations
```

### Debugging Authentication
```java
// Set breakpoint in AuthManager
// Inspect:
// - User credentials
// - Password validation
// - Activation codes
```

### Debugging Database Queries
```java
// Enable SQL logging
// Check logs for:
// - Query execution time
// - Parameter values
// - Query results
```

## Performance Debugging

### Enable Performance Monitoring
```properties
spring.jpa.properties.hibernate.generate_statistics=true
```

### Check Statistics
Access Hibernate statistics via JMX or logs:
```java
Statistics stats = sessionFactory.getStatistics();
```

### Monitor Memory
```bash
# Add to VM options
-XX:+PrintGCDetails
-XX:+PrintGCTimeStamps
-Xloggc:gc.log
```

## Troubleshooting Debug Issues

### Issue: Debugger Not Attaching
**Solution:**
- Check if port 5005 is already in use
- Verify JVM arguments are correct
- Ensure firewall allows connections

### Issue: Too Much Logging
**Solution:**
- Switch to `dev` profile
- Adjust logging levels in properties file
- Use log filtering

### Issue: Application Not Starting in Debug Mode
**Solution:**
- Check application.properties for errors
- Verify database connection
- Check port conflicts

### Issue: Breakpoints Not Working
**Solution:**
- Ensure source code is compiled with debug symbols
- Check if code matches running version
- Try cleaning and rebuilding project

## Log Files

### Debug Log Location
Logs are written to:
- **Console**: Standard output
- **File**: `logs/hrms-debug.log` (if configured)

### Log Rotation
- Max file size: 10MB (debug), 50MB (prod)
- Max history: 30 days (debug), 60 days (prod)

## Best Practices

1. **Use Debug Profile Only for Development**
   - Never use debug profile in production
   - Switch to dev profile for normal development

2. **Monitor Log File Size**
   - Debug logs can grow large quickly
   - Regularly clean up old log files

3. **Use Conditional Breakpoints**
   - Avoid stopping on every iteration
   - Use conditions for specific scenarios

4. **Clean Up After Debugging**
   - Remove temporary debug code
   - Switch back to dev profile

5. **Document Debug Findings**
   - Keep notes of issues found
   - Update documentation with fixes

## Quick Reference

### Maven Commands
```bash
# Debug profile
mvn spring-boot:run -Dspring-boot.run.profiles=debug

# With remote debugging
mvn spring-boot:run -Dspring-boot.run.profiles=debug \
    -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"

# Development profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Production profile
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Environment Variables
```bash
# Windows
set SPRING_PROFILES_ACTIVE=debug

# Linux/Mac
export SPRING_PROFILES_ACTIVE=debug
```

### JVM Debug Options
```
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005
```

### Useful Endpoints (Debug Profile)
- Health: `http://localhost:8080/actuator/health`
- Info: `http://localhost:8080/actuator/info`
- Metrics: `http://localhost:8080/actuator/metrics`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

---

**Happy Debugging! 🐛🔍**
