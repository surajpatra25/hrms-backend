# IntelliJ IDEA Setup Guide

This guide explains how to set up and run the HRMS backend project in IntelliJ IDEA.

## Prerequisites

1. **IntelliJ IDEA** (Community or Ultimate Edition)
2. **Java 11** or higher installed
3. **Maven** configured in IntelliJ IDEA

## Initial Setup

### 1. Import Project

1. Open IntelliJ IDEA
2. Select **File → Open**
3. Navigate to the `hrms-backend` directory
4. Select the `pom.xml` file
5. Choose **Open as Project**

### 2. Configure JDK

1. Go to **File → Project Structure** (Ctrl+Alt+Shift+S)
2. Under **Project**, set:
   - **Project SDK**: Java 11 or higher
   - **Project language level**: 11 or higher
3. Under **Modules**, ensure the `hrms` module uses the correct SDK
4. Click **OK**

### 3. Enable Maven Auto-Import

1. When Maven asks to import changes, click **Enable Auto-Import**
2. Wait for Maven to download dependencies

### 4. Install Spring Boot Plugin (Optional but Recommended)

The run configurations work without the plugin, but installing it provides better Spring Boot support:

1. Go to **File → Settings** (Ctrl+Alt+S)
2. Navigate to **Plugins**
3. Search for **"Spring Boot"**
4. Install the **Spring Boot** plugin
5. Restart IntelliJ IDEA

## Run Configurations

Pre-configured run configurations are available in `.idea/runConfigurations/`:

### Available Configurations

1. **HRMS (Debug Profile)**
   - Profile: `debug`
   - Remote debugging: Port 5005
   - Full debug logging

2. **HRMS (Development)**
   - Profile: `dev`
   - Development logging

3. **HRMS (Production)**
   - Profile: `prod`
   - Production-optimized settings

4. **HRMS (Default)**
   - Uses default `application.properties`
   - Standard configuration

### Using Run Configurations

#### Method 1: Using Pre-configured Configurations

1. Click the run configuration dropdown (top toolbar)
2. Select the desired configuration:
   - **HRMS (Debug Profile)**
   - **HRMS (Development)**
   - **HRMS (Production)**
   - **HRMS (Default)**
3. Click **Run** (Shift+F10) or **Debug** (Shift+F9)

#### Method 2: Creating New Configuration Manually

1. Go to **Run → Edit Configurations**
2. Click **+** → **Application**
3. Configure:
   - **Name**: `HRMS Debug`
   - **Main class**: `io.kodlama.hrms.HrmsApplication`
   - **VM options**: `-Xms512m -Xmx1024m -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005`
   - **Program arguments**: `--spring.profiles.active=debug`
   - **Environment variables**: `SPRING_PROFILES_ACTIVE=debug`
   - **Working directory**: `$PROJECT_DIR$`
4. Click **OK**

## Debugging

### Using Debug Profile

1. Select **HRMS (Debug Profile)** from run configurations
2. Click **Debug** (Shift+F9)
3. The application will start with remote debugging on port 5005

### Setting Breakpoints

1. Open any Java file
2. Click in the left margin next to the line number
3. A red dot appears indicating a breakpoint
4. Run in debug mode
5. When execution reaches the breakpoint, it will pause

### Debugging Tips

- **Step Over** (F8): Execute current line
- **Step Into** (F7): Step into method calls
- **Step Out** (Shift+F8): Step out of current method
- **Resume** (F9): Continue execution
- **Evaluate Expression** (Alt+F8): Evaluate code expressions

## Remote Debugging

### Attach to Running Application

If you've started the application with remote debugging enabled:

1. Go to **Run → Edit Configurations**
2. Click **+** → **Remote JVM Debug**
3. Configure:
   - **Name**: `Remote HRMS Debug`
   - **Host**: `localhost`
   - **Port**: `5005`
4. Click **Debug**

### Starting Application with Remote Debugging

Use the **HRMS (Debug Profile)** configuration, which automatically enables remote debugging on port 5005.

