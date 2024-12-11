package com.softwareengineering.controller;

import com.softwareengineering.common.Result;
import com.softwareengineering.entity.User;
import com.softwareengineering.service.UserService;
import com.softwareengineering.vo.UserLoginVO;
import com.softwareengineering.vo.UserRegisterVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;

@Validated
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public Result<Void> register(@RequestBody @Valid UserRegisterVO registerVO) {
        userService.register(registerVO);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<User> login(@RequestBody @Valid UserLoginVO loginVO) {
        return Result.success(userService.login(loginVO));
    }

    @GetMapping("/verification-code")
    public Result<Void> sendVerificationCode(@RequestParam @Email String email) {
        userService.sendVerificationCode(email);
        return Result.success();
    }

    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("X-Session-Id") String sessionId) {
        userService.logout(sessionId);
        return Result.success();
    }

}