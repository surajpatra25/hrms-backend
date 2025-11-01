# AWS Deployment Quick Start

Quick reference guide for deploying HRMS backend to AWS.

## Prerequisites Checklist

- [ ] AWS Account created
- [ ] AWS CLI installed and configured
- [ ] RDS PostgreSQL database created
- [ ] Security groups configured
- [ ] IAM permissions set up

## Option 1: Elastic Beanstalk (Recommended for Beginners)

### Quick Steps

```bash
# 1. Install EB CLI
pip install awsebcli

# 2. Build application
mvn clean package

# 3. Initialize EB (first time only)
eb init -p "Java 11" -r us-east-1 hrms-backend

# 4. Create environment
eb create hrms-backend-prod

# 5. Set environment variables
eb setenv \
  SPRING_PROFILES_ACTIVE=prod \
  RDS_DB_URL=jdbc:postgresql://your-rds-endpoint:5432/hrms_db \
  RDS_DB_USERNAME=your_username \
  RDS_DB_PASSWORD=your_password

# 6. Deploy
eb deploy
```

### Access Application
```bash
eb open
```

## Option 2: EC2 (Full Control)

### Quick Steps

```bash
# 1. Build application
mvn clean package

# 2. Upload to EC2
scp -i key.pem target/hrms-0.0.1-SNAPSHOT.jar ec2-user@your-ec2-ip:/home/ec2-user/

# 3. SSH into EC2
ssh -i key.pem ec2-user@your-ec2-ip

# 4. Install Java (if not installed)
sudo yum install java-11-amazon-corretto -y

# 5. Create systemd service (see deployment guide)

# 6. Start service
sudo systemctl start hrms-backend
```

## Option 3: ECS/Fargate (Container-Based)

### Quick Steps

```bash
# 1. Build Docker image
docker build -t hrms-backend .

# 2. Login to ECR
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin your-account-id.dkr.ecr.us-east-1.amazonaws.com

# 3. Create ECR repository
aws ecr create-repository --repository-name hrms-backend

# 4. Tag and push
docker tag hrms-backend:latest your-account-id.dkr.ecr.us-east-1.amazonaws.com/hrms-backend:latest
docker push your-account-id.dkr.ecr.us-east-1.amazonaws.com/hrms-backend:latest

# 5. Create ECS task definition and service
# (See full deployment guide)
```

## Environment Variables

Set these in your deployment method:

```bash
SPRING_PROFILES_ACTIVE=prod
RDS_DB_URL=jdbc:postgresql://your-rds-endpoint:5432/hrms_db
RDS_DB_USERNAME=your_username
RDS_DB_PASSWORD=your_password
```

## Security Checklist

- [ ] Use AWS Secrets Manager for passwords
- [ ] Enable SSL/TLS for RDS
- [ ] Configure security groups properly
- [ ] Use IAM roles instead of access keys
- [ ] Enable CloudWatch logging
- [ ] Set up health checks

## Common Commands

### Elastic Beanstalk
```bash
eb logs          # View logs
eb status        # Check status
eb ssh           # SSH to instance
eb deploy        # Deploy new version
eb terminate     # Delete environment
```

### EC2
```bash
# Check service status
sudo systemctl status hrms-backend

# View logs
sudo journalctl -u hrms-backend -f

# Restart service
sudo systemctl restart hrms-backend
```

### ECS
```bash
# View service status
aws ecs describe-services --cluster hrms-cluster --services hrms-backend

# View logs
aws logs tail /ecs/hrms-backend --follow

# Update service
aws ecs update-service --cluster hrms-cluster --service hrms-backend --force-new-deployment
```

## Troubleshooting

### Check Application Logs
- **EB**: `eb logs`
- **EC2**: `sudo journalctl -u hrms-backend -f`
- **ECS**: CloudWatch Logs console

### Database Connection Issues
```bash
# Test connection
psql -h your-rds-endpoint -U your_username -d hrms_db

# Check security group
aws ec2 describe-security-groups --group-ids sg-xxx
```

### Application Not Starting
1. Check Java version: `java -version`
2. Check ports: `netstat -tlnp`
3. Check logs for errors
4. Verify environment variables

## Cost Estimates (Monthly)

### Elastic Beanstalk
- **t3.small**: ~$15
- **RDS db.t3.micro**: Free tier or ~$15
- **Data transfer**: Variable
- **Total**: ~$30-50/month

### EC2
- **t3.small**: ~$15
- **RDS db.t3.micro**: ~$15
- **EBS storage**: ~$1
- **Total**: ~$30-50/month

### ECS/Fargate
- **Fargate (0.5 vCPU, 1 GB)**: ~$15
- **RDS db.t3.micro**: ~$15
- **ECR storage**: Minimal
- **Total**: ~$30-50/month

*Prices are approximate and vary by region*

## Next Steps

1. **Monitor**: Set up CloudWatch alarms
2. **Scale**: Configure auto-scaling
3. **Backup**: Set up RDS automated backups
4. **SSL**: Configure HTTPS with ALB
5. **CDN**: Add CloudFront for static content

For detailed instructions, see [AWS Deployment Guide](aws-deployment.md).

---

**Ready to deploy? Start with Elastic Beanstalk for the easiest setup! 🚀**

