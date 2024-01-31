package com.blind.dating.controller;

import com.blind.dating.config.SecurityConfig;
import com.blind.dating.domain.Chat;
import com.blind.dating.domain.ChatRoom;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.chat.ChatDto;
import com.blind.dating.dto.chat.ChatRoomDto;
import com.blind.dating.security.JwtAuthenticationFilter;
import com.blind.dating.service.ChatService;
import com.blind.dating.service.ChattingRoomService;
import com.blind.dating.service.UserService;
import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.epages.restdocs.apispec.ResourceDocumentation.*;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("ChattingRoomController - 테스트")
@WebMvcTest(ChattingRoomController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class ChattingRoomControllerTest extends ControllerTestConfig{

    @Autowired private MockMvc mvc;
    @MockBean private ChattingRoomService chattingRoomService;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean private ChatService chatService;
    @MockBean private UserService userService;

    private List<ChatRoomDto> rooms;
    private Authentication authentication;
    @BeforeEach
    void setting() {
        authentication = new UsernamePasswordAuthenticationToken("1",null);
        rooms = List.of(new ChatRoomDto(1L, LocalDateTime.now(), 2L, "nick02","안녕 이게 최근 메시지야",10L));
    }

    @Nested
    @DisplayName("채팅방 리스트 조회")
    class GetChattingRooms{
        @DisplayName("성공")
        @Test
        @WithMockUser(username = "1")
        void givenAuthentication_whenGetMessageList_thenReturnList() throws Exception{
            // Given
            given(chattingRoomService.getRooms(anyLong())).willReturn(rooms);

            mvc.perform(
                    RestDocumentationRequestBuilders.get("/api/chatroom")
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .principal(authentication)
                            .header("Authorization", "accessToken")
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("채팅방 리스트 조회 - 성공",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("채팅방 리스트 조회 API")
                                            .tag("Chatting Room Info").description("채팅방 리스트 조회")
                                            .requestFields()
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터"),
                                                    fieldWithPath("data[].roomId").description("채팅방 번호"),
                                                    fieldWithPath("data[].updatedAt").description("최근 수정 일"),
                                                    fieldWithPath("data[].otherUserId").description("채팅방 내 다른 사람의 유니크 아이디"),
                                                    fieldWithPath("data[].otherUserNickname").description("채팅방 내 다른 사람의 닉네임"),
                                                    fieldWithPath("data[].recentMessage").description("채팅방 내 최근 채팅"),
                                                    fieldWithPath("data[].unReadCount").description("채팅방 내 읽지않은 채팅 개수")
                                            ).responseSchema(Schema.schema("채팅방 리스트 응답")).build()
                            )
                    )
            ).andExpect(status().isOk());
        }
    }


    @Nested
    @DisplayName("메시지 리스트 조회")
    class GetChatList{
        @DisplayName("채팅방 메시지 리스트 조회시 채팅방 조회 실패예외 - 테스트")
        @Test
        @WithMockUser(username = "1")
        void givenRoomIdAndChatId_whenEnterRoom_thenThrowException() throws Exception{
            Authentication authentication = new UsernamePasswordAuthenticationToken("1",null);
            given(chattingRoomService.getRoom(anyString())).willThrow(new RuntimeException("채팅목록 조회시 예외가 발생했습니다."));

            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.get("/api/chatroom/{roomId}","1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .queryParam("page","0")
                            .queryParam("size","30")
                            .queryParam("chatId","2")
                            .header("Authorization", "Bearer "+"accessToken")
                            .principal(authentication)
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("채팅 리스트 조회 - 실패",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("채팅 리스트 조회 API")
                                            .tag("Chatting Room Info").description("채팅 리스트 조회 - 실패")
                                            .requestHeaders(
                                                    headerWithName("Authorization").description("Basic auth credentials")
                                            )
                                            .queryParameters(
                                                    parameterWithName("page").optional().description("페이지 번호"),
                                                    parameterWithName("size").optional().description("페이지당 추천 유저 수"),
                                                    parameterWithName("chatId").optional().description("채팅 번호")
                                            ).requestFields()
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터")
                                            ).responseSchema(Schema.schema("채팅 리스트 조회 실패 응답"))
                                            .build()
                            )
                    )
            ).andExpect(status().isBadRequest());
        }
        @DisplayName("채팅방 메시지 리스트 조회 - 테스트")
        @Test
        @WithMockUser(username = "1")
        void givenRoomIdAndChatId_whenEnterRoom_thenReturnChatList() throws Exception {
            UserAccount user1 = new UserAccount(1L, "user01", "pass01", "nick01","서울","intp","M", false, "안녕", LocalDateTime.now(), null, "kakao",null,null,null,null);
            UserAccount user2 = new UserAccount(2L, "user02", "pass02", "nick02","서울","intp","W", false, "안녕", LocalDateTime.now(), null, "kakao",null,null,null,null);
            ChatRoom chatRoom = new ChatRoom(1L, Set.of(user1, user2), null, true, "message");
            List<Chat> list = List.of(new Chat(1L, chatRoom, 1L, "message"), new Chat(2L, chatRoom, 2L, "message2"));

            given(chattingRoomService.getRoom(anyString())).willReturn(Optional.of(chatRoom));
            given(chatService.selectChatList(any(ChatRoom.class), anyString())).willReturn(list);
            
            ResultActions actions = mvc.perform(
                    RestDocumentationRequestBuilders.get("/api/chatroom/{roomId}","1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .queryParam("page","0")
                            .queryParam("size","30")
                            .queryParam("chatId","0")
                            .header("Authorization", "Bearer "+"accessToken")
                            .principal(authentication)
            ).andDo(
                    MockMvcRestDocumentationWrapper.document("채팅 리스트 조회 - 성공",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .description("채팅 리스트 조회 API")
                                            .tag("Chatting Room Info").description("채팅 리스트 조회 - 성공")
                                            .requestHeaders(
                                                    headerWithName("Authorization").description("Basic auth credentials")
                                            )
                                            .pathParameters(
                                                    parameterWithName("roomId").description("채팅방 번호")
                                            )
                                            .queryParameters(
                                                    parameterWithName("page").optional().description("페이지 번호"),
                                                    parameterWithName("size").optional().description("페이지당 추천 유저 수"),
                                                    parameterWithName("chatId").optional().description("채팅 번호")
                                            ).requestFields()
                                            .responseFields(
                                                    fieldWithPath("code").description("응답 코드"),
                                                    fieldWithPath("status").description("응답 상태"),
                                                    fieldWithPath("message").description("응답 메시지"),
                                                    fieldWithPath("data").description("응답 데이터"),
                                                    fieldWithPath("data.otherUserId").description("상대방 유니크 아이디"),
                                                    fieldWithPath("data.otherUserNickname").description("상대방 닉네임"),
                                                    fieldWithPath("data.roomStatus").description("채팅방 상태"),
                                                    fieldWithPath("data.chatList[].id").description("채팅 아이디"),
                                                    fieldWithPath("data.chatList[].writerId").description("작성자 유니크 아니디"),
                                                    fieldWithPath("data.chatList[].message").description("메시지"),
                                                    fieldWithPath("data.chatList[].createdAt").description("메시지 생성일"),
                                                    fieldWithPath("data.chatList[].updatedAt").description("메시지 수정일"),
                                                    fieldWithPath("data.chatList[].chatRoomId").description("채팅방 아이디"),
                                                    fieldWithPath("data.chatList[].status").description("채팅 상태")
                                            ).responseSchema(Schema.schema("채팅 리스트 조회 성공 응답"))
                                            .build()
                            )
                    )
            ).andExpect(status().isOk());
        }
    }







}