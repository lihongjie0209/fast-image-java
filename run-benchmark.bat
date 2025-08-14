@echo off
echo ========================================
echo   FastImageUtils 性能测试工具
echo ========================================
echo.

set JAVA_OPTS=-Xmx1g -XX:+UseG1GC

:menu
echo 请选择测试模式:
echo.
echo [1] 快速性能对比 (推荐, 1分钟)
echo [2] 完整基准测试套件 (详细, 5分钟)
echo [3] 仅JDK ImageIO测试 (不需要FastImageUtils库)
echo [4] 编译项目
echo [5] 退出
echo.
set /p choice="请输入选择 (1-5): "

if "%choice%"=="1" goto quick_benchmark
if "%choice%"=="2" goto full_benchmark  
if "%choice%"=="3" goto jdk_only
if "%choice%"=="4" goto compile
if "%choice%"=="5" goto exit
echo 无效选择，请重新输入
goto menu

:quick_benchmark
echo.
echo 正在运行快速性能对比...
echo ========================================
mvn exec:java@benchmark %JAVA_OPTS%
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ 测试运行失败！请检查项目是否已编译。
    echo 提示：选择选项4先编译项目，或检查FastImageUtils库是否正确安装。
) else (
    echo.
    echo ✅ 快速性能对比测试完成！
)
goto end

:full_benchmark
echo.
echo 正在运行完整基准测试套件...
echo ========================================
mvn test -Dtest=ImageCompressionBenchmark %JAVA_OPTS%
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ 测试运行失败！请检查项目配置。
) else (
    echo.
    echo ✅ 完整基准测试套件运行完成！
)
goto end

:jdk_only
echo.
echo 正在运行JDK ImageIO独立测试...
echo ========================================
echo 注意: 此模式不使用FastImageUtils库，仅测试JDK性能
mvn exec:java@benchmark %JAVA_OPTS%
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ 测试运行失败！
) else (
    echo.
    echo ✅ JDK ImageIO测试完成！
)
goto end

:compile
echo.
echo 正在编译项目...
echo ========================================
mvn clean compile test-compile
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ 编译失败！请检查代码和依赖。
) else (
    echo.
    echo ✅ 项目编译成功！
    echo 现在可以运行性能测试了。
)
goto end

:end
echo.
echo ========================================
echo 按任意键返回菜单，或按Ctrl+C退出...
pause >nul
echo.
goto menu

:exit
echo.
echo 感谢使用FastImageUtils性能测试工具！
echo ========================================
pause
