# FastImage Performance Testing Guide

## Overview

This project includes comprehensive performance testing comparing the FastImage native library (Rust + JNI) with Java's built-in image processing capabilities.

## Test Framework Structure

### 1. JMH Benchmarks (ImageProcessingBenchmark.java)
- **Location**: `src/test/java/cn/lihongjie/image/performance/ImageProcessingBenchmark.java`
- **Purpose**: Professional JMH benchmarks for accurate performance measurement
- **Features**: 
  - Warmup iterations and measurement iterations
  - Statistical analysis
  - JVM optimization handling
- **Run Method**: Requires external JMH runner setup (no main method per requirements)

### 2. JUnit Performance Tests (SimplePerformanceTest.java)
- **Location**: `src/test/java/cn/lihongjie/image/performance/SimplePerformanceTest.java`
- **Purpose**: Simple performance comparison using JUnit test framework
- **Features**:
  - Easy to run with Maven
  - Clear console output
  - Manual timing measurement
- **Run Method**: `mvn test -Dtest=SimplePerformanceTest`

## Test Categories

### 1. JPEG Compression Performance
```bash
mvn test -Dtest=SimplePerformanceTest#testJpegCompressionPerformance
```
Compares:
- Native (mozjpeg) vs Java ImageIO
- Quality levels: Native 70% vs Java 70%
- Image sizes: 256x256, 512x512, 1024x1024

### 2. PNG Compression Performance ⭐ NEW
```bash
mvn test -Dtest=SimplePerformanceTest#testPngCompressionPerformance
```
Compares:
- Native (imagequant) vs Java ImageIO
- Native: Quality-based compression with parameters
- Java: Basic PNG compression (no quality control)
- Image sizes: 256x256, 512x512, 1024x1024

### 3. Image Rotation Performance
```bash
mvn test -Dtest=SimplePerformanceTest#testImageRotationPerformance
```
Compares:
- Native (Rust image crate) vs Java Graphics2D
- Rotation: 90 degrees clockwise
- Formats: Both JPEG and PNG
- Image sizes: 256x256, 512x512, 1024x1024

### 4. Combined Operations Performance
```bash
mvn test -Dtest=SimplePerformanceTest#testCombinedOperationsPerformance
```
Compares:
- Native: Rotate → Compress in single workflow
- Java: Rotate → Compress in separate operations
- Formats: Both JPEG and PNG ⭐ NEW
- Image sizes: 512x512 (medium)

## Running Tests

### Quick Test (All Performance Tests)
```bash
mvn test -Dtest=SimplePerformanceTest
```

### Individual Test Categories
```bash
# JPEG compression only
mvn test -Dtest=SimplePerformanceTest#testJpegCompressionPerformance

# PNG compression only  
mvn test -Dtest=SimplePerformanceTest#testPngCompressionPerformance

# Image rotation only
mvn test -Dtest=SimplePerformanceTest#testImageRotationPerformance

# Combined operations only
mvn test -Dtest=SimplePerformanceTest#testCombinedOperationsPerformance
```

### Batch Script (Windows)
```bash
run-performance-test.bat
```

### All Tests (Including Functional Tests)
```bash
mvn test
```

## Test Output Interpretation

### Performance Metrics
- **Execution Time**: Average milliseconds per operation (10 iterations after 3 warmup)
- **Speed Improvement**: Factor by which native is faster/slower than Java
- **Compression Ratio**: Output file size as percentage of original
- **File Size**: Absolute bytes for compressed/processed images

### Sample Output
```
=== JPEG COMPRESSION PERFORMANCE ===

Small JPEG (256x256):
  Native (mozjpeg):  4.14 ms avg, 2454 bytes (71.0% of original)
  Java (ImageIO):    5.75 ms avg, 3346 bytes (96.8% of original)
  Speed improvement: 1.39x faster with native

=== PNG COMPRESSION PERFORMANCE ===

Small PNG (256x256):
  Native (imagequant): X.XX ms avg, XXXX bytes (XX.X% of original)
  Java (ImageIO):      X.XX ms avg, XXXX bytes (XX.X% of original)
  Speed improvement:   X.XXx faster with native
```

## Expected Results

### JPEG Compression
- **Native Advantage**: Better compression quality (smaller files)
- **Java Advantage**: Faster on very large images
- **Trade-off**: Quality vs Speed

### PNG Compression ⭐ NEW
- **Native Advantage**: Quality-based compression with parameter control
- **Java Limitation**: Basic compression only (no quality parameter)
- **Expected**: Native should provide better compression ratios
- **Performance**: To be determined via testing

### Image Rotation
- **Native Advantage**: Significant speed improvement (2-20x faster)
- **Consistent**: Native faster across all sizes and formats

### Combined Operations
- **Native Advantage**: Workflow efficiency and quality
- **Balanced**: Similar speed with better output
- **Extended**: Now includes PNG workflows

## Key Updates

### PNG Compression Support
- ✅ Added PNG compression benchmarks in both test classes
- ✅ Native library uses imagequant for quality-based PNG compression
- ✅ Java uses basic PNG compression (ImageIO standard)
- ✅ Performance comparison now includes PNG workflows

### Test Framework Compliance
- ✅ Removed all main methods per requirements
- ✅ All performance testing uses JUnit framework
- ✅ Easy integration with Maven test lifecycle
- ✅ Individual test method execution supported

### Enhanced Coverage
- ✅ JPEG compression: Native (mozjpeg) vs Java (ImageIO)
- ✅ PNG compression: Native (imagequant) vs Java (ImageIO) ⭐ NEW  
- ✅ Image rotation: Native (image crate) vs Java (Graphics2D)
- ✅ Combined operations: Both JPEG and PNG workflows ⭐ NEW

## Troubleshooting

### Common Issues
1. **Native Library Not Found**: Ensure `fast_image.dll` is in `src/main/resources/native/`
2. **Compilation Errors**: Run `mvn clean compile test-compile`
3. **Memory Issues**: Increase JVM heap size: `export MAVEN_OPTS="-Xmx2g"`

### Test Dependencies
- Native library must be built and copied to resources
- JUnit 4.13.2
- JMH 1.37 (for advanced benchmarks)
- Test image generator utility

## Notes

- Tests use randomly generated images to avoid file I/O bias
- Performance results may vary based on system specifications
- Warmup iterations help eliminate JVM optimization effects
- Results are averaged over multiple iterations for accuracy
- **No main methods**: All performance testing uses JUnit test framework only
- PNG compression testing added to match actual library capabilities

---

*For detailed performance analysis, see PERFORMANCE_BENCHMARK_REPORT.md*
