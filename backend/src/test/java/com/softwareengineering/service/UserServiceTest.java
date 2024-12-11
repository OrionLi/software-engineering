package com.softwareengineering.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.softwareengineering.common.ResultCode;
import com.softwareengineering.constant.CommonConstants;
import com.softwareengineering.dao.UserRepository;
import com.softwareengineering.entity.User;
import com.softwareengineering.exception.BusinessException;
import com.softwareengineering.util.HexUtil;
import com.softwareengineering.vo.UserLoginVO;
import com.softwareengineering.vo.UserRegisterVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@DisplayName("用户服务测试")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private JavaMailSender mailSender;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String TEST_EMAIL = "test@example.com";

    private static final String TEST_USERNAME = "testUser";

    private static final String TEST_PASSWORD = "TestPass123";

    private static final String TEST_VERIFICATION_CODE = "123456";

    @BeforeEach
    void setUp() {
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        // 清理Redis中的测试数据
        redisTemplate.delete("verification:code:" + TEST_EMAIL);
    }

    @Nested
    @DisplayName("注册功能测试")
    class RegisterTest {

        @Test
        @DisplayName("正常注册流程")
        @Transactional
        @Rollback
        void testNormalRegister() {
            // 1. 发送验证码
            assertDoesNotThrow(() -> userService.sendVerificationCode(TEST_EMAIL));
            verify(mailSender, times(1)).send(any(SimpleMailMessage.class));

            // 2. 准备注册数据
            UserRegisterVO registerVO = createValidRegisterVO();

            // 3. 模拟Redis中存储的验证码
            redisTemplate.opsForValue().set("verification:code:" + TEST_EMAIL, TEST_VERIFICATION_CODE);

            // 4. 执行注册
            assertDoesNotThrow(() -> userService.register(registerVO));

            // 5. 验证用户数据
            User savedUser = userRepository.findByUsername(TEST_USERNAME);
            assertUserData(savedUser);
        }

        @Test
        @DisplayName("重复用户名注册")
        @Transactional
        @Rollback
        void testDuplicateUsername() {
            // 1. 先注册一个用户
            prepareExistingUser();

            // 2. 尝试使用相同用户名注册
            UserRegisterVO duplicateVO = createValidRegisterVO();
            duplicateVO.setEmail("another@example.com");

            // 3. 验证异常
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.register(duplicateVO));
            assertEquals("用户已存在", exception.getMessage());
        }

        @Test
        @DisplayName("验证码错误")
        @Transactional
        @Rollback
        void testWrongVerificationCode() {
            // 1. 准备注册数据
            UserRegisterVO registerVO = createValidRegisterVO();
            registerVO.setVerificationCode("wrong_code");

            // 2. 模拟Redis中存储的验证码
            redisTemplate.opsForValue().set("verification:code:" + TEST_EMAIL, TEST_VERIFICATION_CODE);

            // 3. 验证异常
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.register(registerVO));
            assertEquals("验证码错误或已过期", exception.getMessage());
        }

    }

    @Nested
    @DisplayName("登录功能测试")
    class LoginTest {

        @Test
        @DisplayName("正常登录")
        @Transactional
        @Rollback
        void testNormalLogin() {
            // 1. 准备测试用户
            prepareExistingUser();

            // 2. 执行登录
            UserLoginVO loginVO = new UserLoginVO();
            loginVO.setUsername(TEST_USERNAME);
            loginVO.setPassword(TEST_PASSWORD);

            User loggedInUser = assertDoesNotThrow(() -> userService.login(loginVO));
            assertEquals(TEST_USERNAME, loggedInUser.getUsername());
            assertNotNull(loggedInUser.getSessionId());

            // 3. 验证Redis中的会话信息
            String sessionKey = CommonConstants.Redis.SESSION_PREFIX + loggedInUser.getSessionId();
            String userId = redisTemplate.opsForValue().get(sessionKey);
            assertNotNull(userId);
            assertEquals(loggedInUser.getId().toString(), userId);
        }

        @Test
        @DisplayName("密码错误")
        @Transactional
        @Rollback
        void testWrongPassword() {
            // 1. 准备测试用户
            prepareExistingUser();

            // 2. 使用错误密码登录
            UserLoginVO loginVO = new UserLoginVO();
            loginVO.setUsername(TEST_USERNAME);
            loginVO.setPassword("WrongPass123");

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.login(loginVO));
            assertEquals("密码错误", exception.getMessage());
        }

    }

    @Nested
    @DisplayName("登出功能测试")
    class LogoutTest {

        @Test
        @DisplayName("正常登出")
        @Transactional
        @Rollback
        void testNormalLogout() {
            // 1. 准备已登录用户
            prepareExistingUser();
            UserLoginVO loginVO = new UserLoginVO();
            loginVO.setUsername(TEST_USERNAME);
            loginVO.setPassword(TEST_PASSWORD);
            User loggedInUser = userService.login(loginVO);
            String sessionId = loggedInUser.getSessionId();

            // 2. 执行登出
            assertDoesNotThrow(() -> userService.logout(sessionId));

            // 3. 验证会话已被删除
            String sessionKey = CommonConstants.Redis.SESSION_PREFIX + sessionId;
            assertNull(redisTemplate.opsForValue().get(sessionKey));
        }

        @Test
        @DisplayName("使用无效会话ID登出")
        void testLogoutWithInvalidSession() {
            String invalidSessionId = "invalid-session-id";
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.logout(invalidSessionId));
            assertEquals(ResultCode.UNAUTHORIZED.getMessage(), exception.getMessage());
        }

    }

    @Test
    @DisplayName("验证码发送测试")
    void testSendVerificationCode() {
        assertDoesNotThrow(() -> userService.sendVerificationCode(TEST_EMAIL));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));

        String savedCode = redisTemplate.opsForValue().get("verification:code:" + TEST_EMAIL);
        assertNotNull(savedCode);
        assertEquals(6, savedCode.length());
    }

    // 辅助方法
    private UserRegisterVO createValidRegisterVO() {
        UserRegisterVO registerVO = new UserRegisterVO();
        registerVO.setUsername(TEST_USERNAME);
        registerVO.setPassword(TEST_PASSWORD);
        registerVO.setEmail(TEST_EMAIL);
        registerVO.setSex("M");
        registerVO.setVerificationCode(TEST_VERIFICATION_CODE);
        return registerVO;
    }

    private void prepareExistingUser() {
        UserRegisterVO registerVO = createValidRegisterVO();
        redisTemplate.opsForValue().set("verification:code:" + TEST_EMAIL, TEST_VERIFICATION_CODE);
        userService.register(registerVO);
    }

    private void assertUserData(User user) {
        assertNotNull(user);
        assertEquals(TEST_USERNAME, user.getUsername());
        assertEquals(TEST_EMAIL, user.getEmail());
        assertEquals('M', user.getSex());
        assertNotNull(user.getRegisterDate());
        assertNotNull(user.getLastModifyDate());
        assertTrue(HexUtil.verify(user.getPassword(), TEST_PASSWORD));
    }

}