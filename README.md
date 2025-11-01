# HRMS Backend - Human Resource Management System

[![Java](https://img.shields.io/badge/Java-11-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.5.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-12+-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A comprehensive Human Resource Management System (HRMS) backend built with Spring Boot, featuring candidate management, job postings, resume handling, and a complete leave management system.

## 🚀 Features

### Core HR Features
- **User Management**: Support for Candidates, Employers, and Company Staff
- **Job Management**: Job postings, applications, and recruitment workflow
- **Resume Management**: Complete resume builder with education, experience, and skills
- **Authentication & Authorization**: Secure user registration and activation
- **Turkish National Identity Verification**: Integration with TC Kimlik validation

### Leave Management System ✨
- **Leave Applications**: Apply for various types of leave (Annual, Sick, Personal, Emergency, Maternity, Paternity)
- **Leave Balance Tracking**: Automatic tracking of 15 annual leaves per employee
- **Approval Workflow**: Manager approval/rejection system
- **Leave Cancellation**: Employees can cancel pending applications
- **Date Validation**: Prevents invalid dates and overlapping periods
- **Balance Validation**: Ensures sufficient leave balance for annual leaves

### Additional Features
- **Swagger UI**: Interactive API documentation
- **Cloudinary Integration**: Image upload and management
- **Email Services**: User activation and notifications
- **Comprehensive API**: RESTful endpoints for all operations

## 🛠️ Technology Stack

- **Backend**: Java 11, Spring Boot 2.5.0
- **Database**: PostgreSQL 12+ / MySQL 8.0+
- **Build Tool**: Maven 3.6+
- **ORM**: Hibernate/JPA
- **Documentation**: Swagger/OpenAPI 2.9.2
- **Cloud Storage**: Cloudinary
- **Validation**: Bean Validation
- **Testing**: JUnit 5, Spring Boot Test

## 📋 Prerequisites

- Java 11 or higher
- Maven 3.6+
- PostgreSQL 12+ or MySQL 8.0+
- Git

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd hrms-backend
```

### 2. Configure Database
Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/hrms_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Run the Application

#### Standard Run
```bash
mvn clean install
mvn spring-boot:run
```

#### Run with Debug Profile
```bash
# Using Maven
mvn spring-boot:run -Dspring-boot.run.profiles=debug

# Using batch script (Windows)
run-debug.bat

# Using shell script (Linux/Mac)
./run-debug.sh
```

#### Run with Remote Debugging
```bash
# Using Maven
mvn spring-boot:run -Dspring-boot.run.profiles=debug \
    -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"

# Using batch script (Windows)
run-debug-remote.bat

# Using shell script (Linux/Mac)
./run-debug-remote.sh
```

### 4. Access the Application
- **API Base URL**: `http://localhost:8080/api`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **Health Check** (Debug Profile): `http://localhost:8080/actuator/health`

## 📚 Documentation

Comprehensive documentation is available in the `docs/` folder:

- **[API Documentation](docs/api/README.md)** - Complete API reference
- **[Entity Documentation](docs/entities/README.md)** - Database schema and relationships
- **[Setup Guide](docs/guides/setup.md)** - Detailed installation instructions
- **[Leave Management](docs/guides/leave-management.md)** - Leave system documentation
- **[Debugging Guide](docs/guides/debugging.md)** - Debug profiles and debugging instructions
- **[Development Guide](docs/guides/development.md)** - Development best practices
- **[AWS Deployment](docs/guides/aws-deployment.md)** - Deploy to AWS (EB, EC2, ECS)
- **[IntelliJ Setup](docs/guides/intellij-setup.md)** - IntelliJ IDEA configuration guide

## 🔧 API Endpoints

### Authentication
- `POST /api/auth/registerCandidate` - Register new candidate
- `POST /api/auth/registerEmployer` - Register new employer
- `POST /api/auth/registerCompanyStaff` - Register company staff

### Candidates
- `GET /api/candidates/getAll` - Get all candidates
- `GET /api/candidates/getById` - Get candidate by ID
- `PUT /api/candidates/update` - Update candidate
- `PUT /api/candidates/activate` - Activate candidate

### Leave Management
- `POST /api/leaves/apply` - Apply for leave
- `GET /api/leaves/balance` - Get leave balance
- `GET /api/leaves/remaining` - Get remaining leave balance
- `GET /api/leaves/employee` - Get employee leaves
- `PUT /api/leaves/approve` - Approve leave
- `PUT /api/leaves/reject` - Reject leave
- `PUT /api/leaves/cancel` - Cancel leave

### Job Management
- `GET /api/jobPostings/getAll` - Get all job postings
- `POST /api/jobPostings/add` - Create job posting
- `PUT /api/jobPostings/update` - Update job posting

## 💼 Leave Management System

The system includes a comprehensive leave management module with the following features:

### Leave Types
- **Annual Leave**: 15 days per year (counts against balance)
- **Sick Leave**: Medical leave (doesn't count against balance)
- **Personal Leave**: Personal time off (counts against balance)
- **Emergency Leave**: Urgent matters (counts against balance)
- **Maternity Leave**: For new mothers (doesn't count against balance)
- **Paternity Leave**: For new fathers (doesn't count against balance)

### Business Rules
- Each employee gets 15 annual leaves per year
- Maximum 3 pending applications per employee
- Cannot apply for leave in the past
- Cannot have overlapping leave periods
- Automatic balance tracking and validation

### Example Usage
```bash
# Apply for annual leave
curl -X POST "http://localhost:8080/api/leaves/apply" \
  -d "employeeId=1" \
  -d "startDate=2024-02-01" \
  -d "endDate=2024-02-05" \
  -d "leaveType=ANNUAL" \
  -d "reason=Vacation"

# Check leave balance
curl "http://localhost:8080/api/leaves/balance?employeeId=1&year=2024"
```

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/io/kodlama/hrms/
│   │   ├── api/controllers/          # REST Controllers
│   │   ├── business/
│   │   │   ├── abstracts/           # Service Interfaces
│   │   │   └── concretes/           # Service Implementations
│   │   ├── dataAccess/abstracts/    # Repository Interfaces
│   │   ├── entities/concretes/      # JPA Entities
│   │   ├── core/
│   │   │   ├── entities/            # Base Entities
│   │   │   └── utilities/           # Utility Classes
│   │   └── HrmsApplication.java     # Main Application Class
│   └── resources/
│       └── application.properties   # Configuration
└── test/                            # Test Classes
```

## 🗄️ Database Schema

The system uses a well-structured database schema with the following key entities:

- **User** (Base entity for all user types)
- **Candidate** (Job seekers)
- **Employer** (Companies)
- **CompanyStaff** (Company employees)
- **Leave** (Leave applications)
- **LeaveBalance** (Annual leave tracking)
- **JobPosting** (Job advertisements)
- **Resume** (Candidate resumes)

## 🔒 Security Features

- **Password Encryption**: Secure password storage
- **User Activation**: Email-based account activation
- **Input Validation**: Comprehensive data validation
- **SQL Injection Protection**: JPA/Hibernate protection
- **CORS Configuration**: Cross-origin request handling

## 🧪 Testing

Run tests using Maven:
```bash
mvn test
```

The project includes:
- Unit tests for business logic
- Integration tests for API endpoints
- Database tests for entity operations

## 🚀 Deployment

### Docker Deployment

#### Build and Run
```bash
# Build Docker image
docker build -t hrms-backend .

# Run with Docker Compose (includes PostgreSQL)
docker-compose up -d

# Run standalone
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/hrms_db \
  hrms-backend
```

### AWS Deployment

#### Quick Deploy to Elastic Beanstalk
```bash
# Using deployment script
chmod +x scripts/deploy-eb.sh
./scripts/deploy-eb.sh

# Or manually
mvn clean package
eb init -p "Java 11" -r us-east-1 hrms-backend
eb create hrms-backend-prod
eb deploy
```

#### Deploy to EC2
```bash
chmod +x scripts/deploy-ec2.sh
export EC2_HOST=your-ec2-ip
export SSH_KEY=~/.ssh/your-key.pem
./scripts/deploy-ec2.sh
```

#### Deploy to ECS/Fargate
```bash
# Build and push Docker image
chmod +x scripts/build-and-push-docker.sh
export AWS_ACCOUNT_ID=your-account-id
./scripts/build-and-push-docker.sh

# Then create ECS service (see AWS deployment guide)
```

See **[AWS Deployment Guide](docs/guides/aws-deployment.md)** for detailed instructions.

### Production Configuration
```properties
# application-prod.properties
spring.profiles.active=prod
spring.jpa.hibernate.ddl-auto=validate
logging.level.io.kodlama.hrms=INFO
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Suraj** - *Initial work* - [GitHub Profile](https://github.com/suraj)

## 🙏 Acknowledgments

- Spring Boot community for excellent framework
- PostgreSQL team for robust database
- All contributors who helped improve this project

## 📞 Support

For support and questions:
- Create an issue in the repository
- Check the documentation in `docs/` folder
- Review API documentation at `/swagger-ui.html`

## 🔄 Changelog

### Version 1.0.0
- Initial release with core HR features
- Leave management system implementation
- Swagger UI integration
- Complete API documentation

---

**Made with ❤️ using Spring Boot**