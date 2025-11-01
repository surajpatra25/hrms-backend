#!/bin/bash
# Run HRMS Backend with Debug Profile

echo "Starting HRMS Backend with Debug Profile..."
mvn spring-boot:run -Dspring-boot.run.profiles=debug

