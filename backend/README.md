
# 用户认证系统 - 后端

## 技术栈

- Spring Boot 3.4.0
- Spring Data JPA
- Spring Validation
- Redis
- MySQL
- Java Mail
- Hutool
- Lombok

## 功能特性

### 用户管理

1. 用户注册

   - 邮箱验证码验证
   - 密码强度校验
   - 用户名唯一性校验
   - 密码加密存储
2. 用户登录

   - 基于Redis的会话管理
   - UUID生成会话ID
   - 30分钟会话有效期
3. 密码重置

   - 邮箱验证码重置密码
   - 安全的密码更新流程

### 安全机制

- 密码加密存储
- 接口访问权限控制
- 统一异常处理
- 参数校验
- 接口频率限制

### 缓存设计

- 验证码Redis缓存
- 会话状态Redis缓存

### 通信

- RESTful API设计
- 统一响应结构
- 友好的错误提示

## 项目亮点

- 分层架构
- 统一异常处理
- 数据校验
- 安全性考虑
- 完善的测试支持

## 快速开始

1. 环境要求

   - JDK 21
   - Maven 3.6+
   - MySQL 8.0+
   - Redis 6.0+
2. 配置修改

   ```yaml
   # application.yml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/your_database
       username: your_username
       password: your_password
   ```
3. 运行项目

   ```bash
   mvn spring-boot:run
   ```
