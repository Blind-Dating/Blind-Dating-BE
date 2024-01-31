package com.blind.dating.dto.user;

import com.blind.dating.domain.Interest;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.interest.InterestDto;
import com.blind.dating.dto.question.QuestionDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class LogInResponseDto {

    private Long id;
    private String userId;
    private String nickname;
    private String region;
    private String mbti;
    private String gender;
    private List<InterestDto> interests;
    private List<QuestionDto> questions;
    private String selfIntroduction;
    private String accessToken;

    private LogInResponseDto(Long id, String userId, String nickname, String region, String mbti, String gender, List<InterestDto> interests, List<QuestionDto> questions, String selfIntroduction, String accessToken) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.region = region;
        this.mbti = mbti;
        this.gender = gender;
        this.interests = interests;
        this.questions = questions;
        this.selfIntroduction = selfIntroduction;
        this.accessToken = accessToken;
    }

    public static LogInResponseDto from(LogInResponse response) {
        return new LogInResponseDto(
                response.getId(),
                response.getUserId(),
                response.getNickname(),
                response.getRegion(),
                response.getMbti(),
                response.getGender(),
                response.getInterests(),
                response.getQuestions(),
                response.getSelfIntroduction(),
                response.getAccessToken()
        );
    }
}
