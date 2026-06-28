#!/bin/bash

# ============================================================================
# deployFramework.sh
# Builds the framework module and copies the JAR to a local Tomcat's lib
# directory so it's available to web applications at runtime.
#
# Usage:
#   ./deployFramework.sh                     # Build only
#   ./deployFramework.sh /path/to/tomcat     # Build + copy JAR to Tomcat/lib
# ============================================================================

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
FRAMEWORK_DIR="$SCRIPT_DIR/framework"
TOMCAT_LIB="${1:-}"

echo "=== Building Framework Module ==="

# Step 1: Build the JAR with Maven
cd "$FRAMEWORK_DIR"
echo "[1/2] Running mvn clean install..."
mvn clean install -DskipTests

JAR_FILE="$FRAMEWORK_DIR/target/framework-1.0.0.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "ERROR: JAR file was not created at $JAR_FILE"
    exit 1
fi

echo "[2/2] JAR built successfully: $JAR_FILE"

# Step 2: Copy to Tomcat lib if path was provided
if [ -n "$TOMCAT_LIB" ]; then
    if [ -d "$TOMCAT_LIB" ]; then
        cp "$JAR_FILE" "$TOMCAT_LIB/"
        echo "JAR deployed to Tomcat lib: $TOMCAT_LIB"
    else
        echo "WARNING: Tomcat lib directory does not exist: $TOMCAT_LIB"
        echo "Skipping deployment. Build was successful."
        exit 1
    fi
fi

echo ""
echo "=== Framework build complete ==="
