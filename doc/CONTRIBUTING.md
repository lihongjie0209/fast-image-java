# 贡献指南

感谢你对 Fast Image Java 项目的兴趣！我们欢迎各种形式的贡献。

## 🚀 快速开始

### 开发环境设置

1. **前置要求**
   - Java 8 或更高版本
   - Maven 3.6 或更高版本
   - Git

2. **克隆项目**
   ```bash
   git clone https://github.com/lihongjie0209/fast-image-java.git
   cd fast-image-java
   ```

3. **构建项目**
   ```bash
   mvn clean compile test-compile
   ```

4. **运行测试**
   ```bash
   mvn test
   mvn exec:java@benchmark
   ```

## 📝 如何贡献

### 报告问题

1. 检查 [现有问题](https://github.com/lihongjie0209/fast-image-java/issues) 是否已有相关报告
2. 使用合适的问题模板创建新issue
3. 提供详细的重现步骤和环境信息

### 提交代码

1. **Fork 项目**
   ```bash
   # 在GitHub上Fork项目，然后克隆你的fork
   git clone https://github.com/your-username/fast-image-java.git
   ```

2. **创建特性分支**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **开发和测试**
   ```bash
   # 编写代码
   # 运行测试确保没有破坏现有功能
   mvn test
   
   # 运行性能测试
   mvn exec:java@benchmark
   ```

4. **提交代码**
   ```bash
   git add .
   git commit -m "feat: 添加新功能描述"
   ```

5. **推送并创建PR**
   ```bash
   git push origin feature/your-feature-name
   # 在GitHub上创建Pull Request
   ```

## 🎯 贡献类型

### 代码贡献
- 新功能开发
- 性能优化
- Bug修复
- 代码重构

### 文档贡献
- API文档改进
- 使用指南更新
- 性能测试报告
- 最佳实践文档

### 测试贡献
- 添加单元测试
- 性能基准测试
- 跨平台兼容性测试
- 压力测试

## 📏 代码规范

### Java代码风格
- 遵循标准Java命名约定
- 类名使用PascalCase
- 方法和变量使用camelCase
- 常量使用UPPER_SNAKE_CASE
- 每行代码不超过120个字符

### 注释规范
```java
/**
 * 压缩图像数据
 * 
 * @param imageBytes 图像字节数组
 * @param quality 压缩质量 (0-100)
 * @return 压缩后的字节数组
 * @throws IllegalArgumentException 当质量参数不在有效范围内
 */
public static byte[] compress(byte[] imageBytes, int quality) {
    // 实现逻辑
}
```

### 提交信息格式
遵循 [Conventional Commits](https://www.conventionalcommits.org/) 规范：

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

**类型：**
- `feat`: 新功能
- `fix`: Bug修复
- `docs`: 文档更改
- `style`: 代码格式化
- `refactor`: 代码重构
- `test`: 添加测试
- `chore`: 构建过程或辅助工具的变动

**示例：**
```
feat(compression): 添加PNG格式支持

- 实现PNG图像格式检测
- 添加PNG压缩算法
- 更新相关测试用例

Closes #123
```

## 🧪 测试指南

### 运行所有测试
```bash
mvn test
```

### 运行性能基准测试
```bash
mvn exec:java@benchmark
```

### 运行特定测试
```bash
mvn test -Dtest=ImageCompressionBenchmark#benchmarkSmallImages
```

### 代码覆盖率
```bash
mvn jacoco:report
# 查看 target/site/jacoco/index.html
```

## 📦 发布流程

### 版本号规则
遵循 [Semantic Versioning](https://semver.org/)：
- MAJOR.MINOR.PATCH (例如: 1.2.3)
- 主版本号：不兼容的API修改
- 次版本号：向后兼容的功能性新增
- 修订号：向后兼容的问题修正

### 发布步骤
1. 更新版本号 (`mvn versions:set -DnewVersion=1.2.3`)
2. 更新CHANGELOG.md
3. 提交并推送代码
4. 创建发布标签 (`git tag v1.2.3`)
5. 推送标签 (`git push origin v1.2.3`)
6. GitHub Actions将自动构建和发布

## 🎨 性能测试贡献

### 添加新的基准测试
1. 在 `ImageCompressionBenchmark.java` 中添加新的测试方法
2. 确保测试方法以 `benchmark` 开头
3. 包含适当的预热和迭代次数
4. 添加详细的性能结果分析

### 测试结果格式
```java
@Test
public void benchmarkNewFeature() {
    System.out.println("=== 新功能性能测试 ===");
    
    BenchmarkResult result = performBenchmark();
    
    System.out.printf("处理时间: %s\n", formatTime(result.avgTime));
    System.out.printf("内存使用: %s\n", formatBytes(result.memoryUsed));
    System.out.printf("吞吐量: %.2f ops/sec\n", result.throughput);
}
```

## 📞 获取帮助

- 📧 Email: lihongjie0209@example.com
- 🐛 Issues: [GitHub Issues](https://github.com/lihongjie0209/fast-image-java/issues)
- 💬 Discussions: [GitHub Discussions](https://github.com/lihongjie0209/fast-image-java/discussions)

## 📜 许可证

通过向这个项目贡献代码，你同意你的贡献将在 [MIT许可证](LICENSE) 下发布。

---

感谢你的贡献！🎉
