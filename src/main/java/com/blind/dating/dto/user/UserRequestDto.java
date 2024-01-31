package com.blind.dating.dto.user;

import com.blind.dating.domain.Role;
import com.blind.dating.domain.UserAccount;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class UserRequestDto {

    @NotNull(message = "아이디는 필수 입력 값입니다.")
    @Pattern(regexp = "^[A-Za-z0-9가-힣]{5,20}$", message = "아이디는 문자, 숫자, 한글만 가능하며, 길이는 5~20사이만 가능합니다.")
    private String userId;

    @NotNull(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(regexp = "^[A-Za-z0-9가-힣]{8,20}$", message = "패스워드는 문자, 숫자, 한글만 가능하며, 길이는 8~20사이만 가능합니다.")
    private String userPassword;

    @NotNull(message = "닉네임은 필수 입력 값입니다.")
    @Pattern(regexp = "^[A-Za-z0-9가-힣]{3,20}$", message = "닉네임은 문자, 숫자, 한글만 가능하며, 길이는 3~20사이만 가능합니다.")
    private String nickname;

    @NotNull(message = "지역은 필수 입력 값입니다.")
    private String region;

    @NotNull(message = "MBTI는 필수 입력 값입니다.")
    private String mbti;

    @NotNull(message = "성별은 필수 입력 값입니다.")
    private String gender;

    @NotNull(message = "관심사는 필수 입력 값입니다.")
    private List<String> interests;

    @NotNull(message = "답변은 필수 입력 값입니다.")
    private List<Boolean> questions;

    @NotNull(message = "자기소개는 필수 입력 값입니다.")
    private String selfIntroduction;

    private UserRequestDto(String userId, String userPassword, String nickname, String region, String mbti, String gender, String selfIntroduction) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.nickname = nickname;
        this.region = region;
        this.mbti = mbti;
        this.gender = gender;
        this.selfIntroduction = selfIntroduction;
    }

    public static UserRequestDto of(String userId, String userPassword, String nickname, String region, String mbti, String gender, String selfIntroduction) {
        return new UserRequestDto(userId,userPassword, nickname, region, mbti,gender, selfIntroduction);
    }

    public static UserAccountDto from(UserAccount entity){
        return new UserAccountDto(
                entity.getUserId(),
                entity.getUserPassword(),
                entity.getNickname(),
                entity.getRegion(),
                entity.getMbti(),
                entity.getGender(),
                entity.getDeleted(),
                entity.getSelfIntroduction()
        );
    }

    public UserAccount toEntity(){
        return UserAccount.of(
                userId,
                userPassword,
                nickname,
                region,
                mbti,
                gender,
                selfIntroduction
        );
    }

    public UserAccount toRegisterEntity(String password){
        return UserAccount.of(
                userId,
                password,
                nickname,
                region,
                mbti,
                gender,
                false,
                selfIntroduction,
                LocalDateTime.now(),
                Role.USER.getValue()
        );
    }

}
