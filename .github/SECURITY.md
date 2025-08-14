# 安全配置指南

## 🔐 敏感信息管理

### GitHub Secrets 设置

为了确保CI/CD流程正常工作，需要在GitHub仓库中配置以下Secrets：

#### 必需的Secrets
```
SONAR_TOKEN          # SonarCloud访问令牌
GPG_PRIVATE_KEY      # GPG私钥 (用于Maven Central签名)
GPG_PASSPHRASE       # GPG密码短语
OSSRH_USERNAME       # OSSRH用户名 (Maven Central)
OSSRH_PASSWORD       # OSSRH密码 (Maven Central)
```

#### 可选的Secrets
```
CODECOV_TOKEN        # Codecov上传令牌 (如果使用Codecov)
SLACK_WEBHOOK        # Slack通知webhook (如果需要通知)
```

### 🔧 Secrets配置步骤

#### 1. SonarCloud Token
1. 访问 [SonarCloud](https://sonarcloud.io/)
2. 登录并进入项目设置
3. 生成新的Token: `User Settings -> Security -> Generate Token`
4. 在GitHub仓库设置中添加 `SONAR_TOKEN`

#### 2. GPG签名配置 (Maven Central发布)
```bash
# 生成GPG密钥对
gpg --gen-key

# 导出私钥 (用于GitHub Secret)
gpg --export-secret-keys --armor YOUR_KEY_ID

# 导出公钥 (上传到密钥服务器)
gpg --export --armor YOUR_KEY_ID > public-key.asc
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
```

#### 3. OSSRH账号 (Maven Central)
1. 注册 [OSSRH账号](https://issues.sonatype.org/secure/Signup!default.jspa)
2. 创建项目票据申请发布权限
3. 在GitHub中设置 `OSSRH_USERNAME` 和 `OSSRH_PASSWORD`

### 📋 本地开发环境设置

#### Maven设置文件 (`~/.m2/settings.xml`)
```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 
  http://maven.apache.org/xsd/settings-1.0.0.xsd">
  
  <servers>
    <!-- OSSRH Snapshot Repository -->
    <server>
      <id>ossrh</id>
      <username>${env.OSSRH_USERNAME}</username>
      <password>${env.OSSRH_PASSWORD}</password>
    </server>
  </servers>
  
  <profiles>
    <profile>
      <id>gpg-sign</id>
      <properties>
        <gpg.keyname>YOUR_KEY_ID</gpg.keyname>
        <gpg.passphrase>${env.GPG_PASSPHRASE}</gpg.passphrase>
      </properties>
    </profile>
  </profiles>
</settings>
```

#### 环境变量设置
```bash
# Linux/macOS
export GPG_PASSPHRASE="your-passphrase"
export OSSRH_USERNAME="your-username"
export OSSRH_PASSWORD="your-password"
export SONAR_TOKEN="your-token"

# Windows PowerShell
$env:GPG_PASSPHRASE="your-passphrase"
$env:OSSRH_USERNAME="your-username"
$env:OSSRH_PASSWORD="your-password"
$env:SONAR_TOKEN="your-token"
```

## 🛡️ 安全最佳实践

### 代码安全
- [ ] 绝不在代码中硬编码敏感信息
- [ ] 使用环境变量或GitHub Secrets
- [ ] 定期更新依赖以修复安全漏洞
- [ ] 启用Dependabot安全更新

### CI/CD安全
- [ ] 限制GitHub Actions权限
- [ ] 使用最小权限原则
- [ ] 定期轮换访问令牌
- [ ] 监控异常的构建活动

### 依赖安全
- [ ] 定期运行安全扫描
- [ ] 使用Maven dependency-check插件
- [ ] 监控CVE数据库更新
- [ ] 及时更新有漏洞的依赖

### 访问控制
- [ ] 保护主分支，要求PR审查
- [ ] 限制可以推送标签的人员
- [ ] 启用二次身份验证
- [ ] 定期审查协作者权限

## 🔍 安全扫描工具

### 集成的安全工具
1. **SonarCloud**: 代码质量和安全性扫描
2. **Dependabot**: 依赖安全更新
3. **CodeQL**: GitHub原生安全扫描
4. **OSSF Scorecard**: 开源项目安全评分

### 本地安全检查
```bash
# Maven安全插件
mvn org.owasp:dependency-check-maven:check

# 查找敏感信息泄露
git secrets --scan-history

# GPG签名验证
mvn verify -Dgpg.skip=false
```

## 🚨 安全事件响应

### 发现安全问题时
1. **不要**在公开issue中报告安全漏洞
2. 发送邮件到项目维护者
3. 等待确认和修复方案
4. 配合进行负责任的披露

### 安全更新发布
1. 创建安全补丁
2. 在私有分支中测试
3. 准备安全公告
4. 同时发布补丁和公告
5. 通知用户立即更新

### 事件后处理
1. 分析安全事件原因
2. 改进安全流程
3. 更新安全文档
4. 加强监控措施

## 📞 安全联系方式

### 报告安全漏洞
- **邮箱**: security@example.com (替换为实际邮箱)
- **PGP Key**: 提供用于加密通信的公钥
- **响应时间**: 48小时内确认收到

### 安全更新通知
- GitHub Security Advisories
- 项目邮件列表
- 社交媒体公告

---

**重要提醒**: 安全是持续的过程，需要定期评估和改进！🔒
