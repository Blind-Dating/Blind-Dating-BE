package com.blind.dating.controller;

import com.blind.dating.common.Api;
import com.blind.dating.common.code.TokenResponseCode;
import com.blind.dating.dto.user.*;
import com.blind.dating.service.TokenService;
import com.blind.dating.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class TokenController {

    private final TokenService tokenService;

    @GetMapping ("/refresh")
    public ResponseEntity<Api> newAccessToken(
            @CookieValue(name = "refreshToken") Cookie cookie,
            HttpServletResponse response
    ){

        String userId = tokenService.validRefreshToken(cookie);

        // 맞는지 확인하고 있으면 리프레쉬 토큰 업데이트 후 access, refresh 반환하기.
        LogInResponse logInResponse = tokenService.updateRefreshToken(userId);
        LogInResponseDto dto = LogInResponseDto.from(logInResponse);

        cookie.setMaxAge(0);
        CookieUtil.addCookie(response, "refreshToken", logInResponse.getRefreshToken());

        return ResponseEntity.ok()
                .body(Api.OK(TokenResponseCode.ISSUE_ACCESS_TOKEN_SUCCESS, dto));
    }
}
