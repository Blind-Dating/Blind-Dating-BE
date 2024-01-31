package com.blind.dating.controller;

import com.blind.dating.config.SecurityConfig;
import com.blind.dating.dto.user.UserReceiverId;
import com.blind.dating.security.TokenProvider;
import com.blind.dating.service.LikesUnlikesService;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;

@DisplayName("LikesUnlikesController - 테스트")
@Import(SecurityConfig.class)
@WebMvcTest(LikesUnlikesController.class)
@AutoConfigureMockMvc(addFilters = false)
class LikesUnlikesControllerTest extends ControllerTestConfig{

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean private LikesUnlikesService likesUnlikesService;
    @MockBean private TokenProvider tokenProvider;

    private UserReceiverId userReceiverId;
    @BeforeEach
    void setting() {
        userReceiverId = new UserReceiverId("1");
    }

    @Nested
    @DisplayName("좋아요 기능")
    class LikeOtherUser {
        @Test
        @DisplayName("성공")
        @WithMockUser(username = "1")
        void givenReceiverId_WhenLikeReceiver_ThenReturn200() throws Exception{
            //Given
            given(likesUnlikesService.likeUser(any(Authentication.class),anyString())).willReturn(true);

            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.post("/api/like")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userReceiverId))
                            .header("Authorization", "Bearer "+"accessToken")
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("좋아요 - 성공",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("유저 좋아요 API")
                                            .tag("Like_Unlike").description("유저 좋아요")
                                            .requestFields(
                                                    fieldWithPath("receiverId").description("상대방 유니크 번호")
                                            ).requestSchema(Schema.schema("유저 좋아요 요청"))
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("유저 좋아요 응답")).build()
                            )
                    )
            );

            actions.andExpect(status().isOk());
        }

        @Test
        @DisplayName("실패")
        void givenReceiverId_WhenLikeReceiver_ThenReturnUnauthorized() throws Exception{
            //Given
            given(likesUnlikesService.likeUser(any(Authentication.class),anyString())).willReturn(true);

            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.post("/api/like")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userReceiverId))
                            .header("Authorization", "Bearer "+"accessToken")
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("좋아요 - 실패",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("유저 좋아요 API")
                                            .tag("Like_Unlike").description("유저 좋아요")
                                            .requestFields(
                                                    fieldWithPath("receiverId").description("상대방 유니크 번호")
                                            ).requestSchema(Schema.schema("유저 좋아요 요청"))
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("유저 종하요 응답")).build()
                            )
                    )
            );

            actions.andExpect(status().isOk());
        }
    }


    @Nested
    @DisplayName("싫어요 기능")
    class UnlikeUser {
        @Test
        @DisplayName("성공")
        @WithMockUser(username = "1")
        void givenReceiverId_whenUnlikeReceiver_thenReturn200() throws Exception {

            doNothing().when(likesUnlikesService).unlikeUser(any(Authentication.class), anyString());

            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.post("/api/unlike")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userReceiverId))
                            .header("Authorization", "Bearer "+"accessToken")
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("싫어요 - 성공",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("유저 싫어요 API")
                                            .tag("Like_Unlike").description("유저 싫어요")
                                            .requestFields(
                                                    fieldWithPath("receiverId").description("상대방 유니크 번호")
                                            ).requestSchema(Schema.schema("유저 싫어요 요청"))
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("유저 싫어요 응답")).build()
                            )
                    )
            );

            actions.andExpect(status().isOk());
        }

        @Test
        @DisplayName("실패")
        void givenReceiverId_whenUnlikeReceiver_thenReturnUnauthorized() throws Exception {

            doNothing().when(likesUnlikesService).unlikeUser(any(Authentication.class), anyString());

            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.post("/api/unlike")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(userReceiverId))
                            .header("Authorization", "Bearer "+"accessToken")
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("싫어요 - 실패",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("유저 싫어요 API")
                                            .tag("Like_Unlike").description("유저 싫어요")
                                            .requestFields(
                                                    fieldWithPath("receiverId").description("상대방 유니크 번호")
                                            ).requestSchema(Schema.schema("유저 싫어요 요청"))
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("유저 싫어요 응답")).build()
                            )
                    )
            );

            actions.andExpect(status().isOk());
        }
    }


    

}