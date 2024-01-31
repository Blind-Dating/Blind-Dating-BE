package com.blind.dating.controller;

import com.blind.dating.config.SecurityConfig;
import com.blind.dating.domain.Interest;
import com.blind.dating.domain.Question;
import com.blind.dating.domain.Role;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.interest.InterestDto;
import com.blind.dating.dto.question.QuestionDto;
import com.blind.dating.dto.user.UserUpdateRequestDto;
import com.blind.dating.dto.user.UserWithInterestAndQuestionDto;
import com.blind.dating.repository.UserAccountRedisRepository;
import com.blind.dating.security.TokenProvider;
import com.blind.dating.service.UserService;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("UserController -테스트")
@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest extends ControllerTestConfig{

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper obj;


    @MockBean private UserService userService;
    @MockBean private TokenProvider tokenProvider;
    @MockBean private UserAccountRedisRepository userAccountRedisRepository;

    private UserAccount user;
    private Authentication authentication;
    List<InterestDto> interests;
    List<QuestionDto> questions;

    @BeforeEach
    void setUp(){
        user = new UserAccount(1L,"userId","userPassword","nickname","서울","INTP","M",false, "안녕", LocalDateTime.now(), Role.USER.getValue(),null,null,null,null,null);
//        user = UserAccount.of("qweeqw","asdfdf", "nickname","asdf","asdf","M","하이요");
        user.setInterests(List.of(new Interest()));
        user.setQuestions(List.of(new Question()));
        authentication = new UsernamePasswordAuthenticationToken("1",user.getUserPassword());
        interests = List.of(InterestDto.of(1L,"자전거 타기"),
                InterestDto.of(2L, "놀기"), InterestDto.of(3L,"게임하기"));
        questions = List.of(new QuestionDto(1L,true), new QuestionDto(2L, false),
                new QuestionDto(3L, true));
    }

    @Nested
    @DisplayName("추천 유저 조회")
    class RecommendUserList {
        @DisplayName("성공")
        @Test
        @WithMockUser(username = "1")
        public void givenUserInfo_WhenSelectUserList_ThenReturnUserList() throws Exception {
            //Given
            UserWithInterestAndQuestionDto dto1 = UserWithInterestAndQuestionDto.of(1L,"userId1","nickname1","서울","INTP","M",interests, questions,"안녕");
            UserWithInterestAndQuestionDto dto2 = UserWithInterestAndQuestionDto.of(2L,"userId2","nickname2","서울","INFP","W",interests, questions,"안녕");
            Pageable pageable = Pageable.ofSize(10);
            List<UserWithInterestAndQuestionDto> list = List.of(dto1, dto2);
            Page<UserWithInterestAndQuestionDto> pages = new PageImpl<>(list, pageable, 2);

            given(userService.getUserList(any(Authentication.class), any(Pageable.class))).willReturn(pages);

            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.get("/api/user-list")
                            .contentType(MediaType.APPLICATION_JSON)
                            .queryParam("page","0")
                            .queryParam("size","10")
                            .header("Authorization", "Bearer "+"accessToken")
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("이성 추천 받기 - 성공",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("이성 추천받기 API")
                                            .tag("User").description("이성 추천받기 API")
                                            .requestHeaders(
                                                    headerWithName("Authorization").description("Basic auth credentials")
                                            )
                                            .queryParameters(
                                                    parameterWithName("page").optional().description("페이지 번호"),
                                                    parameterWithName("size").optional().description("페이지당 추천 유저 수")
                                            ).requestFields()
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터"),
                                                    fieldWithPath("data.pageNumber").description("페이지 번호"),
                                                    fieldWithPath("data.totalPages").description("전체 페이지 개수"),
                                                    fieldWithPath("data.content").description("페이지 내 추천 유저 정보"),
                                                    fieldWithPath("data.content[].id").description("유저 유니크 번호"),
                                                    fieldWithPath("data.content[].userId").description("유저 아이디"),
                                                    fieldWithPath("data.content[].nickname").description("닉네임"),
                                                    fieldWithPath("data.content[].region").description("사는 지역"),
                                                    fieldWithPath("data.content[].mbti").description("MBTI"),
                                                    fieldWithPath("data.content[].gender").description("성별"),
                                                    fieldWithPath("data.content[].interests").description("관심사 리스트"),
                                                    fieldWithPath("data.content[].interests[].id").description("관심사 아이디"),
                                                    fieldWithPath("data.content[].interests[].interestName").description("관심사 명"),
                                                    fieldWithPath("data.content[].questions").description("질의 답변 리스트"),
                                                    fieldWithPath("data.content[].questions[].id").description("질의 답변 아이디"),
                                                    fieldWithPath("data.content[].questions[].status").description("질의 답변 상태"),
                                                    fieldWithPath("data.content[].selfIntroduction").description("자기소개")
                                            ).responseSchema(Schema.schema("이성 추천받기 성공 응답"))
                                            .build()
                            )
                    )
            );
            actions.andExpect(status().isOk());
        }
        @DisplayName("인증실패")
        @Test
        public void givenUserInfo_WhenSelectUserList_ThenUnAuthorized() throws Exception {
            //Given
            UserWithInterestAndQuestionDto dto1 = UserWithInterestAndQuestionDto.of(1L,"userId1","nickname1","서울","INTP","M",interests, questions,"안녕");
            UserWithInterestAndQuestionDto dto2 = UserWithInterestAndQuestionDto.of(2L,"userId2","nickname2","서울","INFP","W",interests, questions,"안녕");
            Pageable pageable = Pageable.ofSize(10);
            List<UserWithInterestAndQuestionDto> list = List.of(dto1, dto2);
            Page<UserWithInterestAndQuestionDto> pages = new PageImpl<>(list, pageable, 2);

            given(userService.getUserList(any(Authentication.class), any(Pageable.class))).willReturn(pages);

            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.get("/api/user-list")
                            .contentType(MediaType.APPLICATION_JSON)
                            .queryParam("page","0")
                            .queryParam("size","10")
                            .header("Authorization", "Bearer "+"accessToken")
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("이성 추천 받기 - 인증실패",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("이성 추천받기 API")
                                            .tag("User").description("이성 추천받기 API")
                                            .requestHeaders(
                                                    headerWithName("Authorization").description("Basic auth credentials")
                                            )
                                            .queryParameters(
                                                    parameterWithName("page").optional().description("페이지 번호"),
                                                    parameterWithName("size").optional().description("페이지당 추천 유저 수")
                                            ).requestFields()
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("인증 실패 응답"))
                                            .build()
                            )
                    )
            );
            actions.andExpect(status().is(401));
        }
    }
    

    @Nested
    @DisplayName("내정보 조회")
    class GetMyInfo{
        @DisplayName("성공")
        @Test
        @WithMockUser(username = "1")
        public void givenUserInfo_WhenSelectMyInfo_ThenReturnMyInfo() throws Exception {
            //Given
            UserWithInterestAndQuestionDto dto1 = UserWithInterestAndQuestionDto.of(1L,"userId1","nickname1","서울","INTP","M",interests, questions,"안녕");
            given(userService.getMyInfo(any(Authentication.class))).willReturn(dto1);

            //When && Then
            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.get("/api/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer "+"accessToken")
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("내정보 조회 - 성공",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("내정보 조회 API")
                                            .tag("User").description("내정보 조회 API")
                                            .requestFields()
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터"),
                                                    fieldWithPath("data.id").description("유저 유니크 번호"),
                                                    fieldWithPath("data.userId").description("유저 아이디"),
                                                    fieldWithPath("data.nickname").description("닉네임"),
                                                    fieldWithPath("data.region").description("사는 지역"),
                                                    fieldWithPath("data.mbti").description("MBTI"),
                                                    fieldWithPath("data.gender").description("성별"),
                                                    fieldWithPath("data.interests").description("관심사 리스트"),
                                                    fieldWithPath("data.interests[].id").description("관심사 아이디"),
                                                    fieldWithPath("data.interests[].interestName").description("관심사 명"),
                                                    fieldWithPath("data.questions").description("질의 답변 리스트"),
                                                    fieldWithPath("data.questions[].id").description("질의 답변 아이디"),
                                                    fieldWithPath("data.questions[].status").description("질의 답변 상태"),
                                                    fieldWithPath("data.selfIntroduction").description("자기소개")
                                            ).responseSchema(Schema.schema("내정보 조회 성공")).build()
                            )
                    )
            );
            actions.andExpect(status().isOk());
        }

        @DisplayName("인증 실패")
        @Test
        public void givenUserInfo_WhenSelectMyInfo_ThenReturnUnAuthorization() throws Exception {
            //Given
            UserWithInterestAndQuestionDto dto1 = UserWithInterestAndQuestionDto.of(1L,"userId1","nickname1","서울","INTP","M",interests, questions,"안녕");
            given(userService.getMyInfo(any(Authentication.class))).willReturn(dto1);

            //When && Then
            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.get("/api/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer "+"accessToken")
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("내정보 조회 - 인증실패",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("내정보 조회 API")
                                            .tag("User").description("내정보 조회 API")
                                            .requestFields()
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("인증 실패 응답")).build()
                            )
                    )
            );
            actions.andExpect(status().is(401));
        }
    }



    @Nested
    @DisplayName("내정보 수정")
    class UpdateMyInfo{
        @DisplayName("성공")
        @Test
        @WithMockUser(username = "1")
        public void givenUserInfo_WhenUpdateMyInfo_ThenReturnMyInfo() throws Exception {
            List<String> list = List.of("놀기","게임하기");
            UserUpdateRequestDto dto = new UserUpdateRequestDto("인천","ESTP",list,"안녕하세요");
            UserWithInterestAndQuestionDto dto1 = UserWithInterestAndQuestionDto.of(1L,"userId1","nickname1","서울","INTP","M",interests, questions,"안녕");

            List<Interest> list1 = List.of(new Interest(1L, user, "놀기"), new Interest(2L, user, "게임하기"));
            List<Question> list2 = List.of(new Question(1L, user, true),new Question(2L, user, false));
            user.setInterests(list1);
            user.setQuestions(list2);
            given(userService.updateMyInfo(any(Authentication.class), eq(dto))).willReturn(user);

            //When && Then
            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.put("/api/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer "+"accessToken")
                            .content(objectMapper.writeValueAsString(dto))
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("내정보 수정 - 성공",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("내정보 수정 API")
                                            .tag("User").description("내정보 수정 API")
                                            .requestFields(
                                                    fieldWithPath("region").description("사는 지역"),
                                                    fieldWithPath("mbti").description("MBTI"),
                                                    fieldWithPath("interests").description("관심사"),
                                                    fieldWithPath("selfIntroduction").description("자기소개")
                                            ).requestSchema(Schema.schema("내정보 수정 요청"))
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터"),
                                                    fieldWithPath("data.id").description("유저 유니크 번호"),
                                                    fieldWithPath("data.userId").description("유저 아이디"),
                                                    fieldWithPath("data.nickname").description("닉네임"),
                                                    fieldWithPath("data.region").description("사는 지역"),
                                                    fieldWithPath("data.mbti").description("MBTI"),
                                                    fieldWithPath("data.gender").description("성별"),
                                                    fieldWithPath("data.interests").description("관심사 리스트"),
                                                    fieldWithPath("data.interests[].id").description("관심사 아이디"),
                                                    fieldWithPath("data.interests[].interestName").description("관심사 명"),
                                                    fieldWithPath("data.questions").description("질의 답변 리스트"),
                                                    fieldWithPath("data.questions[].id").description("질의 답변 아이디"),
                                                    fieldWithPath("data.questions[].status").description("질의 답변 상태"),
                                                    fieldWithPath("data.selfIntroduction").description("자기소개")
                                            ).responseSchema(Schema.schema("내정보 수정 성공")).build()
                            )
                    )
            );
        }

        @DisplayName("성공")
        @Test
        public void givenUserInfo_WhenUpdateMyInfo_ThenReturnUnAuthorization() throws Exception {
            List<String> list = List.of("놀기","게임하기");
            UserUpdateRequestDto dto = new UserUpdateRequestDto("인천","ESTP",list,"안녕하세요");
            UserWithInterestAndQuestionDto dto1 = UserWithInterestAndQuestionDto.of(1L,"userId1","nickname1","서울","INTP","M",interests, questions,"안녕");

            List<Interest> list1 = List.of(new Interest(1L, user, "놀기"), new Interest(2L, user, "게임하기"));
            List<Question> list2 = List.of(new Question(1L, user, true),new Question(2L, user, false));
            user.setInterests(list1);
            user.setQuestions(list2);
            given(userService.updateMyInfo(any(Authentication.class), eq(dto))).willReturn(user);

            //When && Then
            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.put("/api/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer "+"accessToken")
                            .content(objectMapper.writeValueAsString(dto))
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("내정보 수정 - 인증실패",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("내정보 수정 API")
                                            .tag("User").description("내정보 수정 API")
                                            .requestFields(
                                                    fieldWithPath("region").description("사는 지역"),
                                                    fieldWithPath("mbti").description("MBTI"),
                                                    fieldWithPath("interests").description("관심사"),
                                                    fieldWithPath("selfIntroduction").description("자기소개")
                                            ).requestSchema(Schema.schema("내정보 수정 요청"))
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("인증 실패 응답")).build()
                            )
                    )
            );
        }

    }




}