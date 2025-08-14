#!/bin/bash

# Build script for Fast Image JNI Library
# This script compiles the Rust code into a shared library for JNI

echo "🚀 Building Fast Image JNI Library..."

# Set up variables
RUST_PROJECT_DIR="c:/Users/lihongjie/RustroverProjects/fast-image"
JAVA_PROJECT_DIR="d:/code/fast-image-java"
TARGET_DIR="$JAVA_PROJECT_DIR/src/main/resources/native"

# Navigate to Rust project
cd "$RUST_PROJECT_DIR"

echo "📦 Building Rust library for JNI..."

# Build the Rust library in release mode
cargo build --release

# Check if build succeeded
if [ $? -ne 0 ]; then
    echo "❌ Rust build failed!"
    exit 1
fi

echo "✅ Rust build completed successfully!"

# Create native resources directory
mkdir -p "$TARGET_DIR"

# Copy the compiled library to Java resources
# On Windows, the library will be named fast_image.dll
if [ -f "target/release/fast_image.dll" ]; then
    cp "target/release/fast_image.dll" "$TARGET_DIR/"
    echo "✅ Copied fast_image.dll to Java resources"
elif [ -f "target/release/libfast_image.dll" ]; then
    cp "target/release/libfast_image.dll" "$TARGET_DIR/fast_image.dll"
    echo "✅ Copied libfast_image.dll to Java resources as fast_image.dll"
else
    echo "❌ Could not find compiled library!"
    echo "Looking for files in target/release/:"
    ls -la target/release/
    exit 1
fi

# Copy to system library path for testing
SYSTEM_LIB_DIR="$JAVA_PROJECT_DIR/target/classes"
mkdir -p "$SYSTEM_LIB_DIR"
cp "$TARGET_DIR/fast_image.dll" "$SYSTEM_LIB_DIR/"

echo "🔧 Library build and setup completed!"
echo "📍 Library locations:"
echo "   - Java resources: $TARGET_DIR/fast_image.dll"
echo "   - Test location: $SYSTEM_LIB_DIR/fast_image.dll"

# Navigate to Java project and compile
cd "$JAVA_PROJECT_DIR"

echo "☕ Compiling Java code..."
mvn clean compile test-compile

if [ $? -eq 0 ]; then
    echo "✅ Java compilation successful!"
    echo "🎉 Build completed successfully!"
    echo ""
    echo "To test the library, run:"
    echo "  cd $JAVA_PROJECT_DIR"
    echo "  mvn exec:java -Dexec.mainClass=\"cn.lihongjie.image.CompressionTest\""
else
    echo "❌ Java compilation failed!"
    exit 1
fi
