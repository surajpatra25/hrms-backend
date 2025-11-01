#!/bin/bash
# Elastic Beanstalk Deployment Script

set -e

echo "🚀 Starting Elastic Beanstalk Deployment..."

# Build application
echo "📦 Building application..."
mvn clean package -DskipTests

# Check if EB is initialized
if [ ! -f ".elasticbeanstalk/config.yml" ]; then
    echo "⚠️  Elastic Beanstalk not initialized. Initializing now..."
    read -p "Enter AWS region (default: us-east-1): " region
    region=${region:-us-east-1}
    eb init -p "Java 11" -r "$region" hrms-backend
fi

# Deploy
echo "☁️  Deploying to Elastic Beanstalk..."
eb deploy

echo "✅ Deployment completed!"
echo "🌐 Getting application URL..."
eb status | grep "CNAME"

