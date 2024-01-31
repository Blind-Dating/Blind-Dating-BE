package com.blind.dating.service;

import com.blind.dating.domain.Interest;
import com.blind.dating.domain.Question;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.user.UserIdWithNicknameAndGender;
import com.blind.dating.dto.user.UserUpdateRequestDto;
import com.blind.dating.dto.user.UserWithInterestAndQuestionDto;
import com.blind.dating.repository.InterestRepository;
import com.blind.dating.repository.UserAccountRedisRepository;
import com.blind.dating.repository.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


@DisplayName("UserService 테스트")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks private UserService userService;
    @Mock private UserAccountRepository userAccountRepository;
    @Mock private InterestRepository interestRepository;

    @Mock private UserAccountRedisRepository userAccountRedisRepository;

    private UserAccount user;
    private UserAccount user2;
    private Authentication authentication;

    @BeforeEach
    void setUp(){
        user = UserAccount.of("user01","pass01", "nickname1","서울","intp","M","하이요");
        user.setInterests(List.of(new Interest()));
        user.setQuestions(List.of(new Question()));
        user2 = UserAccount.of("user02","pass02", "nickname2","서울","intp","W","하이요");
        user2.setInterests(List.of(new Interest()));
        user2.setQuestions(List.of(new Question()));
        authentication = new UsernamePasswordAuthenticationToken("1",user.getUserPassword());
    }


    @DisplayName("남성 추천 리스트 - 테스트")
    @Test
    @WithMockUser(username = "1")
    void givenRequireData_whenSelectWomanList_thenReturnUserList(){
        //Given
        Pageable pageable = Pageable.ofSize(10);
        List<UserAccount> list = List.of(user2);
        Page<UserAccount> pages = new PageImpl<>(list, pageable, 10);

        UserIdWithNicknameAndGender userInfo = new UserIdWithNicknameAndGender(1L,"fd","M");

        given(userAccountRepository.findById(1L)).willReturn(Optional.of(user));
        given(userAccountRepository.recommendUser("W",1L, pageable)).willReturn(pages);

        //When
        Page<UserWithInterestAndQuestionDto> result = userService.getUserList(authentication, pageable);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalPages()).isEqualTo(1);

    }

    @DisplayName("여성 추천 리스트 - 테스트")
    @Test
    @WithMockUser(username = "1")
    void givenRequireData_whenSelectManList_thenReturnUserList(){
        //Given
        Pageable pageable = Pageable.ofSize(10);
        List<UserAccount> list = List.of(user);
        Page<UserAccount> pages = new PageImpl<>(list, pageable, 10);

        UserIdWithNicknameAndGender userInfo = new UserIdWithNicknameAndGender(2L,"nickname2","W");

        given(userAccountRepository.findById(anyLong())).willReturn(Optional.of(user2));
        given(userAccountRepository.recommendUser("M",1L, pageable)).willReturn(pages);

        //When
        Page<UserWithInterestAndQuestionDto> result = userService.getUserList(authentication, pageable);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalPages()).isEqualTo(1);

    }

    @DisplayName("이성 추천 리스트중 예외 - 테스트")
    @Test
    @WithMockUser(username = "1")
    void givenRequireData_whenSelectManList_thenThrowException(){
        //Given
        given(userAccountRepository.findById(anyLong())).willReturn(Optional.empty());
        //When
        RuntimeException exception = assertThrows(RuntimeException.class, ()-> {
            userService.getUserList(authentication, Pageable.ofSize(2));
        });

        //Then
        assertEquals(exception.getMessage(),"예외 발생");

    }

    @DisplayName("내정보 조회 - 테스트")
    @Test
    @WithMockUser(username = "1")
    void givenUser_whenGetMyInfo_thenReturnUserInfo(){
        //Given
        given(userAccountRepository.findById(1L)).willReturn(Optional.of(user));

        //When
        UserWithInterestAndQuestionDto result = userService.getMyInfo(authentication);

        //Then
        assertThat(result).isNotNull();
        assertThat(result).hasFieldOrPropertyWithValue("userId","user01");
    }

    @DisplayName("내정보 조회 - 테스트")
    @Test
    @WithMockUser(username = "1")
    void givenUser_whenGetMyInfo_thenThrowException(){
        //Given
        given(userAccountRepository.findById(1L)).willReturn(Optional.empty());

        //When
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getMyInfo(authentication);
        });

        //Then
        assertThat(exception.getMessage()).isEqualTo("내정보 조회에 실패했습니다.");
    }



    @DisplayName("내 정보 수정하기 - 테스트")
    @Test
    @WithMockUser(username = "1")
    void givenUser_whenUpdateMyInfo_thenReturnUserInfo(){
        //Given
        Optional<UserAccount> opUser = Optional.of(user);
        List<String> list = new ArrayList<>();
        list.add("놀기");
        list.add("피하기");
        UserUpdateRequestDto dto = UserUpdateRequestDto.builder()
                .region("인천")
                .mbti("MBTI")
                .selfIntroduction("하이요")
                .interests(list).build();

        given(userAccountRepository.findById(1L)).willReturn(opUser);

        //When
        UserAccount result = userService.updateMyInfo(authentication, dto);

        //Then
        assertThat(result).isNotNull();
        assertThat(result).hasFieldOrPropertyWithValue("userId","user01");
    }

    @DisplayName("내 정보 수정중에 예외 발생 - 테스트")
    @Test
    @WithMockUser(username = "1")
    void givenUser_whenUpdateMyInfo_thenThrowException(){
        //Given
        given(userAccountRepository.findById(1L)).willReturn(Optional.empty());
        UserUpdateRequestDto dto = new UserUpdateRequestDto();

        //When
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            UserAccount result = userService.updateMyInfo(authentication, dto);
        });

        //Then
        assertThat(exception.getMessage()).isEqualTo("유저정보 조회중에 예외가 발생했습니다.");
    }


    @DisplayName("특정 유저 조회하기 - 테스트")
    @Test
    @WithMockUser(username = "1")
    void givenUserId_whenSelectUserAccount_thenReturnUserAccount(){
        //Given
        Optional<UserAccount> opUser = Optional.of(user);
        given(userAccountRepository.findById(1L)).willReturn(opUser);

        //When
        Optional<UserAccount> result = userService.getUser(1L);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.get()).hasFieldOrPropertyWithValue("userId","user01");
    }
}
