# HR Policy Files Storage Guide

## Overview
This guide explains how policy files are stored and persisted in different deployment scenarios.

---

## Storage Options

### Option 1: Bind Mount (Recommended for Production)

**What it is:** Maps a directory on your host server directly to the container.

**Advantages:**
- ✅ Full control over file location
- ✅ Easy to backup (just backup the directory)
- ✅ Easy to access files directly on server
- ✅ Can be included in server backups
- ✅ Files persist permanently on server

**Configuration:**
```yaml
volumes:
  - ./policy-files:/app/policy-files
```

**Setup on Server:**
```bash
# 1. Create directory on server
mkdir -p /opt/hrms/policy-files

# 2. Set proper permissions
sudo chown -R 1000:1000 /opt/hrms/policy-files
sudo chmod -R 755 /opt/hrms/policy-files

# 3. Update docker-compose.yml to use absolute path
# Change: - ./policy-files:/app/policy-files
# To:     - /opt/hrms/policy-files:/app/policy-files
```

**Backup:**
```bash
# Backup policy files
tar czf policy-files-backup-$(date +%Y%m%d).tar.gz /opt/hrms/policy-files

# Restore
tar xzf policy-files-backup-20250111.tar.gz -C /
```

---

### Option 2: Named Docker Volume

**What it is:** Docker manages the volume internally.

**Advantages:**
- ✅ Managed by Docker
- ✅ Works across different host systems
- ✅ Good for development

**Disadvantages:**
- ❌ Less control over file location
- ❌ Harder to backup
- ❌ Files stored in Docker's internal directory

**Configuration:**
```yaml
volumes:
  - hrms-policy-files:/app/policy-files

volumes:
  hrms-policy-files:
    driver: local
```

**Volume Location:**
- Linux: `/var/lib/docker/volumes/hrms-backend_hrms-policy-files/_data/`
- Docker Desktop (Mac/Windows): Inside Docker VM

**Backup:**
```bash
# Backup
docker run --rm \
  -v hrms-backend_hrms-policy-files:/data \
  -v $(pwd):/backup \
  alpine tar czf /backup/policy-files-backup.tar.gz -C /data .

# Restore
docker run --rm \
  -v hrms-backend_hrms-policy-files:/data \
  -v $(pwd):/backup \
  alpine tar xzf /backup/policy-files-backup.tar.gz -C /data
```

---

## Persistence Guarantee

### What Persists:
✅ Container stop/start
✅ Container restart
✅ Container removal and recreation
✅ Host server restart
✅ Docker daemon restart
✅ `docker-compose down` (without `-v` flag)

### What Removes Data:
❌ `docker volume rm <volume-name>`
❌ `docker-compose down -v` (the `-v` flag)
❌ `docker system prune -a --volumes`
❌ Manually deleting the bind mount directory

---

## Recommended Production Setup

### On EC2/Linux Server:

```bash
# 1. SSH to your server
ssh your-server

# 2. Create directory structure
sudo mkdir -p /opt/hrms/policy-files
sudo chown -R $(whoami):$(whoami) /opt/hrms/policy-files

# 3. Create docker-compose.yml with absolute path
cat > docker-compose.yml <<EOF
version: '3.8'

services:
  hrms:
    image: sspatra25/hrms:latest
    container_name: hrms
    ports:
      - "80:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
    volumes:
      - /opt/hrms/policy-files:/app/policy-files
    networks:
      - hrms-network
    restart: unless-stopped

networks:
  hrms-network:
    driver: bridge
EOF

# 4. Start container
docker-compose up -d

# 5. Verify volume mount
docker inspect hrms | grep -A 10 Mounts
```

---

## File Permissions

### Linux/EC2:
```bash
# Check current permissions
ls -la /opt/hrms/policy-files

# Set correct permissions (user ID 1000 is the 'hrms' user in container)
sudo chown -R 1000:1000 /opt/hrms/policy-files
sudo chmod -R 755 /opt/hrms/policy-files
```

### Fix Permission Issues:
If you get "Permission denied" errors:

```bash
# Option 1: Match container user
sudo chown -R 1000:1000 /opt/hrms/policy-files

# Option 2: Use current user (if you modified Dockerfile)
sudo chown -R $(id -u):$(id -g) /opt/hrms/policy-files
```

---

## Backup Strategies

