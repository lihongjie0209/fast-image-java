# FastImageUtils 性能测试 - 快速使用指南

## 🚀 快速开始

### 最简单的方式（Windows）
1. 双击 `run-benchmark.bat`
2. 选择 `[1] 快速性能对比`
3. 等待1分钟获得结果

### 命令行方式
```bash
# 编译项目
mvn clean compile test-compile

# 运行快速性能对比（推荐）
mvn exec:java@benchmark

# 运行完整测试套件
mvn test -Dtest=ImageCompressionBenchmark
```

## 📊 测试结果解读

### 快速基准测试输出示例
```
质量  | FastImageUtils        | JDK ImageIO          | 性能提升
------|----------------------|----------------------|----------
 30%  |   19 ms (5.6KB)     |   14 ms (12.0KB)    | 1.36x 更慢
 50%  |   21 ms (7.0KB)     |   13 ms (13.8KB)    | 1.62x 更慢  
 70%  |   24 ms (10.1KB)    |   14 ms (16.7KB)    | 1.71x 更慢
 90%  |   31 ms (19.6KB)    |   15 ms (28.7KB)    | 2.07x 更慢
```

### 关键指标含义

**🏃 性能指标**
- `FastImageUtils`: Rust原生库的压缩时间
- `JDK ImageIO`: Java标准库的压缩时间  
- `性能提升`: 相对速度比较（越大越好）

**💾 压缩效果**
- `(5.6KB)`: 压缩后的文件大小
- `压缩率`: 文件大小减少的百分比
- `压缩率差异`: FastImageUtils相对JDK的压缩优势

### 结果分析

**🎯 当FastImageUtils更快时**
```
性能提升: 1.85x 更快
```
说明：FastImageUtils在该质量级别下比JDK快85%

**📦 当压缩率更好时**
```  
压缩率差异: +11.1%
```
说明：FastImageUtils比JDK多压缩了11.1%的空间

## 💡 实际应用建议

### 选择FastImageUtils的场景
- ✅ 需要更小的文件大小（节省存储/传输成本）
- ✅ 批量处理大量图片
- ✅ 对压缩质量有高要求
- ✅ 服务器端图片处理

### 选择JDK ImageIO的场景  
- ✅ 对处理速度要求极高
- ✅ 简单的图片处理需求
- ✅ 不想引入额外的原生库依赖
- ✅ 跨平台兼容性优先

## 🔧 故障排除

### FastImageUtils库加载失败
```
库可用性: ✗ 不可用
```
**解决方案：**
1. 检查 `src/main/resources/native/` 目录下是否有对应平台的`.dll`/`.so`/`.dylib`文件
2. 确认文件名格式：`fast_image-windows-x86_64.dll`
3. 重新编译项目：`mvn clean compile test-compile`

### 测试运行失败
```
❌ 测试运行失败！
```
**常见原因：**
- 项目未编译：运行 `mvn clean compile test-compile`
- Java版本问题：确保Java 8+
- 内存不足：调整JVM参数 `-Xmx1g`

## 📈 性能优化建议

### 质量级别选择
- **Web应用**: 50-70% (平衡速度和大小)
- **移动应用**: 30-50% (优先减小文件)  
- **桌面应用**: 70-90% (优先保证质量)
- **存储优化**: 30% (最大压缩比)

### 批量处理优化
```java
// 预热JNI库
FastImageUtils.compress(sampleImage, 70);

// 批量处理
for (byte[] image : imageList) {
    byte[] compressed = FastImageUtils.compress(image, 70);
    // 处理压缩结果...
}
```

希望这个性能测试能帮助你选择最适合的图片压缩方案！🚀
