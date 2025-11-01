# HRMS Backend Documentation

Welcome to the comprehensive documentation for the HRMS (Human Resource Management System) backend. This documentation covers all aspects of the system from setup to advanced usage.

## 📚 Documentation Structure

### [API Documentation](api/README.md)
Complete reference for all REST API endpoints including:
- Authentication endpoints
- Candidate management
- Leave management system
- Job posting management
- Request/response examples
- Error handling

### [Entity Documentation](entities/README.md)
Detailed information about database entities and relationships:
- Entity descriptions and fields
- Database relationships
- Schema diagrams
- Foreign key constraints

### [Setup Guide](guides/setup.md)
Step-by-step installation and configuration:
- Prerequisites and requirements
- Database setup
- Application configuration
- Docker deployment
- Troubleshooting

### [Leave Management Guide](guides/leave-management.md)
Comprehensive guide for the leave management system:
- System overview and features
- Business rules and validation
- API usage examples
- Database schema
- Performance considerations

## 🚀 Quick Links

### Getting Started
1. [Setup Guide](guides/setup.md) - Install and configure the system
2. [API Documentation](api/README.md) - Learn about available endpoints
3. [Leave Management](guides/leave-management.md) - Understand the leave system

### Development
1. [Entity Documentation](entities/README.md) - Database schema reference
2. [API Documentation](api/README.md) - API endpoint reference
3. [Setup Guide](guides/setup.md) - Development environment setup

### Production
1. [Setup Guide](guides/setup.md) - Production deployment
2. [API Documentation](api/README.md) - API monitoring and usage
3. [Leave Management](guides/leave-management.md) - System administration

## 🔧 System Overview

The HRMS backend is built with Spring Boot and provides:

- **User Management**: Support for candidates, employers, and company staff
- **Job Management**: Job postings and recruitment workflow
- **Resume Management**: Complete resume builder
- **Leave Management**: Comprehensive leave application and tracking system
- **Authentication**: Secure user registration and activation
- **API Documentation**: Swagger UI integration

## 📋 Key Features

### Leave Management System
- 15 annual leaves per employee
- Multiple leave types (Annual, Sick, Personal, Emergency, Maternity, Paternity)
- Manager approval workflow
- Balance tracking and validation
- Date overlap prevention

### User Management
- Three user types: Candidates, Employers, Company Staff
- Email-based activation
- Turkish National Identity verification
- Secure password handling

### Job Management
- Job posting creation and management
- Application tracking
- Employer and candidate matching

## 🛠️ Technology Stack

- **Backend**: Java 11, Spring Boot 2.5.0
- **Database**: PostgreSQL 12+ / MySQL 8.0+
- **Build Tool**: Maven 3.6+
- **Documentation**: Swagger/OpenAPI 2.9.2
- **Cloud Storage**: Cloudinary
- **Validation**: Bean Validation

## 📞 Support

For questions or issues:
1. Check the relevant documentation section
2. Review the troubleshooting guides
3. Create an issue in the repository
4. Contact the development team

## 🔄 Documentation Updates

This documentation is regularly updated to reflect the latest system changes. Check the changelog for recent updates.

---

**Last Updated**: October 2024  
**Version**: 1.0.0
