#!/bin/bash

echo "Starting RBAC System..."
echo ""

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    exit 1
fi

# Make gradlew executable
chmod +x gradlew

# Run the application
./gradlew bootRun
