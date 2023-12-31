package com.blind.dating.dto.user;

import com.blind.dating.domain.UserAccount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "UserAccountDto", description = "유저 정보 Dto")
public class UserAccountDto {

    @Schema(description = "유저 아이디")
    private String userId;
    @Schema(description = "유저 비밀번호")
    private String userPassword;
    @Schema(description = "닉네임")
    private String nickname;
    @Schema(description = "사는 지역")
    private String region;
    @Schema(description = "MBTI")
    private String mbti;
    @Schema(description = "성별")
    private String gender;
    @Schema(description = "회원 탈퇴 여부")
    private Boolean deleted;
    @Schema(description = "자기소개")
    private String selfIntroduction;


    private UserAccountDto(String userId, String userPassword, String nickname, String region, String mbti, String gender) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.nickname = nickname;
        this.region = region;
        this.mbti = mbti;
        this.gender = gender;
    }

    public static UserAccountDto of(String userId, String userPassword, String nickname, String region, String mbti, String gender, Boolean deleted, String selfIntroduction) {
        return new UserAccountDto(userId,userPassword, nickname, region, mbti, gender, deleted, selfIntroduction);
    }

    public static UserAccountDto of(String userId, String userPassword, String nickname, String region, String mbti, String gender) {
        return new UserAccountDto(userId,userPassword, nickname, region, mbti, gender);
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
}
