# FastImageUtils - 高性能JPEG压缩库 & 性能基准测试

> 🚀 基于Rust的高性能图像压缩JNI库，包含完整的性能对比测试框架

[![Java](https://img.shields.io/badge/Java-8%2B-orange.svg)](https://openjdk.java.net/)
[![Maven](https://img.shields.io/badge/Maven-3.6%2B-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![CI](https://github.com/lihongjie0209/fast-image-java/actions/workflows/ci.yml/badge.svg)](https://github.com/lihongjie0209/fast-image-java/actions/workflows/ci.yml)
[![Release](https://github.com/lihongjie0209/fast-image-java/actions/workflows/build-and-release.yml/badge.svg)](https://github.com/lihongjie0209/fast-image-java/actions/workflows/build-and-release.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=lihongjie0209_fast-image-java&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=lihongjie0209_fast-image-java)

---

## 📋 **项目概述**

本项目提供了一个完整的JPEG压缩解决方案，包括：

1. **FastImageUtils** - 基于Rust的高性能JNI压缩库
2. **JDK ImageIO对比** - 与Java标准库的全面性能对比
3. **基准测试框架** - 自动化性能测试和分析工具
4. **决策指导** - 详细的方案选择建议

## ⚡ **快速开始**

### 方式1: 直接使用发布版本
```bash
# 从GitHub Releases下载最新的JAR文件
# 包含所有平台的原生库，开箱即用
wget https://github.com/lihongjie0209/fast-image-java/releases/latest/download/fast-image-java-1.0.0.jar
```

### 方式2: 本地构建
```bash
# 克隆项目
git clone https://github.com/lihongjie0209/fast-image-java.git
cd fast-image-java

# 下载最新的原生库
./download-native-libs.sh  # Linux/macOS
# 或
download-native-libs.bat   # Windows

# 编译和测试
mvn clean compile test-compile
mvn exec:java@benchmark
```

### 方式3: Maven依赖（即将支持）
```xml
<dependency>
    <groupId>cn.lihongjie</groupId>
    <artifactId>fast-image-java</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 在代码中使用
```java
import cn.lihongjie.image.FastImageUtils;

// 基本压缩
byte[] imageData = Files.readAllBytes(Paths.get("image.jpg"));
byte[] compressed = FastImageUtils.compress(imageData, 70);

// 便捷方法
byte[] high = FastImageUtils.compressHigh(imageData);    // 90% 质量
byte[] medium = FastImageUtils.compressMedium(imageData); // 60% 质量
byte[] low = FastImageUtils.compressLow(imageData);      // 30% 质量
```

## 📊 **JPEG 压缩性能测试报告**

### 🎯 **压缩大小对比 (重点)**

基于真实复杂图片（包含文本、线条、渐变等）的测试结果：

| 质量级别 | Native Standard | Native Fast | Java Native | 最佳选择 |
|----------|-----------------|-------------|-------------|----------|
| 30% | 208.32 KB | 258.82 KB | 258.29 KB | ✅ Standard (最小) |
| 50% | 302.30 KB | 386.82 KB | 384.00 KB | ✅ Standard (最小) |
| 70% | 419.68 KB | 499.42 KB | 499.87 KB | ✅ Standard (最小) |
| 90% | 623.07 KB | 1.07 MB | 768.13 KB | ✅ Standard (最小) |

> 📝 测试图片：540KB 复杂内容图片（含随机文本、几何图形、渐变背景）

### ⚡ **性能 vs 大小权衡分析**

在质量级别 70% 的综合对比：

| 压缩方法 | 时间 | 大小 | 速度评级 | 大小评级 | 推荐场景 |
|----------|------|------|----------|----------|----------|
| **Native Standard** | 340.6 ms | 419.68 KB | ⭐⭐⭐ (慢) | ⭐⭐⭐⭐⭐ (最小) | 💾 存储优化 |
| **Native Fast** | 34.3 ms | 499.42 KB | ⭐⭐⭐⭐⭐ (最快) | ⭐⭐⭐⭐ (较小) | 🚀 实时应用 |
| **Java Native** | 81.8 ms | 499.87 KB | ⭐⭐⭐⭐ (较快) | ⭐⭐⭐⭐ (较小) | ⚖️ 通用场景 |

### 📈 **压缩效率详细分析**

**压缩率对比 (相对于原始图片 540KB)**：

- **Native Standard**: 22.2% 压缩率 → 节省空间 **120KB** (22%)
- **Native Fast**: 7.4% 压缩率 → 节省空间 **40KB** (7%)  
- **Java Native**: 7.3% 压缩率 → 节省空间 **40KB** (7%)

**关键发现**：
- 🏆 **Native Standard** 在所有质量级别都产生最小文件
- ⚡ **Native Fast** 速度快 **10x**，文件仅大 **19%**
- 🔄 **Java Native** 与 Native Fast 大小相近，但速度慢 2.4x

### 🎯 **使用建议矩阵**

| 应用场景 | 推荐方案 | 理由 |
|----------|----------|------|
| **Web 图片服务** | Native Fast | 响应速度关键，19% 大小增长可接受 |
| **移动 App 上传** | Native Fast | 快速处理用户图片，提升体验 |
| **CDN/存储优化** | Native Standard | 长期存储成本，22% 空间节省显著 |
| **缩略图生成** | Java Native | 平衡性能，无需额外依赖 |
| **实时图片 API** | Native Fast | 低延迟要求，34ms vs 341ms |
| **批量归档处理** | Native Standard | 存储成本优先，可接受处理时间 |

## 📊 **原有性能优势一览**

| 指标 | FastImageUtils | JDK ImageIO | 优势 |
|------|---------------|-------------|------|
| **压缩率** | 81.2% (质量30%) | 70.1% (质量30%) | **+11.1%** |
| **文件大小** | 5.6KB | 12.0KB | **小53%** |
| **内存效率** | 12MB (100次压缩) | 18MB (100次压缩) | **节省33%** |
| **一致性** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | 跨平台一致 |

JDK ImageIO的优势：
- **处理速度**: 小文件快75%，大文件快69%
- **部署简单**: 无需原生库，纯Java
- **兼容性**: Java标准库，最佳兼容性

## 📁 **项目结构**

```
fast-image-java/
├── src/main/java/
│   └── cn/lihongjie/image/
│       └── FastImageUtils.java          # 核心压缩库
├── src/test/java/
│   └── cn/lihongjie/image/
│       ├── ImageCompressionBenchmark.java  # 完整基准测试
│       └── QuickBenchmark.java             # 快速性能对比
├── run-benchmark.bat                    # Windows快速测试工具
├── JPEG-COMPRESSION-COMPARISON.md      # 详细对比分析报告
├── DECISION-GUIDE.md                   # 快速决策指南
├── BENCHMARK.md                        # 基准测试详细说明
└── QUICK-GUIDE.md                      # 快速使用指南
```

## 🎯 **使用场景建议**

### 选择 FastImageUtils 🚀
- ✅ **Web应用**: 减少页面加载时间，降低CDN成本
- ✅ **移动应用**: 节省用户流量，提升用户体验
- ✅ **大规模存储**: 显著降低存储成本(10-16%)
- ✅ **内容管理**: CMS系统的图片优化

### 选择 JDK ImageIO ⚡
- ✅ **实时处理**: 需要极低延迟的图片API
- ✅ **简单项目**: 快速开发，零配置
- ✅ **小文件处理**: 缩略图、头像等场景
- ✅ **高安全环境**: 不允许第三方原生库

## 🧪 **详细测试报告**

### 🎯 **测试类型说明**

本项目包含两种类型的测试：

#### 📋 **单元测试 (自动运行)**
```bash
# 正常开发中运行的测试
mvn test
```
- **位置**: `src/test/java/cn/lihongjie/image/unit/`
- **用途**: 验证功能正确性，CI/CD 自动运行
- **包含**: CompressionUnitTest, RotationUnitTest, CrossPlatformUnitTest, FastJpegCompressionTest

#### 🔬 **手动分析测试 (按需运行)**
```bash
# 分析和性能测试，需要手动运行
mvn test -P manual-tests
mvn test -P manual-tests -Dtest=JpegCompressionSizeAnalysisTest
```
- **位置**: `src/test/manual/java/cn/lihongjie/image/analysis/`
- **用途**: 性能分析、数据生成、问题诊断
- **包含**: 压缩大小分析、性能诊断、编码器分析、基准测试等

详细说明请查看：[手动测试指南](src/test/manual/README.md)

### 💾 **文件大小对比 (不同图片尺寸)**

**小图片 (~127KB 原始大小)**：
```
质量   | Native Standard | Native Fast | Java Native | 节省空间
30%    |     50.18 KB   |   62.07 KB  |   61.92 KB  | 60-51%
50%    |     72.27 KB   |   91.33 KB  |   90.73 KB  | 43-28%
70%    |     99.57 KB   |  117.55 KB  |  117.51 KB  | 21-7%
90%    |    146.55 KB   |  259.63 KB  |  179.84 KB  | -15~+105%
```

**中图片 (~540KB 原始大小)**：
```
质量   | Native Standard | Native Fast | Java Native | 节省空间
30%    |    208.32 KB   |  258.82 KB  |  258.29 KB  | 61-52%
50%    |    302.30 KB   |  386.82 KB  |  384.00 KB  | 44-29%
70%    |    419.68 KB   |  499.42 KB  |  499.87 KB  | 22-7%
90%    |    623.07 KB   |    1.07 MB  |  768.13 KB  | -15~+98%
```

**大图片 (~1.4MB 原始大小)**：
```
质量   | Native Standard | Native Fast | Java Native | 节省空间
30%    |    498.63 KB   |  676.02 KB  |  678.02 KB  | 64-52%
50%    |    764.42 KB   |    1.04 MB  |    1.03 MB  | 45-26%
70%    |      1.09 MB   |    1.33 MB  |    1.34 MB  | 22-5%
90%    |      1.65 MB   |    2.82 MB  |    2.01 MB  | -18~+101%
```

### ⚡ **速度 vs 大小权衡**

**最优选择指南**：

| 场景需求 | 质量设置 | 推荐方案 | 性能特点 |
|----------|----------|----------|----------|
| **最小文件** | 30-70% | Native Standard | 📦 最小 + 🐌 最慢 |
| **最快速度** | 50-70% | Native Fast | ⚡ 最快 + 📦 适中 |
| **平衡选择** | 50-90% | Java Native | ⚖️ 平衡 + 📦 适中 |

### 🔍 **深度分析结论**

**Native Standard (mozjpeg) 优势**：
- ✅ **文件最小**: 比其他方案小 15-25%
- ✅ **一致性好**: 各种图片类型都能获得最佳压缩
- ✅ **质量稳定**: 高质量设置下优势更明显
- ❌ **速度较慢**: 比 Fast 慢 6-10 倍

**Native Fast (专用编码器) 优势**：
- ✅ **速度最快**: 比标准方案快 6-10 倍
- ✅ **大小合理**: 仅比最佳大小增加 15-25%
- ✅ **适合实时**: 低延迟应用的理想选择
- ❌ **高质量下**: 90% 质量时文件增大较多

**Java Native (ImageIO) 优势**：
- ✅ **无依赖**: 纯 Java，部署简单
- ✅ **速度适中**: 比 Standard 快 3-4 倍
- ✅ **稳定可靠**: Java 标准库，兼容性最佳
- ❌ **不是最优**: 速度和大小都不是最佳

**查看完整报告**: [JPEG-COMPRESSION-COMPARISON.md](JPEG-COMPRESSION-COMPARISON.md)

## 🏆 **完整性能基准测试报告 (速度对比)**

### ⚡ **综合性能测试结果**

基于 JMH (Java Microbenchmark Harness) 的专业基准测试，测试环境：
- **测试方法**: 3次迭代取平均值
- **测试指标**: 平均执行时间 (ms/op) 
- **测试图片**: 小/中/大三种尺寸真实图片
- **测试场景**: 纯压缩、纯旋转、组合操作

### 📊 **JPEG 压缩速度对比 (重点)**

| 图片大小 | Java Standard | Native Standard | Native Fast | 性能提升 |
|----------|--------------|----------------|-------------|----------|
| **小图片** | 4.13 ms | 4.23 ms | **0.60 ms** | 🚀 **快 6.9x** |
| **中图片** | 7.52 ms | 12.41 ms | **2.44 ms** | 🚀 **快 3.1x** |
| **大图片** | 20.08 ms | 40.05 ms | **9.22 ms** | 🚀 **快 2.2x** |

> 💡 **关键发现**: Native Fast 在所有尺寸下都是最快的JPEG压缩方案！

### 🔄 **图片旋转速度对比**

#### **JPEG 图片旋转**
| 图片大小 | Java | Native | 性能提升 |
|----------|------|--------|----------|
| **小图片** | 4.47 ms | **1.26 ms** | 🚀 **快 3.5x** |
| **中图片** | 10.75 ms | **5.06 ms** | 🚀 **快 2.1x** |
| **大图片** | 30.22 ms | **19.68 ms** | 🚀 **快 1.5x** |

#### **PNG 图片旋转**
| 图片大小 | Java | Native | 性能提升 |
|----------|------|--------|----------|
| **小图片** | 5.56 ms | **0.35 ms** | 🚀 **快 15.9x** |
| **中图片** | 13.69 ms | **1.70 ms** | 🚀 **快 8.1x** |
| **大图片** | 41.69 ms | **7.50 ms** | 🚀 **快 5.6x** |

> 💡 **关键发现**: PNG旋转中，原生方案的优势极其明显，最高快 **15.9 倍**！

### 🔧 **组合操作性能 (旋转 + 压缩)**

| 操作类型 | Java | Native Standard | Native Fast | 最佳选择 |
|----------|------|----------------|-------------|----------|
| **JPEG旋转+压缩** | 20.38 ms | 18.17 ms | **7.55 ms** | ✅ Native Fast |
| **PNG旋转+压缩** | N/A | **15.75 ms** | N/A | ✅ Native Standard |

> 💡 **实际应用**: 在Web应用中处理用户上传图片时，Native Fast能将处理时间从20ms降低到7.5ms

### 📈 **详细性能分析表**

#### **原始基准测试数据**

```
测试项目                                          执行时间(ms)    标准差      性能等级
========================================================================
JPEG压缩性能:
  Java小图片压缩                                   4.13 ± 2.75    ⭐⭐⭐
  Java中图片压缩                                   7.52 ± 1.37    ⭐⭐⭐
  Java大图片压缩                                  20.08 ± 4.32    ⭐⭐
  
  Native标准小图片压缩                             4.23 ± 0.82    ⭐⭐⭐
  Native标准中图片压缩                            12.41 ± 0.86    ⭐⭐
  Native标准大图片压缩                            40.05 ± 16.78   ⭐
  
  Native快速小图片压缩                             0.60 ± 0.16    ⭐⭐⭐⭐⭐
  Native快速中图片压缩                             2.44 ± 4.50    ⭐⭐⭐⭐⭐  
  Native快速大图片压缩                             9.22 ± 1.33    ⭐⭐⭐⭐⭐

旋转操作性能:
  Java小JPEG旋转                                   4.47 ± 1.56    ⭐⭐⭐
  Java中JPEG旋转                                  10.75 ± 3.00    ⭐⭐⭐
  Java大JPEG旋转                                  30.22 ± 6.22    ⭐⭐
  Java小PNG旋转                                    5.56 ± 3.29    ⭐⭐⭐
  Java中PNG旋转                                   13.69 ± 5.55    ⭐⭐
  Java大PNG旋转                                   41.69 ± 2.34    ⭐
  
  Native小JPEG旋转                                 1.26 ± 0.28    ⭐⭐⭐⭐⭐
  Native中JPEG旋转                                 5.06 ± 1.09    ⭐⭐⭐⭐
  Native大JPEG旋转                                19.68 ± 3.58    ⭐⭐⭐
  Native小PNG旋转                                  0.35 ± 0.01    ⭐⭐⭐⭐⭐
  Native中PNG旋转                                  1.70 ± 7.33    ⭐⭐⭐⭐⭐
  Native大PNG旋转                                  7.50 ± 2.27    ⭐⭐⭐⭐

组合操作性能:
  Java旋转+JPEG压缩                               20.38 ± 25.47   ⭐⭐
  Native标准旋转+JPEG压缩                         18.17 ± 2.37    ⭐⭐⭐
  Native快速旋转+JPEG压缩                          7.55 ± 5.06    ⭐⭐⭐⭐⭐
  Native标准旋转+PNG压缩                          15.75 ± 16.82   ⭐⭐⭐

其他操作:
  Native小PNG压缩                                  3.36 ± 5.92    ⭐⭐⭐⭐
```

### 🎯 **速度优化建议**

#### **基于场景的最优选择**

| 使用场景 | 推荐方案 | 性能优势 | 适用原因 |
|----------|----------|----------|----------|
| **实时图片API** | Native Fast JPEG | 快6.9倍 | 用户等待时间短 |
| **批量图片处理** | Native PNG旋转 | 快15.9倍 | 大幅提升吞吐量 |
| **Web图片上传** | Native Fast组合 | 快2.7倍 | 快速预览生成 |
| **移动App缩略图** | Native Fast小图 | 快6.9倍 | 流畅用户体验 |
| **内容管理系统** | Native Fast中图 | 快3.1倍 | 编辑器快速响应 |

#### **性能优化策略**

```java
public class OptimizedImageProcessor {
    
    // 策略1: 根据图片大小选择最优算法
    public byte[] smartCompress(byte[] imageData, int width, int height) {
        if (width * height < 500_000) {  // 小图片
            return FastImageUtils.compressJpegFast(imageData, 70);  // 0.6ms
        } else if (width * height < 2_000_000) {  // 中图片  
            return FastImageUtils.compressJpegFast(imageData, 65);  // 2.4ms
        } else {  // 大图片
            return FastImageUtils.compressJpegFast(imageData, 60);  // 9.2ms
        }
    }
    
    // 策略2: 针对PNG旋转优化
    public byte[] rotatePNG(byte[] pngData, int degrees) {
        // PNG旋转使用Native方案，快15.9倍
        return FastImageUtils.rotatePNG(pngData, degrees);
    }
    
    // 策略3: 组合操作优化  
    public byte[] rotateAndCompress(byte[] imageData, int degrees, int quality) {
        // 组合操作比分步执行快2.7倍
        return FastImageUtils.rotateAndCompressJpeg(imageData, degrees, quality);
    }
}
```

#### **实际应用性能提升计算**

**Web应用图片服务示例**：
```
假设每日处理10万张图片:
- Java方案平均: 7.52ms × 100,000 = 12.5小时处理时间
- Native Fast: 2.44ms × 100,000 = 4.1小时处理时间  
- 节省时间: 8.4小时/天 (67%处理时间减少)
- 成本节省: 服务器资源减少67%使用
```

**移动App用户体验提升**：
```
用户上传图片处理时间:
- 之前: 20.38ms (Java旋转+压缩)
- 现在: 7.55ms (Native Fast)
- 用户感知: 响应速度提升2.7倍，接近瞬时处理
```

### 🏁 **性能测试结论**

#### **🥇 最快方案排名**

1. **Native Fast JPEG压缩**: 比Java快 **2.2-6.9倍**
2. **Native PNG旋转**: 比Java快 **5.6-15.9倍**  
3. **Native组合操作**: 比Java快 **2.7倍**
4. **Native JPEG旋转**: 比Java快 **1.5-3.5倍**

#### **💰 投入产出比分析**

| 投入 | 产出 | ROI |
|------|------|-----|
| **增加Native库依赖** | 处理速度提升2-16倍 | ⭐⭐⭐⭐⭐ |
| **学习JNI调用** | 服务器成本降低60% | ⭐⭐⭐⭐⭐ |
| **部署复杂度增加** | 用户体验大幅提升 | ⭐⭐⭐⭐ |

#### **🎯 最终建议**

- **追求极致性能**: 全面使用Native方案，特别是PNG旋转和Fast JPEG
- **平衡型应用**: JPEG使用Native Fast，其他保持Java
- **简单项目**: 保持Java方案，但考虑后续升级

**基准测试命令**：
```bash
# 运行完整性能测试
mvn test -Dtest=ImageProcessingBenchmark

# 查看详细测试报告  
cat target/surefire-reports/TEST-*.xml
```

## 🔧 **运行测试**

### 方式1: 快速单元测试 ⚡
```bash
# 运行核心功能单元测试（推荐日常开发）
mvn test
```

### 方式2: 手动分析测试 🔬
```bash
# 运行所有分析和性能测试
mvn test -P manual-tests

# 运行特定分析测试
mvn test -P manual-tests -Dtest=JpegCompressionSizeAnalysisTest    # 压缩大小分析
mvn test -P manual-tests -Dtest=NativeFastPerformanceDiagnosisTest # 性能诊断
mvn test -P manual-tests -Dtest=ImageProcessingBenchmark           # JMH基准测试
```

### 方式3: 快速性能对比
```bash
# 自动下载原生库并运行基准测试
mvn exec:java@benchmark
```

### 方式4: 图形界面 (Windows)
```cmd
# 下载原生库
download-native-libs.bat

# 运行测试菜单
run-benchmark.bat
```

### 方式5: 手动下载原生库
```bash
# 下载特定版本的原生库
./download-native-libs.sh v0.2.1

# 或下载最新版本
./download-native-libs.sh latest
```

### 🎯 **测试使用指南**

| 使用场景 | 推荐命令 | 执行时间 | 用途 |
|----------|----------|----------|------|
| **日常开发** | `mvn test` | ~5秒 | 验证功能正确性 |
| **性能分析** | `mvn test -P manual-tests -Dtest=分析类名` | 1-20分钟 | 生成性能报告 |
| **问题诊断** | `mvn test -P manual-tests -Dtest=诊断类名` | 2-10分钟 | 识别性能瓶颈 |
| **基准测试** | `mvn exec:java@benchmark` | 2-5分钟 | 快速性能对比 |
| **CI/CD** | `mvn test` | ~5秒 | 自动化测试 |

## 💡 **最佳实践**

### FastImageUtils优化配置
```java
public class ImageProcessor {
    // 预热JNI库，提升首次调用性能
    static {
        byte[] dummy = createTestImage();
        FastImageUtils.compress(dummy, 70);
    }
    
    // 不同场景的质量推荐
    public static final int WEB_QUALITY = 60;      // Web图片
    public static final int MOBILE_QUALITY = 40;   // 移动端
    public static final int ARCHIVE_QUALITY = 85;  // 存档质量
    
    public byte[] processForWeb(byte[] imageData) {
        return FastImageUtils.compress(imageData, WEB_QUALITY);
    }
}
```

### 批量处理优化
```java
// 并行处理大量图片
public List<byte[]> batchCompress(List<byte[]> images) {
    return images.parallelStream()
                 .map(img -> FastImageUtils.compress(img, 60))
                 .collect(Collectors.toList());
}
```

## 📚 **文档索引**

| 文档 | 用途 | 阅读时间 |
|------|-----|----------|
| [DECISION-GUIDE.md](DECISION-GUIDE.md) | 5分钟快速选择方案 | 5分钟 |
| [QUICK-GUIDE.md](QUICK-GUIDE.md) | 快速上手指南 | 10分钟 |
| [JPEG-COMPRESSION-COMPARISON.md](JPEG-COMPRESSION-COMPARISON.md) | 详细对比分析 | 30分钟 |
| [BENCHMARK.md](BENCHMARK.md) | 基准测试详细说明 | 20分钟 |

## 🚨 **故障排除**

### FastImageUtils库加载失败
```
Error: Failed to load Fast Image native library
```
**解决方案**:
1. 确保 `src/main/resources/native/` 目录下有对应平台的库文件
2. 检查文件名格式: `fast_image-windows-x86_64.dll`
3. 重新编译: `mvn clean compile test-compile`

### 测试运行失败
```
❌ 测试运行失败！
```
**常见原因**:
- Java版本 < 8: 升级到Java 8+
- Maven版本过低: 升级到Maven 3.6+
- 内存不足: 增加JVM参数 `-Xmx1g`

## 🤝 **贡献指南**

欢迎提交Issue和Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交改动 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 **许可证**

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🙏 **致谢**

- [Rust Image库](https://github.com/image-rs/image) - 提供高性能的图像处理算法
- [JNI](https://docs.oracle.com/javase/8/docs/technotes/guides/jni/) - Java本地接口支持
- 所有贡献者和测试用户

---

## 🎯 **快速决策指南**

### 📊 **基于压缩大小的选择**

**如果你主要关心文件大小**：

```java
// 1. 存储成本敏感 → 选择 Native Standard  
byte[] minimal = FastImageUtils.compress(imageData, 70);  // 最小文件

// 2. 需要平衡速度 → 选择 Native Fast
byte[] fast = FastImageUtils.compressJpegFast(imageData, 70);  // 快速且文件适中

// 3. 简单部署优先 → 选择 Java Native
byte[] standard = compressWithJavaImageIO(imageData, 0.7f);  // 无额外依赖
```

**压缩大小决策树**：

```
你的应用场景是什么？
├─ 📦 存储成本关键 (如CDN、云存储)
│   └─ ✅ 选择 Native Standard (节省 20-25% 空间)
├─ ⚡ 响应速度关键 (如实时API、用户上传)  
│   └─ ✅ 选择 Native Fast (速度快10x，大小仅增20%)
└─ 🛠️ 部署简单优先 (如企业内部、原型开发)
    └─ ✅ 选择 Java Native (纯Java，无依赖)
```

### 💡 **实际应用示例**

**Web 图片服务优化**：
```java
public class WebImageService {
    // 根据图片用途选择不同压缩策略
    public byte[] processImage(byte[] imageData, ImageUsage usage) {
        switch (usage) {
            case THUMBNAIL:
                return FastImageUtils.compressJpegFast(imageData, 60);  // 快速缩略图
            case GALLERY:  
                return FastImageUtils.compress(imageData, 75);          // 画廊展示
            case ARCHIVE:
                return FastImageUtils.compress(imageData, 85);          // 长期存储
            default:
                return FastImageUtils.compressJpegFast(imageData, 70);  // 默认快速
        }
    }
}
```

**💡 不知道选哪个？**

运行一次基准测试：
```bash
mvn exec:java@benchmark
```

根据你的具体需求和测试结果，选择最适合的方案！

**📞 需要帮助？**

- 📧 Email: your-email@example.com
- 🐛 Issues: [GitHub Issues](https://github.com/your-username/fast-image-java/issues)
- 💬 Discussions: [GitHub Discussions](https://github.com/your-username/fast-image-java/discussions)

---

*让图片压缩更高效！🚀*
