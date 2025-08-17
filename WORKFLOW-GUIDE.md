# 工作流使用指南

## 统一工作流概述

项目现在使用单个统一的工作流 `.github/workflows/build-and-test.yml`，它根据触发条件自动决定执行模式：

### 触发方式

#### 1. 自动触发（构建和测试）
- **推送到主分支**: `main`, `master`, `develop`
- **Pull Request**: 针对 `main`, `master` 分支
- **执行内容**: 下载原生库 → 跨平台测试 → 构建JAR → 集成测试

#### 2. 标签触发（发布模式）
- **推送 v* 标签**: 例如 `git tag v1.0.0 && git push origin v1.0.0`
- **执行内容**: 完整构建测试 → 创建GitHub Release

#### 3. 手动触发
- **访问**: GitHub仓库 → Actions → "Build, Test and Release" → Run workflow
- **参数**:
  - `release_version`: 发布版本号（例如 `v1.0.0`）
  - 留空 = 仅构建测试
  - 填写 = 创建发布

### 使用场景

#### 📦 仅构建和测试
```bash
# 手动触发，不填写 release_version
# 或者推送代码到主分支
git push origin main
```

#### 🚀 创建发布
```bash
# 方式1: 推送标签（推荐）
git tag v1.0.0
git push origin v1.0.0

# 方式2: 手动触发，填写 release_version = "v1.0.0"
```

### 工作流详细步骤

#### 标准模式（构建+测试）
1. **check-release**: 检查是否为发布模式
2. **download-native-libs**: 下载fast-image原生库
3. **test-cross-platform**: 在多平台测试（Ubuntu, Windows, macOS）
4. **build-jar**: 构建JAR包
5. **integration-test**: 集成测试

#### 发布模式（标准模式 + 发布）
1. 执行所有标准步骤
2. **build-jar**: 额外构建sources.jar和javadoc.jar
3. **test-release**: 在多平台测试发布包
4. **create-release**: 创建GitHub Release

### 发布包内容

发布时会创建以下文件：
- `fast-image-java-{version}.jar` - 主库（包含原生依赖）
- `fast-image-java-{version}-sources.jar` - 源代码
- `fast-image-java-{version}-javadoc.jar` - API文档

### 支持的平台

- **Windows**: x86_64, aarch64
- **Linux**: x86_64, aarch64  
- **macOS**: x86_64 (Intel), aarch64 (Apple Silicon)

### 图片格式处理

库遵循**格式保持原则**：
- PNG输入 → PNG输出
- JPEG输入 → JPEG输出

### 示例

#### 开发阶段测试
```bash
git add .
git commit -m "Add new feature"
git push origin main
# 自动触发构建和测试
```

#### 发布新版本
```bash
# 确保代码已提交
git add .
git commit -m "Release v1.2.0"

# 创建并推送标签
git tag v1.2.0
git push origin v1.2.0
# 自动创建发布
```

#### 紧急手动发布
1. 访问 GitHub Actions
2. 选择 "Build, Test and Release"
3. 点击 "Run workflow"
4. 填写 `release_version`: `v1.2.1`
5. 点击 "Run workflow"

### 工作流状态检查

可以在以下位置检查工作流状态：
- GitHub仓库 → Actions 标签
- 每个commit的状态检查
- Pull Request的检查状态

### 故障排除

如果工作流失败：
1. 检查Actions页面的详细日志
2. 验证原生库版本 `FAST_IMAGE_VERSION` 是否正确
3. 确认代码编译通过
4. 检查测试是否都能通过
