#!/bin/bash

# ============================================================================
# deployTestApplication.sh
# Builds the test-application module and deploys the WAR to a local Tomcat
# webapps directory.
#
# Usage:
#   ./deployTestApplication.sh /path/to/tomcat/webapps
#
# Example:
#   ./deployTestApplication.sh /opt/tomcat/webapps
# ============================================================================

set -euo pipefail

if [ $# -lt 1 ]; then
    echo "Usage: $0 <tomcat-webapps-directory>"
    echo "Example: $0 /opt/tomcat/webapps"
    exit 1
fi

TOMCAT_WEBAPPS="$1"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TEST_APP_DIR="$SCRIPT_DIR/test-application"

echo "=== Building Test Application WAR ==="

cd "$TEST_APP_DIR"
echo "[1/3] Running mvn clean package..."
mvn clean package -DskipTests

WAR_FILE="$TEST_APP_DIR/target/test-application-1.0.0.war"

if [ ! -f "$WAR_FILE" ]; then
    echo "ERROR: WAR file was not created at $WAR_FILE"
    exit 1
fi

echo "[2/3] WAR built successfully: $WAR_FILE"

if [ ! -d "$TOMCAT_WEBAPPS" ]; then
    echo "ERROR: Tomcat webapps directory does not exist: $TOMCAT_WEBAPPS"
    exit 1
fi

echo "[3/3] Copying WAR to Tomcat webapps..."
cp "$WAR_FILE" "$TOMCAT_WEBAPPS/"

echo ""
echo "=== Deployment complete ==="
echo "WAR deployed to: $TOMCAT_WEBAPPS/test-application-1.0.0.war"
echo ""
echo "Next steps:"
echo "  1. Make sure Tomcat is running"
echo "  2. Access the application at: http://localhost:8080/test-application-1.0.0/"
echo "  3. (Optional) Rename WAR to 'test-app.war' for a shorter context path"
