package com.blind.dating.service;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.user.LogInResponse;
import com.blind.dating.dto.user.TokenDto;
import com.blind.dating.dto.user.UserIdRequestDto;
import com.blind.dating.dto.user.UserInfoWithTokens;
import com.blind.dating.repository.RefreshTokenRepository;
import com.blind.dating.repository.UserAccountRepository;
import com.blind.dating.security.TokenProvider;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
public class TokenService {

    private final UserAccountRepository userAccountRepository;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;


    /**
     * 리프레쉬 토큰 재발급 서비스
     * @param cookie
     * @return String
     */
    public String validRefreshToken(Cookie cookie){
        String userId = tokenProvider.validateAndGetUserId(cookie.getValue());
        String refreshToken = refreshTokenRepository.getRefreshToken(userId);

        if(refreshToken == null) throw new RuntimeException("조회된 리프레쉬 토큰이 없습니다.");
        return userId;
    }

    /**
     * 리프래쉬 토큰을 새로운 리프래쉬 토큰으로 업데이트
     * @param userId
     * @return LoginResponse.class
     */
    @Transactional
    public LogInResponse updateRefreshToken(String userId){
        UserAccount user = userAccountRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new RuntimeException(userId+"에 해당하는 유저는 존재하지 않습니다."));

        String accessToken = tokenProvider.create(user);
        String refreshToken = tokenProvider.refreshToken(user);
        refreshTokenRepository.save(userId, refreshToken);

        return LogInResponse.from(user, accessToken, refreshToken);
    }


}
