@echo off
:start
echo ===============================================
echo           Fast Image Manual Tests
echo ===============================================
echo.
echo Select test type:
echo.
echo 1. Unit Tests (Fast - 5 seconds)
echo 2. All Manual Analysis Tests (Slow - 5-20 minutes)
echo 3. JPEG Compression Size Analysis
echo 4. Native Fast Performance Diagnosis  
echo 5. Compression Performance Comparison
echo 6. JPEG Encoder Analysis
echo 7. JMH Benchmark Tests
echo 8. Interactive Image Processing (NEW!)
echo 9. Exit
echo.
set /p choice="Enter your choice (1-9): "

if "%choice%"=="1" goto :unit_tests
if "%choice%"=="2" goto :all_manual
if "%choice%"=="3" goto :size_analysis
if "%choice%"=="4" goto :performance_diagnosis
if "%choice%"=="5" goto :performance_comparison
if "%choice%"=="6" goto :encoder_analysis
if "%choice%"=="7" goto :jmh_benchmark
if "%choice%"=="8" goto :interactive_processing
if "%choice%"=="9" goto :exit
goto :invalid

:unit_tests
echo.
echo Running Unit Tests...
mvn test
goto :end

:all_manual
echo.
echo Running All Manual Analysis Tests...
echo This may take 5-20 minutes depending on your system.
mvn test -P manual-tests
goto :end

:size_analysis
echo.
echo Running JPEG Compression Size Analysis...
mvn test -P manual-tests -Dtest=JpegCompressionSizeAnalysisTest
goto :end

:performance_diagnosis
echo.
echo Running Native Fast Performance Diagnosis...
mvn test -P manual-tests -Dtest=NativeFastPerformanceDiagnosisTest
goto :end

:performance_comparison
echo.
echo Running Compression Performance Comparison...
mvn test -P manual-tests -Dtest=CompressionPerformanceComparisonTest
goto :end

:encoder_analysis
echo.
echo Running JPEG Encoder Analysis...
mvn test -P manual-tests -Dtest=JpegEncoderAnalysisTest
goto :end

:jmh_benchmark
echo.
echo Running JMH Benchmark Tests...
mvn test -P manual-tests -Dtest=ImageProcessingBenchmark
goto :end

:interactive_processing
echo.
echo ===============================================
echo        Interactive Image Processing
echo ===============================================
echo.
echo This test allows you to process images in a folder:
echo - Compress with Standard and Fast methods
echo - Rotate by 90, 180, 270 degrees
echo - Output files with descriptive suffixes
echo.
echo Choose processing mode:
echo 1. Process custom folder (you input path)
echo 2. Process example images (automatic)
echo 3. Process single file
echo.
set /p proc_choice="Enter your choice (1-3): "

if "%proc_choice%"=="1" goto :custom_folder
if "%proc_choice%"=="2" goto :example_images
if "%proc_choice%"=="3" goto :single_file
goto :interactive_processing

:custom_folder
echo.
echo Processing custom folder...
mvn test -P manual-tests -Dtest=InteractiveImageProcessingTest#testInteractiveImageProcessing
goto :end

:example_images
echo.
echo Processing example images...
mvn test -P manual-tests -Dtest=InteractiveImageProcessingTest#testBatchProcessingWithExampleImages
goto :end

:single_file
echo.
echo Processing single file...
mvn test -P manual-tests -Dtest=InteractiveImageProcessingTest#testSingleFileProcessing
goto :end

:invalid
echo.
echo Invalid choice. Please select 1-9.
goto :start

:end
echo.
echo ===============================================
echo Test completed! Check the output above.
echo ===============================================
pause
goto :start

:exit
echo.
echo Goodbye!
pause
