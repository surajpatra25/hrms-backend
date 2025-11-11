# Quick Fix: Permission Issues with Policy Files

## Problem
Getting error: "Failed to upload file: ./policy-files/holiday_policy.pdf"

This is a **permissions issue** - the container can't write to the mounted directory.

---

## Solution 1: Fix Host Directory Permissions (Fastest - No Rebuild)

If you're using a bind mount, fix permissions on your host machine:

### On Linux/EC2 Server:
```bash
# Stop container
docker-compose down

# Fix permissions on host directory
sudo chmod -R 777 ./policy-files
# OR better, set ownership to UID 1000
sudo chown -R 1000:1000 ./policy-files

# Start container
docker-compose up -d
```

### On Your Development Machine:
```bash
# Stop container
docker-compose down

# Make directory writable
chmod -R 777 ./policy-files

# Start container
docker-compose up -d
```

---

## Solution 2: Rebuild with Updated Dockerfile (Permanent Fix)

The Dockerfile has been updated to handle permissions better. Rebuild and redeploy:

```bash
# Rebuild the image
docker-compose down
docker build -t sspatra25/hrms:latest .

# If pushing to registry
docker push sspatra25/hrms:latest

# Start with new image
docker-compose up -d
```

---

## Solution 3: Run Container as Root (Not Recommended for Production)

Update `docker-compose.yml`:

```yaml
services:
  hrms:
    image: sspatra25/hrms:latest
    user: "0:0"  # Run as root
    # ... rest of config
```

**Warning:** This is less secure and not recommended for production.

---

## Verify Fix

After applying any solution:

```bash
# Check if directory is writable
docker exec hrms touch /app/policy-files/test.txt
docker exec hrms ls -la /app/policy-files

# If successful, delete test file
docker exec hrms rm /app/policy-files/test.txt
```

If you see the test.txt file, permissions are correct!

---

## For Production Deployment

Use absolute path with correct permissions:

```bash
# On server
sudo mkdir -p /opt/hrms/policy-files
sudo chmod 777 /opt/hrms/policy-files
# OR
sudo chown -R 1000:1000 /opt/hrms/policy-files
```

Update `docker-compose.yml`:
```yaml
volumes:
  - /opt/hrms/policy-files:/app/policy-files
```

---

## Check Current Permissions

```bash
# Inside container
docker exec hrms ls -la /app/policy-files

# On host (if using bind mount)
ls -la ./policy-files

# Check what user the container is running as
docker exec hrms id
```

Expected output: `uid=1000(hrms) gid=1000(hrms)`

---

## Most Common Fix (TL;DR)

```bash
docker-compose down
sudo chmod -R 777 ./policy-files
docker-compose up -d
```

Test upload again - it should work! ✅

