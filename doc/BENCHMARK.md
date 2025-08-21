# FastImageUtils 性能测试指南

这个项目包含了用于比较 FastImageUtils（基于Rust的JNI库）和 JDK 自带 ImageIO 的压缩性能和效率的基准测试。

## 测试内容

### 🚀 性能对比维度
- **压缩速度**：比较两种方法的执行时间
- **压缩比率**：比较不同质量设置下的文件大小
- **内存使用**：监控内存消耗情况
- **多尺寸测试**：小图片 (500x300)、中等图片 (1920x1080)、大图片 (3840x2160)

### 📊 测试质量级别
- **30%**：高压缩率，低质量
- **50%**：中等压缩率和质量  
- **70%**：平衡的压缩率和质量
- **90%**：低压缩率，高质量

## 快速开始

### 方式1：运行快速基准测试（推荐）

```bash
# 编译项目
mvn clean compile test-compile

# 运行快速性能对比（包含表格化输出）
mvn exec:java -Dexec.mainClass="cn.lihongjie.image.QuickBenchmark" -Dexec.classpathScope="test"
```

### 方式2：运行完整的JUnit基准测试

```bash
# 运行所有基准测试
mvn test

# 或者运行特定的测试类
mvn test -Dtest=ImageCompressionBenchmark

# 运行特定的测试方法
mvn test -Dtest=ImageCompressionBenchmark#benchmarkMediumImages
```

### 方式3：在IDE中运行

1. 导入项目到IDE（IntelliJ IDEA/Eclipse）
2. 直接运行 `QuickBenchmark.main()` 方法获得快速结果
3. 或者运行 `ImageCompressionBenchmark` 中的JUnit测试获得详细分析

## 输出示例

### 快速基准测试输出
```
=== FastImageUtils vs JDK ImageIO 快速性能对比 ===

1. 检查 FastImageUtils 库状态:
Platform: Windows 11 (windows-x86_64)
Architecture: amd64
Java: 1.8.0_311
Native Library: fast_image-windows-x86_64.dll
Initialized: true
库可用性: ✓ 可用

2. 创建测试图片...
测试图片大小: 856.2KB

3. 性能对比测试结果:
质量  | FastImageUtils        | JDK ImageIO          | 性能提升
------|----------------------|----------------------|----------
 30%  |   45 ms (234.1KB)    |   89 ms (245.3KB)   | 1.98x 更快
 50%  |   52 ms (456.7KB)    |   94 ms (467.2KB)   | 1.81x 更快
 70%  |   58 ms (623.4KB)    |  102 ms (635.1KB)   | 1.76x 更快
 90%  |   71 ms (789.5KB)    |  115 ms (798.2KB)   | 1.62x 更快

4. 压缩率对比 (基于1080p测试图片):
原始大小: 3.2MB

质量  | FastImageUtils 压缩率 | JDK ImageIO 压缩率   | 压缩率差异
------|----------------------|----------------------|----------
 30%  |          92.3%       |          91.8%       | +0.5%
 50%  |          85.7%       |          85.2%       | +0.5%
 70%  |          76.4%       |          75.9%       | +0.5%
 90%  |          51.2%       |          50.8%       | +0.4%
```

### 完整基准测试输出
```
=== 图像压缩性能基准测试 ===
FastImageUtils vs JDK ImageIO

测试图片大小：
小图片 (500x300): 456.8KB
中图片 (1920x1080): 2.3MB
大图片 (3840x2160): 8.7MB

=== 小图片性能测试 (500x300) ===
--- 质量: 30% ---
FastImageUtils: 23 ms (压缩后: 123.4KB, 压缩比: 73.0%)
JDK ImageIO:   41 ms (压缩后: 128.7KB, 压缩比: 71.8%)
性能对比: FastImageUtils 快 1.78x
大小对比: FastImageUtils 更小 0.96x

... 更多详细测试结果
```

## 测试类说明

### `QuickBenchmark.java`
- 🎯 **用途**：快速性能对比，输出简洁的表格
- ⏱️ **运行时间**：约30-60秒  
- 📋 **特点**：
  - 自动检测FastImageUtils库状态
  - 表格化输出，易于阅读
  - 包含压缩速度和压缩率双重对比
  - 如果FastImageUtils不可用，会退化为JDK独立测试

### `ImageCompressionBenchmark.java`
- 🎯 **用途**：全面的基准测试套件
- ⏱️ **运行时间**：约2-5分钟
- 📋 **特点**：
  - 多种图片尺寸测试
  - 内存使用情况监控
  - 预热和多次迭代提高准确性
  - JUnit框架，可单独运行各个测试方法

## 依赖要求

- **Java 8+**
- **Maven 3.6+**
- **FastImageUtils Native Library** (可选，如果不可用会进行JDK独立测试)

## 故障排除

### FastImageUtils库加载失败
```
FastImageUtils 库状态：
Error: Failed to load Fast Image native library for platform: windows-x86_64
```

**解决方案：**
1. 确保 native library 文件在 `src/main/resources/native/` 目录下
2. 文件名格式正确（如：`fast_image-windows-x86_64.dll`）
3. 检查平台架构是否匹配

### 测试图片创建失败
```
测试运行失败: java.awt.HeadlessException
```

**解决方案：**
在Linux服务器上运行时添加JVM参数：
```bash
mvn exec:java -Dexec.args="-Djava.awt.headless=true"
```

## 性能分析建议

### 🚀 速度优化场景
- 批量图片处理
- 实时图片压缩
- 服务器端图片处理

### 💾 大小优化场景  
- 网络传输
- 存储空间限制
- CDN分发

### ⚡ 综合考虑
- Web应用：推荐质量50-70%，平衡速度和大小
- 移动应用：推荐质量30-50%，优先减小文件大小
- 桌面应用：推荐质量70-90%，优先保证质量

## 贡献

欢迎提交Issue和Pull Request来改进这些基准测试！
