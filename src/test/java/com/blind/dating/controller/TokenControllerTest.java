package com.blind.dating.controller;

import com.blind.dating.dto.user.UserInfoWithTokens;
import com.blind.dating.security.TokenProvider;
import com.blind.dating.service.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("TokenController - 테스트")
@WebMvcTest(TokenController.class)
class TokenControllerTest {

    @Autowired MockMvc mvc;
    @MockBean private TokenService tokenService;
    @MockBean private TokenProvider tokenProvider;

    @DisplayName("리프래시 토큰 재발급")
    @Test
    @WithMockUser(username = "1")
    void givenRefreshToken_whenRegenerateRefreshToken_thenReturnRefreshToken() throws Exception {
        //Given
        UserInfoWithTokens dto = new UserInfoWithTokens("access","refreshToken",1L,"nick");
        Cookie cookie = new Cookie("refreshToken","refreshToken");
        String userId = "1";

        given(tokenService.validRefreshToken(cookie)).willReturn(userId);
        given(tokenService.updateRefreshToken(userId)).willReturn(dto);

        //When & Then
        mvc.perform(get("/api/refresh")
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("accessToken 이 성공적으로 생성되었습니다."))
                .andExpect(jsonPath("$.data.id").value(dto.getId()))
                .andExpect(jsonPath("$.data.accessToken").value(dto.getAccessToken()))
                .andExpect(jsonPath("$.data.nickname").value(dto.getNickname()));

        then(tokenService).should().validRefreshToken(cookie);
        then(tokenService).should().updateRefreshToken(userId);

    }
}