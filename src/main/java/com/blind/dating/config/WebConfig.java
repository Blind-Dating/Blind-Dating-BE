package com.blind.dating.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://127.0.0.1:5500","http://52.78.66.72:8081","http://127.0.0.1:5173","https://127.0.0.1:5173","http://127.0.0.1:5174","https://fe-zeta.vercel.app") // 허용할 Origin(도메인)을 설정
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드 설정
                .allowCredentials(true)
                .maxAge(3000);
    }
}
