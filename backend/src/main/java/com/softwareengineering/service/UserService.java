package com.softwareengineering.service;

import com.softwareengineering.entity.User;
import com.softwareengineering.vo.UserLoginVO;
import com.softwareengineering.vo.UserRegisterVO;

public interface UserService {

    void register(UserRegisterVO registerVO);

    User login(UserLoginVO loginVO);

    void sendVerificationCode(String email);

    void logout(String sessionId);

    void resetPassword(String email, String verificationCode, String newPassword);

}