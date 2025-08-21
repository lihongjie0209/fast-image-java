@echo off
echo =======================================
echo FastImage Performance Benchmark Runner
echo =======================================
echo.

cd /d "%~dp0"

echo [1/3] Compiling test classes...
call mvn test-compile -q
if ERRORLEVEL 1 (
    echo ERROR: Failed to compile test classes
    pause
    exit /b 1
)

echo [2/3] Running performance benchmarks using JUnit...
echo.
echo Note: These are performance comparison tests, not functional tests.
echo Running individual test methods:
echo.

echo Running JPEG compression performance tests...
call mvn test -Dtest=SimplePerformanceTest#testJpegCompressionPerformance -q

echo Running PNG compression performance tests...
call mvn test -Dtest=SimplePerformanceTest#testPngCompressionPerformance -q

echo Running image rotation performance tests...
call mvn test -Dtest=SimplePerformanceTest#testImageRotationPerformance -q

echo Running combined operations performance tests...
call mvn test -Dtest=SimplePerformanceTest#testCombinedOperationsPerformance -q

echo.
echo [3/3] Performance benchmarks completed!
echo.
echo You can also run all performance tests with:
echo   mvn test -Dtest=SimplePerformanceTest
echo.
echo For JMH benchmarks, use:
echo   mvn test -Dtest=ImageProcessingBenchmark (with external JMH runner)
echo.
pause
