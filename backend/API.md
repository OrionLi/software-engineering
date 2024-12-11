
# API文档

## 基础信息

- 基础路径: `/api`
- 响应格式: JSON
- 认证方式: 请求头 `X-Session-Id`

## 通用响应格式

```json
{
    "code": 200,
    "message": "操作成功",
    "data": null
}
```


## 错误码说明

| 错误码 | 说明               |
| ------ | ------------------ |
| 200    | 成功               |
| 400    | 参数错误           |
| 401    | 未授权             |
| 403    | 禁止访问           |
| 404    | 资源不存在         |
| 1001   | 用户已存在         |
| 1002   | 用户不存在         |
| 1003   | 密码错误           |
| 1004   | 邮箱已被注册       |
| 1005   | 验证码错误或已过期 |
| 500    | 系统错误           |

## 接口详情

### 1. 发送验证码

- 请求路径：`/user/verification-code`
- 请求方法：GET
- 请求参数：
  ```
  email: string (query参数) // 邮箱地址
  ```
- 响应示例：
  ```json
  {
    "code": 200,
    "message": "操作成功",
    "data": null
  }
  ```

### 2. 用户注册

- 请求路径：`/user/register`
- 请求方法：POST
- 请求体：
  ```json
  {
    "username": "string", // 4-32位
    "password": "string", // 至少8位，包含大小写字母和数字
    "email": "string",    // 有效邮箱格式
    "sex": "string",      // M或F
    "verificationCode": "string" // 6位验证码
  }
  ```
- 响应示例：
  ```json
  {
    "code": 200,
    "message": "操作成功",
    "data": null
  }
  ```

### 3. 用户登录

- 请求路径：`/user/login`
- 请求方法：POST
- 请求体：
  ```json
  {
    "username": "string",
    "password": "string"
  }
  ```
- 响应示例：
  ```json
  {
    "code": 200,
    "message": "操作成功",
    "data": {
      "id": 1,
      "username": "testUser",
      "email": "test@example.com",
      "sex": "M",
      "registerDate": "2024-01-01T00:00:00Z",
      "lastModifyDate": "2024-01-01T00:00:00Z",
      "sessionId": "uuid-string"
    }
  }
  ```

### 4. 用户登出

- 请求路径：`/user/logout`
- 请求方法：POST
- 请求头：
  ```
  X-Session-Id: string // 登录时返回的sessionId
  ```
- 响应示例：
  ```json
  {
    "code": 200,
    "message": "操作成功",
    "data": null
  }
  ```

## 注意事项

1. 所有接口（除了登录、注册、发送验证码）都需要在请求头中携带 `X-Session-Id`
2. 会话有效期为30分钟
3. 验证码有效期为5分钟
4. 密码必须包含大小写字母和数字，且长度不少于8位
5. 用户名长度必须在4-32位之间
