@echo off
setlocal enabledelayedexpansion

:: Fast Image Java - Native Libraries Download Script (Windows)
:: 从Rust项目的GitHub Release下载最新的原生库

echo ========================================
echo   Fast Image Java - Native Libraries 
echo   Downloader for Windows
echo ========================================
echo.

set RUST_REPO=lihongjie0209/fast-image
set NATIVE_DIR=src\main\resources\native
set VERSION=latest

:: 解析命令行参数
if not "%1"=="" set VERSION=%1

echo Target Version: %VERSION%
echo.

:: 检查PowerShell是否可用
powershell -Command "Write-Host 'PowerShell is available'" >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ❌ PowerShell is required but not available.
    echo Please install PowerShell or use WSL with download-native-libs.sh
    pause
    exit /b 1
)

:: 创建目录
echo 📁 Creating native libraries directory...
if not exist "%NATIVE_DIR%" mkdir "%NATIVE_DIR%"

:: 使用PowerShell下载
echo ⬇️  Downloading native libraries...
echo.

powershell -ExecutionPolicy Bypass -Command "& {
    $rustRepo = '%RUST_REPO%'
    $version = '%VERSION%'
    $nativeDir = '%NATIVE_DIR%'
    
    # 获取最新版本
    if ($version -eq 'latest') {
        try {
            $releases = Invoke-RestMethod -Uri \"https://api.github.com/repos/$rustRepo/releases/latest\"
            $version = $releases.tag_name
            Write-Host \"Using version: $version\"
        } catch {
            Write-Host \"❌ Failed to fetch latest version: $($_.Exception.Message)\" -ForegroundColor Red
            exit 1
        }
    }
    
    # 定义所有平台的原生库
    $platforms = @(
        'fast_image-windows-x86_64.dll',
        'fast_image-windows-aarch64.dll',
        'libfast_image-linux-x86_64.so',
        'libfast_image-linux-aarch64.so',
        'libfast_image-macos-x86_64.dylib',
        'libfast_image-macos-aarch64.dylib'
    )
    
    $downloaded = 0
    $failed = 0
    
    foreach ($platform in $platforms) {
        Write-Host \"  📥 $platform ... \" -NoNewline
        
        $url = \"https://github.com/$rustRepo/releases/download/$version/$platform\"
        $output = \"$nativeDir\$platform\"
        
        try {
            Invoke-WebRequest -Uri $url -OutFile $output -ErrorAction Stop
            $size = (Get-Item $output).Length
            
            if ($size -gt 1000) {
                $sizeStr = switch ($size) {
                    {$_ -gt 1MB} { '{0:N1} MB' -f ($size / 1MB) }
                    {$_ -gt 1KB} { '{0:N1} KB' -f ($size / 1KB) }
                    default { '$size bytes' }
                }
                Write-Host \"✅ ($sizeStr)\" -ForegroundColor Green
                $downloaded++
            } else {
                Write-Host \"❌ (file too small)\" -ForegroundColor Red
                Remove-Item $output -ErrorAction SilentlyContinue
                $failed++
            }
        } catch {
            Write-Host \"❌ (download failed)\" -ForegroundColor Red
            $failed++
        }
    }
    
    Write-Host \"\"
    Write-Host \"📊 Download Summary:\"
    Write-Host \"  ✅ Successfully downloaded: $downloaded files\"
    Write-Host \"  ❌ Failed downloads: $failed files\"
    
    # 显示下载的文件
    if ($downloaded -gt 0) {
        Write-Host \"\"
        Write-Host \"📁 Downloaded files:\" -ForegroundColor Green
        Get-ChildItem $nativeDir -Include *.dll,*.so,*.dylib -Recurse | ForEach-Object {
            $sizeStr = switch ($_.Length) {
                {$_ -gt 1MB} { '{0:N1} MB' -f ($_.Length / 1MB) }
                {$_ -gt 1KB} { '{0:N1} KB' -f ($_.Length / 1KB) }
                default { '$($_.Length) bytes' }
            }
            Write-Host \"  $($_.Name) ($sizeStr)\"
        }
    }
    
    # 验证Windows库
    Write-Host \"\"
    Write-Host \"🔍 Verifying Windows compatibility...\" -ForegroundColor Blue
    
    if (Test-Path \"$nativeDir\fast_image-windows-x86_64.dll\") {
        Write-Host \"✅ Windows x64 library available\" -ForegroundColor Green
    } else {
        Write-Host \"⚠️  Windows x64 library missing\" -ForegroundColor Yellow
    }
    
    if (Test-Path \"$nativeDir\fast_image-windows-aarch64.dll\") {
        Write-Host \"✅ Windows ARM64 library available\" -ForegroundColor Green
    } else {
        Write-Host \"⚠️  Windows ARM64 library missing\" -ForegroundColor Yellow
    }
    
    if ($downloaded -eq 0) {
        Write-Host \"\"
        Write-Host \"❌ No libraries were downloaded successfully!\" -ForegroundColor Red
        Write-Host \"Please check:\"
        Write-Host \"  1. Internet connection\"
        Write-Host \"  2. GitHub repository: https://github.com/$rustRepo\"
        Write-Host \"  3. Release version: $version\"
        exit 1
    }
    
    Write-Host \"\"
    Write-Host \"🎉 Native libraries download completed!\" -ForegroundColor Green
    Write-Host \"\"
    Write-Host \"Next steps:\"
    Write-Host \"  1. Run tests: mvn test\"
    Write-Host \"  2. Run benchmark: mvn exec:java@benchmark\"
    Write-Host \"  3. Build JAR: mvn package\"
}"

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ Download failed. Please check the error messages above.
    pause
    exit /b 1
)

echo.
echo ========================================
echo Press any key to exit...
pause >nul
