package com.blind.dating.service;

import com.blind.dating.domain.Interest;
import com.blind.dating.domain.Question;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.user.LogInResponse;
import com.blind.dating.dto.user.UserIdWithNicknameAndGender;
import com.blind.dating.dto.user.UserInfoWithTokens;
import com.blind.dating.dto.user.UserRequestDto;
import com.blind.dating.repository.RefreshTokenRepository;
import com.blind.dating.repository.UserAccountRedisRepository;
import com.blind.dating.repository.UserAccountRepository;
import com.blind.dating.security.TokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@DisplayName("유저 조회 서비스")
@ExtendWith(MockitoExtension.class)
class UserAccountServiceTest {

    @InjectMocks private UserAccountService userAccountService;
    @Mock private UserAccountRepository userAccountRepository;
    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock private TokenProvider tokenProvider;
    @Mock private UserAccountRedisRepository userAccountRedisRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private QuestionService questionService;
    @Mock private InterestService interestService;

    private UserRequestDto dto;
    private UserAccount user;

    @BeforeEach
    void setting(){
        dto = UserRequestDto.of("userId","userPass01","userNickname","서울","INFP","M","안녕하세요");
        dto.setInterests(List.of("자전거타기","놀기","게임하기"));
        dto.setQuestions(List.of(true, false, true));
        user = dto.toEntity();
        user.setRecentLogin(LocalDateTime.now());
        user.setDeleted(false);
        user.setUserPassword(bCryptPasswordEncoder.encode(dto.getUserPassword()));
    }



    @DisplayName("회원가입 테스트")
    @Test
    void givenUserRequestDto_whenRegister_thenRegisterSuccess(){
        //given
        String accessToken = "asdffqwerqwerdfgscvASDF";
        String password = "hashPass";
        List<Question> list = List.of();
        List<Interest> list1 = List.of();

        given(userAccountRepository.existsByUserId(dto.getUserId())).willReturn(false);// 존재 x
        given(bCryptPasswordEncoder.encode(dto.getUserPassword())).willReturn(password);
        given(userAccountRepository.save(user)).willReturn(user);
        given(questionService.saveQuestions(user, dto.getQuestions())).willReturn(list);
        given(interestService.saveInterest(user, dto.getInterests())).willReturn(list1);


        //when
        UserAccount info = userAccountService.register(dto);

        //then
        assertThat(info).isNotNull()
                .hasFieldOrPropertyWithValue("id",info.getId());

    }



    @DisplayName("로그인 서비스 - 성공 테스트")
    @Test
    void givenLoginInfo_whenLogin_thenReturnLogInResponseSuccess(){
        //given
        String userId = "userId";
        String password = "userPass01";
        String accessToken = "asdffasd";

        LogInResponse response = LogInResponse.from(user);

       given(userAccountRepository.findByUserId(userId)).willReturn(user);
       // When
        LogInResponse user1 = userAccountService.getLoginInfo(userId, password);

        assertThat(user1.getUserId()).isEqualTo(userId);

    }



    @DisplayName("유저아이디 확인 테스트 - 아이디 존재")
    @Test
    void givenUserId_whenCheckUserId_thenReturnTrue(){
        //given
        String userId = "userId";
        UserAccount user = UserAccount.of(userId,"asdfdf", "nick1","asdf","asdf","M","하이요");
        given(userAccountRepository.findByUserId(userId)).willReturn(user);

        //when
        boolean result = userAccountService.checkUserId(userId);

        //then
        assertThat(result).isTrue();

    }

    @DisplayName("유저아이디 확인 테스트 - 아이디 없음.")
    @Test
    void givenUserId_whenCheckUserId_thenReturnFalse(){
        //given
        String userId = "userId";
        UserAccount user = UserAccount.of(userId,"asdfdf", "nick1","asdf","asdf","M","하이요");
        given(userAccountRepository.findByUserId(userId)).willReturn(null);

        //when
        boolean result = userAccountService.checkUserId(userId);

        //then
        assertThat(result).isFalse();

    }

    @DisplayName("유저 닉네임 확인 테스트 - 닉네임 없음.")
    @Test
    void givenUserNickname_whenCheckUserNickname_thenReturnFalse(){
        //given
        String userNickname = "nick11";
        UserAccount user = UserAccount.of("qweeqw","asdfdf", userNickname,"asdf","asdf","M","하이요");
        given(userAccountRepository.findByNickname(userNickname)).willReturn(null);

        //when
        boolean result = userAccountService.checkNickname(userNickname);

        //then
        assertThat(result).isFalse();

    }
    @DisplayName("유저 닉네임 확인 테스트 - 닉네임 없음.")
    @Test
    void givenUserNickname_whenCheckUserNickname_thenReturnTrue(){
        //given
        String userNickname = "nick11";
        UserAccount user = UserAccount.of("qweeqw","asdfdf", userNickname,"asdf","asdf","M","하이요");
        given(userAccountRepository.findByNickname(userNickname)).willReturn(user);

        //when
        boolean result = userAccountService.checkNickname(userNickname);

        //then
        assertThat(result).isTrue();

    }

}