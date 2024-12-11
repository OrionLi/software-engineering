package com.softwareengineering.service.impl;

import com.softwareengineering.common.ResultCode;
import com.softwareengineering.constant.CommonConstants;
import com.softwareengineering.dao.UserRepository;
import com.softwareengineering.entity.User;
import com.softwareengineering.exception.BusinessException;
import com.softwareengineering.mapper.UserMapper;
import com.softwareengineering.service.UserService;
import com.softwareengineering.util.HexUtil;
import com.softwareengineering.vo.UserLoginVO;
import com.softwareengineering.vo.UserRegisterVO;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final JavaMailSender mailSender;

    private final StringRedisTemplate redisTemplate;

    private static final String VERIFICATION_CODE_PREFIX = "verification:code:";

    private static final long VERIFICATION_CODE_EXPIRE = 5;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(UserRegisterVO registerVO) {
        // 验证用户名和邮箱是否已存在
        if (userRepository.existsByUsername(registerVO.getUsername())) {
            throw new BusinessException(ResultCode.USER_EXISTED);
        }
        if (userRepository.existsByEmail(registerVO.getEmail())) {
            throw new BusinessException(ResultCode.EMAIL_EXISTED);
        }

        // 验证验证码
        String codeKey = VERIFICATION_CODE_PREFIX + registerVO.getEmail();
        String savedCode = redisTemplate.opsForValue().get(codeKey);
        if (savedCode == null || !savedCode.equals(registerVO.getVerificationCode())) {
            throw new BusinessException(ResultCode.VERIFICATION_CODE_ERROR);
        }

        // 转换并保存用户
        User user = userMapper.toEntity(registerVO);
        user.setPassword(HexUtil.encryptAndFormat(registerVO.getPassword()));
        userRepository.save(user);

        // 删除验证码
        redisTemplate.delete(codeKey);
    }

    @Override
    public User login(UserLoginVO loginVO) {
        User user = userRepository.findByUsername(loginVO.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (!HexUtil.verify(user.getPassword(), loginVO.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        // 生成会话ID并存储到Redis
        String sessionId = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                CommonConstants.Redis.SESSION_PREFIX + sessionId,
                user.getId().toString(),
                CommonConstants.Redis.SESSION_EXPIRE,
                TimeUnit.MINUTES
        );

        // 设置sessionId到用户对象中(临时属性)
        user.setSessionId(sessionId);
        return user;
    }

    @Override
    public void sendVerificationCode(String email) {
        // 生成6位随机验证码
        String code = String.format("%06d", new Random().nextInt(1000000));

        // 发送邮件
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("验证码");
        message.setText("您的验证码是：" + code + "，有效期5分钟。");
        mailSender.send(message);

        // 保存到Redis
        redisTemplate.opsForValue().set(
                VERIFICATION_CODE_PREFIX + email,
                code,
                VERIFICATION_CODE_EXPIRE,
                TimeUnit.MINUTES
        );
    }

    @Override
    public void logout(String sessionId) {
        String key = CommonConstants.Redis.SESSION_PREFIX + sessionId;
        String userId = redisTemplate.opsForValue().get(key);
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        redisTemplate.delete(key);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(String email, String verificationCode, String newPassword) {
        // 验证验证码
        String codeKey = VERIFICATION_CODE_PREFIX + email;
        String savedCode = redisTemplate.opsForValue().get(codeKey);
        if (savedCode == null || !savedCode.equals(verificationCode)) {
            throw new BusinessException(ResultCode.VERIFICATION_CODE_ERROR);
        }

        // 查找用户
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        try {
            // 直接使用新密码加密并保存，不要使用 encryptAndFormat
            user.setPassword(HexUtil.encryptAndFormat(newPassword));
            userRepository.saveAndFlush(user);  // 使用 saveAndFlush 确保立即写入数据库
            
            // 删除验证码
            redisTemplate.delete(codeKey);
            
            // 删除该用户的所有会话
            String sessionPattern = CommonConstants.Redis.SESSION_PREFIX + "*";
            Set<String> keys = redisTemplate.keys(sessionPattern);
            if (keys != null) {
                for (String key : keys) {
                    String userId = redisTemplate.opsForValue().get(key);
                    if (userId != null && userId.equals(user.getId().toString())) {
                        redisTemplate.delete(key);
                    }
                }
            }
        } catch (Exception e) {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "密码重置失败：" + e.getMessage());
        }
    }

}