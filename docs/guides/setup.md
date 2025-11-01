# HRMS Backend Setup Guide

## Prerequisites

Before setting up the HRMS backend, ensure you have the following installed:

### Required Software
- **Java 11** or higher
- **Maven 3.6+**
- **PostgreSQL 12+** or **MySQL 8.0+**
- **Git**

### Optional Software
- **IntelliJ IDEA** or **Eclipse** (for development)
- **Postman** or **Insomnia** (for API testing)
- **pgAdmin** or **MySQL Workbench** (for database management)

## Installation Steps

### 1. Clone the Repository

```bash
git clone <repository-url>
cd hrms-backend
```

### 2. Database Setup

#### PostgreSQL Setup
1. Install PostgreSQL on your system
2. Create a new database:
   ```sql
   CREATE DATABASE hrms_db;
   CREATE USER hrms_user WITH PASSWORD 'your_password';
   GRANT ALL PRIVILEGES ON DATABASE hrms_db TO hrms_user;
   ```

#### MySQL Setup
1. Install MySQL on your system
2. Create a new database:
   ```sql
   CREATE DATABASE hrms_db;
   CREATE USER 'hrms_user'@'localhost' IDENTIFIED BY 'your_password';
   GRANT ALL PRIVILEGES ON hrms_db.* TO 'hrms_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

### 3. Configure Application Properties

Edit `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/hrms_db
spring.datasource.username=hrms_user
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

# Server Configuration
server.port=8080

# Logging
logging.level.io.kodlama.hrms=DEBUG
logging.level.org.springframework.web=DEBUG
```

For MySQL, change the configuration to:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hrms_db?useSSL=false&serverTimezone=UTC
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

### 4. Install Dependencies

```bash
mvn clean install
```

### 5. Run the Application

#### Option 1: Using Maven
```bash
mvn spring-boot:run
```

#### Option 2: Using JAR file
```bash
mvn clean package
java -jar target/hrms-0.0.1-SNAPSHOT.jar
```

#### Option 3: Using IDE
1. Open the project in your IDE
2. Run the `HrmsApplication.java` main class

### 6. Verify Installation

1. Open your browser and go to `http://localhost:8080`
2. You should see the application running
3. Test the API endpoints using Postman or curl:

```bash
# Test health endpoint
curl http://localhost:8080/api/health

# Test candidates endpoint
curl http://localhost:8080/api/candidates/getAll
```

## Development Setup

### 1. IDE Configuration

#### IntelliJ IDEA
1. Open the project
2. Import Maven dependencies when prompted
3. Configure the application run configuration:
   - Main class: `io.kodlama.hrms.HrmsApplication`
   - VM options: `-Dspring.profiles.active=dev`
   - Program arguments: (leave empty)

#### Eclipse
1. Import as Maven project
2. Right-click project → Run As → Maven Build
3. Goals: `spring-boot:run`

### 2. Database Schema

The application will automatically create the database schema on first run using Hibernate's `ddl-auto=update` setting.

#### Manual Schema Creation (Optional)
If you prefer to create the schema manually, set `ddl-auto=validate` and run the SQL scripts in the `docs/sql/` directory.

### 3. Testing Configuration

Create a test properties file `src/test/resources/application-test.properties`:

```properties
# Test Database Configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

## Environment Configuration

### Development Environment
```properties
# application-dev.properties
spring.profiles.active=dev
spring.jpa.hibernate.ddl-auto=update
logging.level.io.kodlama.hrms=DEBUG
```

### Production Environment
```properties
# application-prod.properties
spring.profiles.active=prod
spring.jpa.hibernate.ddl-auto=validate
logging.level.io.kodlama.hrms=INFO
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USERNAME}
spring.datasource.password=${DATABASE_PASSWORD}
```

## Docker Setup (Optional)

### 1. Create Dockerfile
```dockerfile
FROM openjdk:11-jre-slim
COPY target/hrms-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### 2. Create docker-compose.yml
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/hrms_db
      - SPRING_DATASOURCE_USERNAME=hrms_user
      - SPRING_DATASOURCE_PASSWORD=hrms_password
    depends_on:
      - db
  
  db:
    image: postgres:13
    environment:
      - POSTGRES_DB=hrms_db
      - POSTGRES_USER=hrms_user
      - POSTGRES_PASSWORD=hrms_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

### 3. Run with Docker
```bash
docker-compose up -d
```

## Troubleshooting

### Common Issues

#### 1. Database Connection Error
```
Error: Connection refused
```
**Solution:**
- Ensure database is running
- Check connection parameters in `application.properties`
- Verify database credentials

#### 2. Port Already in Use
```
Error: Port 8080 is already in use
```
**Solution:**
- Change port in `application.properties`: `server.port=8081`
- Or kill the process using port 8080

#### 3. Maven Build Failure
```
Error: Failed to resolve dependencies
```
**Solution:**
- Check internet connection
- Clear Maven cache: `mvn clean`
- Update Maven: `mvn -U clean install`

#### 4. Hibernate Schema Error
```
Error: Table doesn't exist
```
**Solution:**
- Set `spring.jpa.hibernate.ddl-auto=create` for first run
- Check database permissions
- Verify database exists

### Logs Location
- Application logs: `logs/application.log` (if configured)
- Console output: Check terminal/IDE console

### Performance Tuning

#### Database Optimization
```properties
# Connection pool settings
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000

# JPA optimization
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
```

#### JVM Optimization
```bash
java -Xms512m -Xmx1024m -jar target/hrms-0.0.1-SNAPSHOT.jar
```

## Security Considerations

### 1. Database Security
- Use strong passwords
- Limit database user permissions
- Enable SSL connections in production

### 2. Application Security
- Change default passwords
- Use environment variables for sensitive data
- Enable HTTPS in production
- Implement proper authentication and authorization

### 3. Network Security
- Use firewall rules
- Limit database access to application servers only
- Use VPN for remote access

## Monitoring and Maintenance

### 1. Health Checks
The application provides health check endpoints:
- `GET /actuator/health` - Application health
- `GET /actuator/info` - Application information

### 2. Database Maintenance
- Regular backups
- Monitor database size and performance
- Update database statistics

### 3. Application Maintenance
- Monitor memory usage
- Check application logs
- Update dependencies regularly

## Support

For additional support:
1. Check the troubleshooting section above
2. Review application logs
3. Check the API documentation in `docs/api/`
4. Create an issue in the project repository
