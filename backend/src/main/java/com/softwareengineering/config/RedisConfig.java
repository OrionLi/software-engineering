package com.softwareengineering.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class RedisConfig {
    // 配置都已经移到application.yml中
    // 如果后续需要自定义Redis操作，可以在这里添加
} 