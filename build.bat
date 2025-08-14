@echo off
REM Build script for Fast Image JNI Library on Windows
REM This script compiles the Rust code and sets up the Java environment

echo 🚀 Building Fast Image JNI Library for Windows...

REM Set up paths
set RUST_PROJECT_DIR=c:\Users\lihongjie\RustroverProjects\fast-image
set JAVA_PROJECT_DIR=d:\code\fast-image-java
set TARGET_DIR=%JAVA_PROJECT_DIR%\src\main\resources\native

REM Navigate to Rust project
cd /d "%RUST_PROJECT_DIR%"

echo 📦 Building Rust library for JNI...
cargo build --release

if %ERRORLEVEL% neq 0 (
    echo ❌ Rust build failed!
    pause
    exit /b 1
)

echo ✅ Rust build completed successfully!

REM Create native resources directory
if not exist "%TARGET_DIR%" mkdir "%TARGET_DIR%"

REM Find and copy the compiled library
if exist "target\release\fast_image.dll" (
    copy "target\release\fast_image.dll" "%TARGET_DIR%\"
    echo ✅ Copied fast_image.dll to Java resources
) else if exist "target\release\libfast_image.dll" (
    copy "target\release\libfast_image.dll" "%TARGET_DIR%\fast_image.dll"
    echo ✅ Copied libfast_image.dll to Java resources as fast_image.dll
) else (
    echo ❌ Could not find compiled library!
    echo Looking for files in target\release\:
    dir target\release\
    pause
    exit /b 1
)

REM Also copy to target\classes for testing
set CLASSES_DIR=%JAVA_PROJECT_DIR%\target\classes
if not exist "%CLASSES_DIR%" mkdir "%CLASSES_DIR%"
copy "%TARGET_DIR%\fast_image.dll" "%CLASSES_DIR%\"

echo 🔧 Library build and setup completed!
echo 📍 Library locations:
echo    - Java resources: %TARGET_DIR%\fast_image.dll
echo    - Test location: %CLASSES_DIR%\fast_image.dll

REM Navigate to Java project and compile
cd /d "%JAVA_PROJECT_DIR%"

echo ☕ Compiling Java code...
call mvn clean compile test-compile

if %ERRORLEVEL% equ 0 (
    echo ✅ Java compilation successful!
    echo 🎉 Build completed successfully!
    echo.
    echo 🧪 To test the library, run one of these commands:
    echo    mvn exec:java -Dexec.mainClass="cn.lihongjie.image.SimpleTest"
    echo    mvn exec:java -Dexec.mainClass="cn.lihongjie.image.SimpleTest" -Dexec.args="path\to\your\image.jpg"
) else (
    echo ❌ Java compilation failed!
    pause
    exit /b 1
)

pause