### Option 1: Simple File Backup
```bash
# Daily backup script
#!/bin/bash
BACKUP_DIR="/backups/hrms-policies"
DATE=$(date +%Y%m%d)

mkdir -p $BACKUP_DIR
tar czf $BACKUP_DIR/policy-files-$DATE.tar.gz /opt/hrms/policy-files

# Keep only last 30 days
find $BACKUP_DIR -name "policy-files-*.tar.gz" -mtime +30 -delete
```

### Option 2: Sync to S3
```bash
# Install AWS CLI
aws s3 sync /opt/hrms/policy-files s3://your-bucket/hrms-policies/ \
  --storage-class STANDARD_IA \
  --delete

# Set up cron job for daily sync
0 2 * * * aws s3 sync /opt/hrms/policy-files s3://your-bucket/hrms-policies/ --delete
```

### Option 3: Database + File Backup
```bash
# Backup both database and policy files
#!/bin/bash
DATE=$(date +%Y%m%d)
pg_dump hrms_db > /backups/db-$DATE.sql
tar czf /backups/policies-$DATE.tar.gz /opt/hrms/policy-files
```

---

## Migration Between Servers

### From Server A to Server B:

**On Server A:**
```bash
# Create backup
cd /opt/hrms
tar czf policy-files-migration.tar.gz policy-files/

# Transfer to Server B
scp policy-files-migration.tar.gz user@server-b:/tmp/
```

**On Server B:**
```bash
# Create directory
sudo mkdir -p /opt/hrms/policy-files

# Extract backup
cd /opt/hrms
sudo tar xzf /tmp/policy-files-migration.tar.gz

# Fix permissions
sudo chown -R 1000:1000 /opt/hrms/policy-files

# Start container
docker-compose up -d
```

---

## Monitoring Storage

### Check Disk Usage:
```bash
# Check policy files directory size
du -sh /opt/hrms/policy-files

# Check available disk space
df -h /opt/hrms

# List all policy files
ls -lh /opt/hrms/policy-files
```

### Set Up Alerts:
```bash
# Alert if directory size exceeds 1GB
SIZE=$(du -sm /opt/hrms/policy-files | cut -f1)
if [ $SIZE -gt 1024 ]; then
  echo "Warning: Policy files directory is using ${SIZE}MB"
fi
```

---

## Troubleshooting

### Container Can't Write Files

**Problem:** "Permission denied" when uploading policies

**Solution:**
```bash
# Check container user ID
docker exec hrms id

# Match host directory permissions
sudo chown -R 1000:1000 /opt/hrms/policy-files
sudo chmod -R 755 /opt/hrms/policy-files
```

### Files Disappear After Restart

**Cause:** Using wrong path or volume was deleted

**Solution:**
```bash
# Check mounted volumes
docker inspect hrms | grep -A 10 Mounts

# Verify volume configuration in docker-compose.yml
cat docker-compose.yml | grep -A 2 volumes
```

### Can't Find Files on Host

**If using named volume:**
```bash
# Find volume location
docker volume inspect hrms-backend_hrms-policy-files

# Access files
sudo ls -la /var/lib/docker/volumes/hrms-backend_hrms-policy-files/_data/
```

---

## Best Practices

1. ✅ **Use absolute paths** in production: `/opt/hrms/policy-files` instead of `./policy-files`
2. ✅ **Set proper permissions** before starting container
3. ✅ **Regular backups** - automate daily backups
4. ✅ **Monitor disk space** - set up alerts
5. ✅ **Test restore** - verify backups actually work
6. ✅ **Document location** - keep track of where files are stored
7. ✅ **Use bind mounts** for production (easier management)
8. ✅ **Include in server backups** - add to backup scripts

---

## Quick Reference

| Scenario | Command |
|----------|---------|
| View volume location | `docker volume inspect <volume-name>` |
| Check mount points | `docker inspect hrms \| grep -A 10 Mounts` |
| View files in container | `docker exec hrms ls -la /app/policy-files` |
| View files on host | `ls -la /opt/hrms/policy-files` |
| Fix permissions | `sudo chown -R 1000:1000 /opt/hrms/policy-files` |
| Backup files | `tar czf backup.tar.gz /opt/hrms/policy-files` |
| Check disk usage | `du -sh /opt/hrms/policy-files` |

