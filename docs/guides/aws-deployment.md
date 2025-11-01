# AWS Deployment Guide

This guide covers various methods to deploy the HRMS backend to AWS.

## Table of Contents

1. [AWS Elastic Beanstalk](#aws-elastic-beanstalk) - Easiest option
2. [AWS EC2](#aws-ec2) - More control
3. [AWS ECS/Fargate](#aws-ecsfargate) - Container-based
4. [AWS RDS Setup](#aws-rds-setup) - Database setup
5. [Best Practices](#best-practices)
6. [Troubleshooting](#troubleshooting)

## Prerequisites

- AWS Account
- AWS CLI installed and configured
- Maven installed
- Java 11 or higher
- Git (optional)

### Install AWS CLI

```bash
# Windows (using PowerShell)
Invoke-WebRequest -Uri "https://awscli.amazonaws.com/AWSCLIV2.msi" -OutFile "$env:TEMP\AWSCLIV2.msi"
Start-Process msiexec.exe -Wait -ArgumentList '/I "$env:TEMP\AWSCLIV2.msi" /quiet'

# Linux
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install

# macOS
brew install awscli
```

### Configure AWS CLI

```bash
aws configure
# Enter your:
# AWS Access Key ID
# AWS Secret Access Key
# Default region (e.g., us-east-1)
# Default output format (json)
```

---

## AWS Elastic Beanstalk

Elastic Beanstalk is the easiest way to deploy Spring Boot applications to AWS.

### Step 1: Prepare Application

#### Update application.properties for Production

```properties
# src/main/resources/application-prod.properties
spring.datasource.url=${RDS_DB_URL}
spring.datasource.username=${RDS_DB_USERNAME}
spring.datasource.password=${RDS_DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

server.port=5000

logging.level.root=WARN
logging.level.io.kodlama.hrms=INFO
```

#### Create .ebextensions Configuration

Create directory: `.ebextensions/01-java.config`

```yaml
option_settings:
  aws:elasticbeanstalk:environment:
    EnvironmentType: LoadBalanced
  aws:elasticbeanstalk:application:environment:
    SPRING_PROFILES_ACTIVE: prod
    SERVER_PORT: 5000
    JAVA_HOME: /usr/lib/jvm/java-11-amazon-corretto
  aws:autoscaling:launchconfiguration:
    InstanceType: t3.small
    IamInstanceProfile: aws-elasticbeanstalk-ec2-role
  aws:elasticbeanstalk:healthreporting:system:
    SystemType: enhanced
```

### Step 2: Install EB CLI

```bash
# Windows
pip install awsebcli

# Linux/Mac
pip3 install awsebcli
```

### Step 3: Initialize Elastic Beanstalk

```bash
# Build the application
mvn clean package

# Initialize EB (first time only)
eb init -p "Java 11" -r us-east-1 hrms-backend

# Create environment
eb create hrms-backend-prod
```

### Step 4: Configure Environment Variables

```bash
# Set RDS connection
eb setenv RDS_DB_URL=jdbc:postgresql://your-rds-endpoint:5432/hrms_db \
         RDS_DB_USERNAME=your_username \
         RDS_DB_PASSWORD=your_password \
         SPRING_PROFILES_ACTIVE=prod
```

### Step 5: Deploy

```bash
# Build and deploy
mvn clean package
eb deploy
```

### Step 6: Access Application

```bash
# Get application URL
eb status

# Open in browser
eb open
```

### Managing Environment

```bash
# View logs
eb logs

# SSH into instance
eb ssh

# View environment health
eb health

# Update environment
eb deploy

# Terminate environment
eb terminate
```

---

## AWS EC2

Deploy directly to EC2 instance for more control.

### Step 1: Launch EC2 Instance

1. Go to **EC2 Console** → **Launch Instance**
2. Choose **Amazon Linux 2 AMI** or **Ubuntu Server**
3. Select **t3.small** or larger
4. Configure Security Group:
   - **HTTP**: Port 80
   - **HTTPS**: Port 443
   - **SSH**: Port 22
   - **Application**: Port 8080 (or your port)
5. Launch and save key pair

### Step 2: Connect to EC2

```bash
# SSH into instance
ssh -i your-key.pem ec2-user@your-ec2-ip

# For Ubuntu
ssh -i your-key.pem ubuntu@your-ec2-ip
```

### Step 3: Install Required Software

#### On Amazon Linux 2

```bash
# Update system
sudo yum update -y

# Install Java 11
sudo amazon-linux-extras install java-openjdk11 -y

# Install Maven
sudo wget https://archive.apache.org/dist/maven/maven-3/3.8.6/binaries/apache-maven-3.8.6-bin.tar.gz
sudo tar -xzf apache-maven-3.8.6-bin.tar.gz -C /opt/
sudo ln -s /opt/apache-maven-3.8.6 /opt/maven

# Add to PATH
echo 'export PATH=/opt/maven/bin:$PATH' >> ~/.bashrc
source ~/.bashrc

# Install PostgreSQL client (for connection testing)
sudo yum install postgresql -y
```

#### On Ubuntu

```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Java 11
sudo apt install openjdk-11-jdk -y

# Install Maven
sudo apt install maven -y
```

### Step 4: Build Application

```bash
# Clone repository (or upload files)
git clone your-repo-url
cd hrms-backend

# Build application
mvn clean package -DskipTests

# Or build locally and transfer
# On local machine:
mvn clean package -DskipTests
scp -i your-key.pem target/hrms-0.0.1-SNAPSHOT.jar ec2-user@your-ec2-ip:/home/ec2-user/
```

### Step 5: Create systemd Service

Create `/etc/systemd/system/hrms-backend.service`:

```ini
[Unit]
Description=HRMS Backend Application
After=network.target

[Service]
Type=simple
User=ec2-user
WorkingDirectory=/home/ec2-user/hrms-backend
ExecStart=/usr/bin/java -Xms512m -Xmx1024m -jar /home/ec2-user/hrms-backend/hrms-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal

Environment="SPRING_PROFILES_ACTIVE=prod"
Environment="RDS_DB_URL=jdbc:postgresql://your-rds-endpoint:5432/hrms_db"
Environment="RDS_DB_USERNAME=your_username"
Environment="RDS_DB_PASSWORD=your_password"

[Install]
WantedBy=multi-user.target
```

### Step 6: Start Service

```bash
# Reload systemd
sudo systemctl daemon-reload

# Enable service
sudo systemctl enable hrms-backend

# Start service
sudo systemctl start hrms-backend

# Check status
sudo systemctl status hrms-backend

# View logs
sudo journalctl -u hrms-backend -f
```

### Step 7: Configure Nginx (Reverse Proxy)

Install Nginx:

```bash
# Amazon Linux 2
sudo yum install nginx -y

# Ubuntu
sudo apt install nginx -y
```

Configure `/etc/nginx/conf.d/hrms.conf`:

```nginx
upstream hrms_backend {
    server localhost:8080;
}

server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://hrms_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Start Nginx:

```bash
sudo systemctl enable nginx
sudo systemctl start nginx
sudo systemctl status nginx
```

---

## AWS ECS/Fargate

Deploy using Docker containers for better scalability.

### Step 1: Create Dockerfile

Create `Dockerfile` in project root:

```dockerfile
FROM openjdk:11-jre-slim

WORKDIR /app

# Copy JAR file
COPY target/hrms-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Environment variables
ENV SPRING_PROFILES_ACTIVE=prod

# Run application
ENTRYPOINT ["java", "-Xms512m", "-Xmx1024m", "-jar", "app.jar"]
```

### Step 2: Build and Push Docker Image

```bash
# Build Docker image
docker build -t hrms-backend:latest .

# Tag for ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin your-account-id.dkr.ecr.us-east-1.amazonaws.com

# Create ECR repository
aws ecr create-repository --repository-name hrms-backend

# Tag image
docker tag hrms-backend:latest your-account-id.dkr.ecr.us-east-1.amazonaws.com/hrms-backend:latest

# Push to ECR
docker push your-account-id.dkr.ecr.us-east-1.amazonaws.com/hrms-backend:latest
```

### Step 3: Create ECS Task Definition

Create `task-definition.json`:

```json
{
  "family": "hrms-backend",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "512",
  "memory": "1024",
  "containerDefinitions": [
    {
      "name": "hrms-backend",
      "image": "your-account-id.dkr.ecr.us-east-1.amazonaws.com/hrms-backend:latest",
      "portMappings": [
        {
          "containerPort": 8080,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "SPRING_PROFILES_ACTIVE",
          "value": "prod"
        }
      ],
      "secrets": [
        {
          "name": "RDS_DB_URL",
          "valueFrom": "arn:aws:secretsmanager:us-east-1:account-id:secret:hrms-db-url"
        },
        {
          "name": "RDS_DB_USERNAME",
          "valueFrom": "arn:aws:secretsmanager:us-east-1:account-id:secret:hrms-db-username"
        },
        {
          "name": "RDS_DB_PASSWORD",
          "valueFrom": "arn:aws:secretsmanager:us-east-1:account-id:secret:hrms-db-password"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/hrms-backend",
          "awslogs-region": "us-east-1",
          "awslogs-stream-prefix": "ecs"
        }
      }
    }
  ]
}
```

### Step 4: Register Task Definition

```bash
aws ecs register-task-definition --cli-input-json file://task-definition.json
```

### Step 5: Create ECS Service

```bash
aws ecs create-service \
  --cluster hrms-cluster \
  --service-name hrms-backend \
  --task-definition hrms-backend \
  --desired-count 1 \
  --launch-type FARGATE \
  --network-configuration "awsvpcConfiguration={subnets=[subnet-xxx],securityGroups=[sg-xxx],assignPublicIp=ENABLED}"
```

---

## AWS RDS Setup

Set up PostgreSQL database for production.

### Step 1: Create RDS Instance

1. Go to **RDS Console** → **Create Database**
2. Choose **PostgreSQL**
3. Select **Free tier** or production template
4. Configure:
   - **DB Instance Identifier**: `hrms-db`
   - **Master Username**: `hrms_admin`
   - **Master Password**: (secure password)
   - **DB Instance Class**: `db.t3.micro` (free tier) or `db.t3.small`
   - **Storage**: 20 GB
   - **VPC**: Default or your VPC
   - **Security Group**: Create new or use existing
5. Click **Create Database**

### Step 2: Configure Security Group

1. Select RDS instance → **Connectivity & security**
2. Click on Security Group
3. Add **Inbound Rule**:
   - **Type**: PostgreSQL
   - **Source**: Security group of your EC2/Elastic Beanstalk
   - Or temporarily: Your IP for testing

### Step 3: Get Connection Endpoint

1. Select RDS instance
2. Copy **Endpoint** (e.g., `hrms-db.xxxxx.us-east-1.rds.amazonaws.com`)
3. Use this in your application properties

### Step 4: Create Database

Connect to RDS and create database:

```bash
# Connect using psql
psql -h your-rds-endpoint -U hrms_admin -d postgres

# Create database
CREATE DATABASE hrms_db;

# Grant permissions
GRANT ALL PRIVILEGES ON DATABASE hrms_db TO hrms_admin;

# Exit
\q
```

### Step 5: Update Application Configuration

```properties
spring.datasource.url=jdbc:postgresql://your-rds-endpoint:5432/hrms_db
spring.datasource.username=hrms_admin
spring.datasource.password=your_password
```

---

## Best Practices

### Security

1. **Use AWS Secrets Manager** for sensitive data:
   ```bash
   aws secretsmanager create-secret \
     --name hrms-db-password \
     --secret-string "your-secure-password"
   ```

2. **Enable SSL/TLS** for RDS connections

3. **Use IAM Roles** instead of access keys

4. **Enable VPC** for network isolation

5. **Use Security Groups** to restrict access

### Monitoring

1. **CloudWatch Logs**: Monitor application logs
2. **CloudWatch Metrics**: Track performance
3. **CloudWatch Alarms**: Set up alerts
4. **Application Load Balancer**: For high availability

### Cost Optimization

1. **Use Reserved Instances** for long-term deployments
2. **Right-size instances** based on actual usage
3. **Use Auto Scaling** to handle traffic
4. **Enable RDS Multi-AZ** only if needed (increases cost)

### CI/CD

Set up automated deployment using:
- **AWS CodePipeline**
- **AWS CodeBuild**
- **GitHub Actions** with AWS deployment
- **Jenkins** on EC2

---

## Troubleshooting

### Issue: Application not starting

**Check logs:**
```bash
# Elastic Beanstalk
eb logs

# EC2
sudo journalctl -u hrms-backend -f

# ECS
aws logs tail /ecs/hrms-backend --follow
```

### Issue: Database connection errors

**Verify:**
1. Security group allows PostgreSQL port (5432)
2. RDS endpoint is correct
3. Credentials are correct
4. Database exists

**Test connection:**
```bash
psql -h your-rds-endpoint -U hrms_admin -d hrms_db
```

### Issue: Out of memory

**Increase heap size:**
```bash
# Update VM options
-Xms1024m -Xmx2048m
```

### Issue: Application slow

**Check:**
1. Instance size (upgrade if needed)
2. Database performance (CloudWatch metrics)
3. Network latency
4. Application logs for errors

### Issue: Port not accessible

**Verify:**
1. Security group rules
2. Application is running
3. Nginx/Apache configuration (if using)
4. Load balancer configuration

---

## Quick Deployment Commands

### Elastic Beanstalk
```bash
mvn clean package
eb deploy
```

### EC2
```bash
mvn clean package
scp target/hrms-0.0.1-SNAPSHOT.jar user@ec2-ip:/path/
ssh user@ec2-ip
sudo systemctl restart hrms-backend
```

### ECS
```bash
mvn clean package
docker build -t hrms-backend .
docker push your-ecr-url/hrms-backend
aws ecs update-service --cluster hrms-cluster --service hrms-backend --force-new-deployment
```

---

## Additional Resources

- [AWS Elastic Beanstalk Documentation](https://docs.aws.amazon.com/elasticbeanstalk/)
- [AWS EC2 Documentation](https://docs.aws.amazon.com/ec2/)
- [AWS ECS Documentation](https://docs.aws.amazon.com/ecs/)
- [AWS RDS Documentation](https://docs.aws.amazon.com/rds/)
- [Spring Boot on AWS](https://aws.amazon.com/blogs/opensource/running-spring-boot-applications-on-aws/)

---

**Happy Deploying to AWS! 🚀☁️**
