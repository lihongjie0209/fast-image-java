# 发布检查清单

## 🚀 发布前检查

### 代码质量
- [ ] 所有测试通过 (`mvn test`)
- [ ] 性能基准测试正常运行 (`mvn exec:java@benchmark`)
- [ ] 代码覆盖率达到要求 (`mvn jacoco:report`)
- [ ] SonarCloud扫描无重大问题
- [ ] 代码符合规范，无明显的代码异味

### 文档更新
- [ ] README.md 版本信息已更新
- [ ] CHANGELOG.md 包含新版本的变更记录
- [ ] JavaDoc 注释完整且准确
- [ ] 性能基准测试结果已更新

### 依赖和兼容性
- [ ] 原生库版本已更新到最新
- [ ] 支持的Java版本测试通过 (8, 11, 17, 21)
- [ ] 跨平台兼容性验证 (Windows, Linux, macOS)
- [ ] Maven依赖无冲突

### 版本管理
- [ ] 版本号遵循语义化版本 (Semantic Versioning)
- [ ] pom.xml 版本号已更新
- [ ] Git标签准备就绪
- [ ] 发布分支代码稳定

### 构建和打包
- [ ] 本地构建成功 (`mvn clean package`)
- [ ] JAR文件包含所有必要的原生库
- [ ] 源码包完整
- [ ] 签名验证 (如果适用)

## 📋 发布步骤

### 1. 准备发布
```bash
# 确保在主分支上
git checkout main
git pull origin main

# 检查工作区是否干净
git status

# 运行完整测试
mvn clean test
mvn exec:java@benchmark
```

### 2. 更新版本
```bash
# 更新版本号
mvn versions:set -DnewVersion=1.0.0
mvn versions:commit

# 更新文档中的版本引用
# 编辑 README.md, CHANGELOG.md 等文件
```

### 3. 提交和标签
```bash
# 提交版本更新
git add .
git commit -m "chore: bump version to 1.0.0"

# 创建发布标签
git tag -a v1.0.0 -m "Release v1.0.0"

# 推送到远程仓库
git push origin main
git push origin v1.0.0
```

### 4. 自动化发布
- GitHub Actions 将自动触发构建和发布流程
- 监控 Actions 执行状态
- 验证发布是否成功创建

### 5. 发布后验证
- [ ] GitHub Release 页面显示正常
- [ ] 下载链接可用
- [ ] JAR文件可正常使用
- [ ] 文档链接正确

## 🔄 自动化 CI/CD

### GitHub Actions 工作流

#### 持续集成 (`.github/workflows/ci.yml`)
- **触发条件**: Push 到 main/master 分支，Pull Request
- **执行内容**:
  - 多版本Java测试 (8, 11, 17, 21)
  - 跨平台测试 (Ubuntu, Windows, macOS)
  - 自动下载最新原生库
  - 运行单元测试和基准测试
  - 代码质量扫描

#### 构建和发布 (`.github/workflows/build-and-release.yml`)
- **触发条件**: 推送版本标签 (v*)
- **执行内容**:
  - 从 Rust 仓库下载最新原生库
  - 多平台构建验证
  - 创建发布包 (JAR + 源码)
  - 自动生成发布说明
  - 创建 GitHub Release

### 原生库依赖管理
- 自动从 `lihongjie0209/fast-image` 仓库获取最新发布
- 支持指定特定版本的原生库
- CI 构建时自动验证库的完整性

### 代码质量保证
- **SonarCloud**: 代码质量和安全性扫描
- **JaCoCo**: 代码覆盖率报告
- **Maven**: 依赖漏洞检查
- **多Java版本**: 兼容性验证

## 📞 问题处理

### 发布失败处理
1. 检查 GitHub Actions 日志
2. 验证原生库下载是否成功
3. 检查版本号格式是否正确
4. 确认权限设置是否正确

### 回滚步骤
```bash
# 删除错误的标签
git tag -d v1.0.0
git push origin :refs/tags/v1.0.0

# 删除 GitHub Release (手动在页面操作)

# 修复问题后重新发布
```

### 联系方式
- GitHub Issues: [项目问题页面](https://github.com/lihongjie0209/fast-image-java/issues)
- GitHub Actions: [构建历史](https://github.com/lihongjie0209/fast-image-java/actions)

---

**记住**: 发布是一个重要的里程碑，务必仔细检查每一步！🎯
