#!/bin/bash
# EC2 Deployment Script

set -e

# Configuration
EC2_USER=${EC2_USER:-ec2-user}
EC2_HOST=${EC2_HOST:-your-ec2-ip}
SSH_KEY=${SSH_KEY:-~/.ssh/your-key.pem}
APP_DIR=${APP_DIR:-/home/ec2-user/hrms-backend}

echo "🚀 Starting EC2 Deployment..."

# Build application
echo "📦 Building application..."
mvn clean package -DskipTests

# Copy JAR to EC2
echo "📤 Uploading to EC2..."
scp -i "$SSH_KEY" target/hrms-0.0.1-SNAPSHOT.jar "$EC2_USER@$EC2_HOST:$APP_DIR/"

# Restart service
echo "🔄 Restarting service..."
ssh -i "$SSH_KEY" "$EC2_USER@$EC2_HOST" "sudo systemctl restart hrms-backend"

echo "✅ Deployment completed!"
echo "📊 Checking service status..."
ssh -i "$SSH_KEY" "$EC2_USER@$EC2_HOST" "sudo systemctl status hrms-backend"