## Troubleshooting

### Issue: "Unknown run configuration type"

**Solution**: 
- The run configurations use standard Java Application type, which works without Spring Boot plugin
- If you prefer Spring Boot configurations, install the Spring Boot plugin (see above)

### Issue: Module not found

**Solution**:
1. Right-click on `pom.xml`
2. Select **Maven → Reload Project**
3. Wait for dependencies to download

### Issue: Main class not found

**Solution**:
1. Go to **File → Project Structure**
2. Under **Modules → hrms → Sources**, ensure `src/main/java` is marked as Sources
3. Click **OK**
4. Rebuild project: **Build → Rebuild Project**

### Issue: Spring profiles not working

**Solution**:
1. Verify the profile name in run configuration
2. Check that `application-{profile}.properties` files exist in `src/main/resources`
3. Verify environment variable `SPRING_PROFILES_ACTIVE` is set correctly

### Issue: Database connection errors

**Solution**:
1. Check `application.properties` or profile-specific properties
2. Verify database is running
3. Check connection credentials
4. Test connection using database tools

## Code Style and Formatting

### Configure Code Style

1. Go to **File → Settings → Editor → Code Style → Java**
2. Import or configure code style settings
3. Set tab size: 4 spaces
4. Enable "Use tab character" if preferred

### Auto-format on Save

1. Go to **File → Settings → Editor → Actions on Save**
2. Check "Reformat code"
3. Check "Optimize imports"
4. Click **OK**

## Useful Shortcuts

- **Build Project**: Ctrl+F9
- **Rebuild Project**: Ctrl+Shift+F9
- **Run**: Shift+F10
- **Debug**: Shift+F9
- **Stop**: Ctrl+F2
- **Search Everywhere**: Double Shift
- **Find Usages**: Alt+F7
- **Go to Declaration**: Ctrl+B
- **Show Parameters**: Ctrl+P
- **Generate Code**: Alt+Insert

## Maven Integration

### Run Maven Goals

1. Open **View → Tool Windows → Maven**
2. Navigate to **hrms → Lifecycle**
3. Double-click any goal:
   - `clean`: Clean build
   - `compile`: Compile sources
   - `test`: Run tests
   - `package`: Create JAR
   - `install`: Install to local repository

### Terminal Access

1. Open **View → Tool Windows → Terminal**
2. Use Maven commands:
   ```bash
   mvn clean install
   mvn spring-boot:run
   mvn spring-boot:run -Dspring-boot.run.profiles=debug
   ```

## Project Structure

The project should have this structure:

```
hrms-backend/
├── src/
│   ├── main/
│   │   ├── java/io/kodlama/hrms/
│   │   │   ├── api/controllers/
│   │   │   ├── business/
│   │   │   ├── dataAccess/
│   │   │   ├── entities/
│   │   │   └── HrmsApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-debug.properties
│   │       ├── application-dev.properties
│   │       └── application-prod.properties
│   └── test/
├── pom.xml
└── .idea/
    └── runConfigurations/
        ├── HRMS_Debug_Profile.xml
        ├── HRMS_Development.xml
        ├── HRMS_Production.xml
        └── HRMS_Default.xml
```

## Additional Tips

### Enable Annotation Processing

1. Go to **File → Settings → Build → Compiler → Annotation Processors**
2. Check "Enable annotation processing"
3. Click **OK**

### Configure Lombok (if using)

1. Install Lombok plugin: **File → Settings → Plugins → Search "Lombok"**
2. Enable annotation processing (see above)
3. Restart IntelliJ IDEA

### Show Line Numbers

1. Go to **File → Settings → Editor → General → Appearance**
2. Check "Show line numbers"

### Enable Auto Import

1. Go to **File → Settings → Editor → General → Auto Import**
2. Check "Add unambiguous imports on the fly"
3. Check "Optimize imports on the fly"

---

**Happy Coding with IntelliJ IDEA! 🚀**

