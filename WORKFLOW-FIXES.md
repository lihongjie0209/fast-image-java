# GitHub Actions workflow更新说明

## 🔧 修复的问题

### 1. **Profile名称不匹配** ✅ 已修复
- **问题**: 使用了已删除的 `maven-central` profile
- **修复**: 改为使用 `release` profile
- **变更**: `-Pmaven-central` → `-Prelease`

### 2. **Secrets配置现代化** ✅ 已修复
- **问题**: 使用旧的 OSSRH secrets
- **修复**: 更新为 central-publishing 所需的 secrets
- **变更**:
  - `OSSRH_USERNAME` → `CENTRAL_TOKEN_USERNAME`
  - `OSSRH_TOKEN` → `CENTRAL_TOKEN_PASSWORD`

### 3. **Server ID配置** ✅ 已修复
- **问题**: `server-id: ossrh` 不匹配
- **修复**: 改为 `server-id: central`
- **原因**: 新插件使用不同的server配置

### 4. **Maven命令现代化** ✅ 已修复
- **问题**: 使用过时的 `-DperformRelease=true`
- **修复**: 简化为 `-Prelease`
- **优势**: 与modern plugin配置一致

### 5. **摘要信息增强** ✅ 已修复
- **问题**: 摘要没有反映modern plugin特性
- **修复**: 
  - 添加插件版本信息
  - 添加Central Portal链接
  - 显示所需的Secrets列表
  - 标注auto-publish特性

## 🎯 现在的配置

### Maven发布命令
```bash
mvn clean deploy -Prelease -Dgpg.passphrase="$GPG_PASSPHRASE"
```

### 所需GitHub Secrets
```
CENTRAL_TOKEN_USERNAME=<Maven Central用户名>
CENTRAL_TOKEN_PASSWORD=<Maven Central令牌>
GPG_PRIVATE_KEY=<GPG私钥内容>
GPG_PASSPHRASE=<GPG密钥密码>
```

### 发布流程
1. 检测到release标签
2. 构建和测试项目
3. 下载native libraries
4. 更新版本号（移除-SNAPSHOT）
5. 导入GPG密钥
6. 使用central-publishing-maven-plugin发布
7. 自动发布到Maven Central (autoPublish=true)

## ✅ 验证检查表

- [x] Profile名称正确 (`release`)
- [x] Secrets名称更新 (`CENTRAL_TOKEN_*`)
- [x] Server ID匹配 (`central`)
- [x] Maven命令现代化 (`-Prelease`)
- [x] 摘要信息完整
- [x] 与pom.xml配置一致

## 🚀 下一步

1. **设置GitHub Secrets**: 根据MAVEN-CENTRAL-GUIDE.md设置所需secrets
2. **获取Maven Central访问权限**: 申请cn.lihongjie命名空间
3. **测试发布**: 创建测试标签验证完整流程

现在GitHub Actions workflow与现代化的Maven配置完全匹配！
