#!/bin/bash
# Docker Build and Push to ECR Script

set -e

# Configuration
AWS_REGION=${AWS_REGION:-us-east-1}
AWS_ACCOUNT_ID=${AWS_ACCOUNT_ID:-your-account-id}
ECR_REPO=${ECR_REPO:-hrms-backend}
IMAGE_TAG=${IMAGE_TAG:-latest}

ECR_URL="$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com"
FULL_IMAGE_NAME="$ECR_URL/$ECR_REPO:$IMAGE_TAG"

echo "🚀 Starting Docker Build and Push..."

# Build application
echo "📦 Building application..."
mvn clean package -DskipTests

# Build Docker image
echo "🐳 Building Docker image..."
docker build -t "$ECR_REPO:$IMAGE_TAG" .

# Login to ECR
echo "🔐 Logging into ECR..."
aws ecr get-login-password --region "$AWS_REGION" | \
    docker login --username AWS --password-stdin "$ECR_URL"

# Create ECR repository if it doesn't exist
echo "📝 Checking ECR repository..."
aws ecr describe-repositories --repository-names "$ECR_REPO" --region "$AWS_REGION" || \
    aws ecr create-repository --repository-name "$ECR_REPO" --region "$AWS_REGION"

# Tag image
echo "🏷️  Tagging image..."
docker tag "$ECR_REPO:$IMAGE_TAG" "$FULL_IMAGE_NAME"

# Push to ECR
echo "📤 Pushing to ECR..."
docker push "$FULL_IMAGE_NAME"

echo "✅ Build and push completed!"
echo "📦 Image: $FULL_IMAGE_NAME"

