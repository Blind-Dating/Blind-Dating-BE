package com.blind.dating.dto;

import jakarta.persistence.Id;
import lombok.Getter;

@Getter
public class RefreshToken {

    @Id
    private String refreshToken;

    private Long userId;

    public RefreshToken(String refreshToken, Long userId) {
        this.refreshToken = refreshToken;
        this.userId = userId;
    }

    public String getRefreshToken(){
        return refreshToken;
    }

    public Long getUserId(){
        return userId;
    }
}
