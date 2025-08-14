# 更新日志

本文档记录了Fast Image Java项目的所有重要变更。

格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/)，
并且本项目遵循 [语义化版本](https://semver.org/lang/zh-CN/)。

## [未发布]

### 新增
- 初始项目设置

### 修改
- 暂无

### 已弃用
- 暂无

### 移除
- 暂无

### 修复
- 暂无

### 安全性
- 暂无

---

## [1.0.0] - YYYY-MM-DD

### 新增
- 🎉 Fast Image Java首次发布
- ✨ 基于JNI的图像压缩功能
- ⚡ 支持JPEG、PNG、WebP格式
- 🔄 自动原生库下载和管理
- 🧪 完整的性能基准测试套件
- 📊 详细的性能对比报告
- 🏗️ GitHub Actions CI/CD支持
- 📚 完整的文档和使用指南
- 🔒 安全扫描和质量保证

### 技术细节
- **Java版本**: 支持Java 8+
- **平台支持**: Windows, Linux, macOS
- **Rust集成**: 自动从fast-image项目获取原生库
- **性能**: 相比JDK ImageIO有显著性能提升
- **内存**: 优化的内存使用模式
- **并发**: 线程安全的设计

---

## 版本说明

### 版本号规则
我们使用语义化版本号 `MAJOR.MINOR.PATCH`：
- **MAJOR**: 不兼容的API更改
- **MINOR**: 向后兼容的新功能
- **PATCH**: 向后兼容的问题修复

### 标签说明
- `🎉` 重大发布
- `✨` 新功能
- `⚡` 性能改进
- `🐛` 问题修复
- `📚` 文档更新
- `🔒` 安全更新
- `🔄` 重构
- `🧪` 测试
- `🏗️` 构建系统
- `💥` 破坏性变更

### 支持政策
- **最新版本**: 完全支持，包括新功能和修复
- **前一个主版本**: 重要安全修复和严重问题修复
- **更老版本**: 仅提供安全修复

### 升级指南
每个主版本发布时都会提供详细的升级指南：
- API变更说明
- 迁移步骤
- 兼容性说明
- 已知问题

### 发布流程
1. 功能开发和测试完成
2. 更新文档和CHANGELOG
3. 创建版本标签
4. 自动构建和发布
5. 创建GitHub Release
6. 社区通知

---

## 参与贡献

如果您想为此项目做出贡献，请：
1. 查看 [CONTRIBUTING.md](CONTRIBUTING.md) 了解贡献指南
2. 在 [Issues](https://github.com/lihongjie0209/fast-image-java/issues) 中报告问题
3. 在 [Discussions](https://github.com/lihongjie0209/fast-image-java/discussions) 中参与讨论
4. 提交Pull Request改进项目

感谢所有贡献者！🙏
