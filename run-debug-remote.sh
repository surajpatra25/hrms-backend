#!/bin/bash
# Run HRMS Backend with Debug Profile and Remote Debugging

echo "Starting HRMS Backend with Debug Profile and Remote Debugging on port 5005..."
mvn spring-boot:run -Dspring-boot.run.profiles=debug -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"

