# FastImageUtils - 高性能JPEG压缩库 & 性能基准测试

> 🚀 基于Rust的高性能图像压缩JNI库，包含完整的性能对比测试框架

[![Java](https://img.shields.io/badge/Java-8%2B-orange.svg)](https://openjdk.java.net/)
[![Maven](https://img.shields.io/badge/Maven-3.6%2B-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

---

## 📋 **项目概述**

本项目提供了一个完整的JPEG压缩解决方案，包括：

1. **FastImageUtils** - 基于Rust的高性能JNI压缩库
2. **JDK ImageIO对比** - 与Java标准库的全面性能对比
3. **基准测试框架** - 自动化性能测试和分析工具
4. **决策指导** - 详细的方案选择建议

## ⚡ **快速开始**

### 1分钟性能对比测试
```bash
# 克隆项目
git clone https://github.com/your-username/fast-image-java.git
cd fast-image-java

# Windows用户 - 双击运行
run-benchmark.bat

# 或者命令行运行
mvn clean compile test-compile
mvn exec:java@benchmark
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

## 📊 **性能优势一览**

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

## 🧪 **测试报告**

### 核心性能指标
```
=== 压缩效果对比 (1080p图片) ===
质量  | FastImageUtils压缩率 | JDK ImageIO压缩率 | 优势
30%   |      81.2%          |      70.1%       | +11.1%
50%   |      73.5%          |      62.9%       | +10.6%
70%   |      66.0%          |      53.7%       | +12.3%
90%   |      41.0%          |      24.6%       | +16.4%

=== 处理速度对比 ===  
图片大小 | FastImageUtils | JDK ImageIO | JDK优势
15KB    |     8ms       |     8ms     | 持平
40KB    |    19ms       |    14ms     | 36%更快
275KB   |    45ms       |    32ms     | 41%更快
```

**查看完整报告**: [JPEG-COMPRESSION-COMPARISON.md](JPEG-COMPRESSION-COMPARISON.md)

## 🔧 **运行测试**

### 方式1: 图形界面 (Windows)
```cmd
run-benchmark.bat
```
选择对应的测试选项即可。

### 方式2: 命令行
```bash
# 快速性能对比（推荐，1分钟）
mvn exec:java@benchmark

# 完整基准测试套件（详细，5分钟）
mvn test -Dtest=ImageCompressionBenchmark

# 特定测试
mvn test -Dtest=ImageCompressionBenchmark#benchmarkSmallImages
```

### 方式3: IDE运行
直接运行 `QuickBenchmark.main()` 或 `ImageCompressionBenchmark` 测试类。

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

## 🎯 **快速决策**

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
