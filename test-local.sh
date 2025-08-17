#!/bin/bash

# Fast Image Java - Local Test Script
# This script downloads native libraries and runs tests locally

set -e

echo "🚀 Fast Image Java - Local Test Setup"
echo "======================================"

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed or not in PATH"
    exit 1
fi

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed or not in PATH"
    exit 1
fi

echo "✓ Maven and Java are available"

# Configuration
FAST_IMAGE_VERSION="v0.2.1"
NATIVE_DIR="src/main/resources/native"

echo ""
echo "📦 Setting up native libraries..."

# Create native resources directory
mkdir -p "$NATIVE_DIR"

# Download native libraries if not already present
if [ ! -f "fast-image-all-platforms.tar.gz" ]; then
    echo "⬇️  Downloading fast-image native libraries $FAST_IMAGE_VERSION..."
    curl -L -o fast-image-all-platforms.tar.gz \
        "https://github.com/lihongjie0209/fast-image/releases/download/$FAST_IMAGE_VERSION/fast-image-all-platforms.tar.gz"
else
    echo "✓ Native libraries archive already exists"
fi

# Extract if native directory is empty
if [ ! "$(ls -A $NATIVE_DIR 2>/dev/null)" ]; then
    echo "📂 Extracting native libraries..."
    tar -xzf fast-image-all-platforms.tar.gz
    
    # Copy native libraries
    find . -name "*.dll" -o -name "*.so" -o -name "*.dylib" | while read file; do
        echo "   Copying $file"
        cp "$file" "$NATIVE_DIR/"
    done
else
    echo "✓ Native libraries already extracted"
fi

# List available libraries
echo ""
echo "📋 Available native libraries:"
ls -la "$NATIVE_DIR/"

# Detect current platform
OS_NAME=$(uname -s | tr '[:upper:]' '[:lower:]')
OS_ARCH=$(uname -m)

echo ""
echo "🖥️  Current platform: $OS_NAME $OS_ARCH"

# Map to expected library name
case "$OS_NAME" in
    linux*)
        if [[ "$OS_ARCH" == *"aarch64"* || "$OS_ARCH" == *"arm64"* ]]; then
            EXPECTED_LIB="libfast_image-linux-aarch64.so"
        else
            EXPECTED_LIB="libfast_image-linux-x86_64.so"
        fi
        ;;
    darwin*)
        if [[ "$OS_ARCH" == *"arm64"* ]]; then
            EXPECTED_LIB="libfast_image-macos-aarch64.dylib"
        else
            EXPECTED_LIB="libfast_image-macos-x86_64.dylib"
        fi
        ;;
    mingw*|msys*|cygwin*)
        if [[ "$OS_ARCH" == *"aarch64"* || "$OS_ARCH" == *"arm64"* ]]; then
            EXPECTED_LIB="fast_image-windows-aarch64.dll"
        else
            EXPECTED_LIB="fast_image-windows-x86_64.dll"
        fi
        ;;
    *)
        echo "⚠️  Unknown platform: $OS_NAME"
        EXPECTED_LIB=""
        ;;
esac

if [ -n "$EXPECTED_LIB" ]; then
    if [ -f "$NATIVE_DIR/$EXPECTED_LIB" ]; then
        echo "✓ Platform-specific library found: $EXPECTED_LIB"
    else
        echo "⚠️  Platform-specific library missing: $EXPECTED_LIB"
        echo "   Available libraries:"
        ls -1 "$NATIVE_DIR/"
    fi
fi

echo ""
echo "🔧 Compiling project..."
mvn clean compile test-compile

echo ""
echo "🧪 Running tests..."
echo ""

# Function to run a test and capture result
run_test() {
    local test_name="$1"
    local test_class="$2"
    
    echo "📋 Running $test_name..."
    if mvn test -Dtest="$test_class" -DfailIfNoTests=false -Djava.awt.headless=true -q; then
        echo "✅ $test_name: PASSED"
        return 0
    else
        echo "❌ $test_name: FAILED"
        return 1
    fi
}

# Run individual test suites
TESTS_PASSED=0
TESTS_TOTAL=0

# FastImageUtils Tests
if run_test "FastImageUtils Tests" "FastImageUtilsTest"; then
    ((TESTS_PASSED++))
fi
((TESTS_TOTAL++))

echo ""

# Cross-Platform Tests
if run_test "Cross-Platform Tests" "CrossPlatformTest"; then
    ((TESTS_PASSED++))
fi
((TESTS_TOTAL++))

echo ""
echo "📊 Test Summary"
echo "==============="
echo "Total test suites: $TESTS_TOTAL"
echo "Passed: $TESTS_PASSED"
echo "Failed: $((TESTS_TOTAL - TESTS_PASSED))"

if [ $TESTS_PASSED -eq $TESTS_TOTAL ]; then
    echo "🎉 All tests passed!"
    exit 0
else
    echo "⚠️  Some tests failed. Check the output above for details."
    exit 1
fi
