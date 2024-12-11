# 用户认证系统

一个基于Spring Boot的用户认证系统，提供用户注册、登录、登出等基础功能。

## 主要特性

- 用户注册
  - 邮箱验证码验证
  - 密码强度校验
  - 用户名唯一性校验
  - 邮箱唯一性校验
  
- 用户登录
  - 基于Redis的会话管理
  - UUID生成的会话ID
  - 30分钟会话有效期
  
- 安全特性
  - 密码加密存储
  - 防止重复注册
  - 接口访问权限控制
  - 接口频率限制

## 技术栈

- Spring Boot
- Spring Data JPA
- Redis
- MySQL
- Java Mail
- Lombok
- JUnit 5

## 项目亮点

1. 分层架构清晰
   - Controller层处理请求响应
   - Service层处理业务逻辑
   - Repository层处理数据访问
   - VO对象处理数据传输

2. 统一异常处理
   - 业务异常统一处理
   - 友好的错误提示

3. 数据校验
   - 使用JSR-303注解进行参数校验
   - 自定义校验注解
   
4. 缓存设计
   - 验证码Redis缓存
   - 会话状态Redis缓存
   
5. 安全性考虑
   - 密码加密存储
   - 会话管理
   - 接口权限控制

6. 完善的测试
   - 单元测试覆盖
   - 集成测试支持
   
7. 代码规范
   - 统一的编码风格
   - 完善的注释
   - 规范的命名

## 快速开始

1. 环境要求
   - JDK 17+
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
     
     redis:
       host: localhost
       port: 6379
       
     mail:
       host: smtp.example.com
       username: your_email
       password: your_password
   ```

3. 运行项目
   ```bash
   mvn spring-boot:run
   ```

## 接口文档

详细的API文档请参考 [API.md](./API.md) 