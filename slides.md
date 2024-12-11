# 项目整体架构概览

- **三层架构**
  - 表现层（Controller）
  - 业务逻辑层（Service）
  - 数据访问层（Repository）

- **关键技术栈**
  - Spring Boot
  - Spring Data JPA
  - Redis
  - MapStruct
  - JUnit 5

---

# 设计模式应用

1. **依赖注入模式**
```java
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
}
```

2. **策略模式**（密码加密）
```java
private static final Map<String, EncryptionPolicy> ENCRYPTION_POLICY_MAP = Map.of(
    "v1", (plainPassword, salt) -> SecureUtil.md5(plainPassword + salt),
    "v2", (plainPassword, salt) -> SecureUtil.sha1(plainPassword + salt)
);
```

---

# SOLID设计原则实践与密码安全

1. **单一职责原则（SRP）**
   - `UserService`专注用户相关业务逻辑
   - `HexUtil`专注密码安全加密

2. **密码安全加密策略**
   ```java
   // 动态加密策略映射
   private static final Map<String, EncryptionPolicy> ENCRYPTION_POLICY_MAP = Map.of(
       "v1", ((plainPassword, salt) -> MD5_DIGESTER.digestHex(plainPassword + salt)),
       "v2", ((plainPassword, salt) -> SHA256_DIGESTER.digestHex(plainPassword + salt)),
       "v3", ((plainPassword, salt) -> SM3_DIGESTER.digestHex(plainPassword + salt))
   );
   ```

3. **加密设计亮点**
   - 动态算法选择、随机盐值生成
   - 多重加密策略
   - 版本化加密方案

---

# 密码安全与加密机制详解

1. **密码存储格式**
   ```
   盐值$算法版本$加密结果，例如：
   "randomSalt$v3$encryptedPassword"
   ```

2. **加密验证流程**
   ```java
   public static boolean verify(String storedPassword, String plainPassword) {
       String[] parts = storedPassword.split(SEPARATOR);
       String salt = parts[0];           // 提取盐值
       String algorithmCode = parts[1];  // 提取算法版本
       String encryptedValue = parts[2]; // 提取加密结果

       // 根据版本选择对应加密策略
       EncryptionPolicy policy = ENCRYPTION_POLICY_MAP.get(algorithmCode);
       
       return encryptedValue.equals(policy.encrypt(plainPassword, salt));
   }
   ```

3. **安全特性**
   - 动态盐值防止彩虹表攻击、多算法支持、版本可平滑升级、加密过程不可逆

---

# 代码解耦与模块化

1. **解耦策略**
   - 使用接口定义服务契约
   - 依赖抽象而非具体实现
   
2. **模块化示例**
   ```java
   public interface UserService {
       void register(UserRegisterVO registerVO);
       User login(UserLoginVO loginVO);
   }
   ```

---

# 测试策略

我们的测试策略主要包含三个层次：

1. **单元测试**
   - 针对单个方法或类进行测试
   - 使用 JUnit 5 和 Mockito
   - 目标是验证代码的基本逻辑正确性

2. **集成测试**
   - 测试多个组件之间的交互
   - 验证服务层、仓库层的协同工作
   - 使用 Spring Boot 的测试支持

3. **端到端测试**
   - 模拟真实的用户场景
   - 验证整个系统的流程

---

# 测试覆盖率策略

1. **分支覆盖**
   - 覆盖所有可能的代码执行路径
   - 包括正常流程和异常场景

2. **边界值测试**
   - 测试输入的边界条件
   - 例如：用户名长度、密码复杂度

3. **异常场景测试**
   - 模拟各种异常情况
   - 验证系统的健壮性

---

# 测试示例 

```java
@Nested
@DisplayName("注册功能测试")
class RegisterTest {
    @Test
    @DisplayName("正常注册流程")
    void testNormalRegister() {
        // 1. 发送验证码
        // 2. 准备注册数据
        // 3. 执行注册
        // 4. 验证用户数据
    }

    @Test
    @DisplayName("重复用户名注册")
    void testDuplicateUsername() {
        // 验证重复用户名被拒绝
    }

    @Test
    @DisplayName("验证码错误")
    void testWrongVerificationCode() {
        // 验证验证码校验机制
    }
}
``` 

---

# Mock 和 Stub 的使用

1. **Mock 的使用**
   - 模拟外部依赖：`doNothing().when(mailSender).send(any(SimpleMailMessage.class))`
   - 它预先定义了一个虚拟的行为（什么都不做），目的是替换真实的操作逻辑，如：邮件发送

2. **Stub 的使用**
   - 模拟内部依赖：`verify(mailSender, times(1)).send(any(SimpleMailMessage.class))`
   - 验证方法被调用的次数和方式，关注的是方法的交互细节，例如：数据库操作、外部 API

---

# 测试示例

```java
@Test
void testSendVerificationCode() {
    // 使用 Mockito 模拟邮件发送服务
    doNothing().when(mailSender).send(any(SimpleMailMessage.class));
    
    // 测试验证码发送逻辑
    userService.sendVerificationCode("test@example.com");
    
    // 验证邮件发送方法被调用
    verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
}
```

---

# 异常处理与安全性

1. **全局异常处理**
   ```java
   @RestControllerAdvice
   public class GlobalExceptionHandler {
       @ExceptionHandler(BusinessException.class)
       public Result<Void> handleBusinessException(BusinessException e) {
           return Result.error(e.getResultCode(), e.getMessage());
       }
   }
   ```

2. **安全性考虑**
   - 密码加盐加密
   - 动态加密算法
   - 验证码机制

---

# 数据验证与校验

1. **参数校验**
   ```java
   public class UserRegisterVO {
       @NotBlank(message = "用户名不能为空")
       @Size(min = 4, max = 32)
       private String username;

       @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$")
       private String password;
   }
   ```

2. **验证层次**
   - 前端校验
   - 后端参数校验
   - 业务层校验

---

# 持久化与缓存策略

1. **JPA持久化**
   ```java
   public interface UserRepository extends JpaRepository<User, Long> {
       User findByUsername(String username);
   }
   ```

2. **Redis缓存**
   - 会话管理
   - 验证码存储
   - 提高系统性能
