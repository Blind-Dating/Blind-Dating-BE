package com.blind.dating.controller;

import com.blind.dating.dto.response.ResponseDto;
import com.blind.dating.dto.user.*;
import com.blind.dating.service.TokenService;
import com.blind.dating.util.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Tag(name = "Token Info", description = "토큰 재발급 서비스")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class TokenController {

    private final TokenService tokenService;
    private final CookieUtil cookieUtil;

    @GetMapping ("/refresh")
    @Operation(summary = "AccessToken 재발급", description = "RefreshToken 으로 AccessToken 을 재발급 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    public ResponseEntity<ResponseDto> newAccessToken(
            @CookieValue(name = "refreshToken") Cookie cookie,
            HttpServletResponse response
    ){

        //리프레쉬 토큰이 해당 유저에 저장된 데이터가 맞는지 확인
        String userId = tokenService.validRefreshToken(cookie);

        if(userId == null){
            //쿠키 삭제해라.
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            return ResponseEntity.<ResponseDto>badRequest()
                    .body(ResponseDto.builder().status("Bad Request")
                            .message("토큰 정보가 일치하지 않습니다.")
                            .data(null)
                            .build());
        }

        // 맞는지 확인하고 있으면 리프레쉬 토큰 업데이트 후 access, refresh 반환하기.
        LogInResponse response1 = tokenService.updateRefreshToken(userId);
        LogInResponseDto dto = LogInResponseDto.from(response1);
        dto.setAccessToken(response1.getAccessToken());
        cookie.setMaxAge(0);

        cookieUtil.addCookie(response, "refreshToken", response1.getRefreshToken());

        return ResponseEntity.<ResponseDto<LogInResponseDto>>ok()
                .body(ResponseDto.<LogInResponseDto>builder()
                        .status("OK")
                        .message("accessToken 이 성공적으로 생성되었습니다.")
                        .data(dto)
                        .build());


    }
}
