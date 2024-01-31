package com.blind.dating.controller;

import com.blind.dating.common.code.ChattingRoomResponseCode;
import com.blind.dating.domain.ChatRoom;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.chat.ChatDto;
import com.blind.dating.dto.chat.ChatListWithOtherUserInfo;
import com.blind.dating.dto.chat.ChatRoomDto;
import com.blind.dating.common.Api;
import com.blind.dating.service.ChatService;
import com.blind.dating.service.ChattingRoomService;
import com.blind.dating.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChattingRoomController {

    private final ChattingRoomService chattingRoomService;
    private final ChatService chatService;
    private final UserService userService;

    @GetMapping("/chatroom")
    public ResponseEntity<Api<List<ChatRoomDto>>> getMyMessageList(
            Authentication authentication
    ){
        String userId = (String) authentication.getPrincipal();
        //나에게 생성된 채팅룸 조회
        List<ChatRoomDto> rooms = chattingRoomService.getRooms(Long.valueOf(userId));

        return ResponseEntity.ok()
                .body(Api.OK(ChattingRoomResponseCode.GET_ROOMS_SUCCESS, rooms));
    }

    @GetMapping("/chatroom/{roomId}")
    public ResponseEntity<Api<ChatListWithOtherUserInfo>> enterRoom(
            @PathVariable String roomId,
            @RequestParam String chatId,
            @PageableDefault(size = 30, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication
    ){
        Long userId = Long.valueOf((String)authentication.getPrincipal());

        ChatRoom chatRoom = chattingRoomService.getRoom(roomId)
                .orElseThrow(() -> new RuntimeException("채팅목록 조회시 예외가 발생했습니다."));

        //다른 유저 정보 조회하기.
        UserAccount otherUser = chatRoom.getUsers().stream().filter(
               it -> it.getId() != userId
        ).toList().get(0);

        //존재할경우 채팅 메세지 30개를 id 오름차순으로 가져오기
        List<ChatDto> chatList = chatService.selectChatList(chatRoom, chatId)
                .stream().map(ChatDto::from).limit(30).collect(Collectors.toList());
        ChatListWithOtherUserInfo chatListWithOtherUserInfo = ChatListWithOtherUserInfo.of(otherUser.getId(), otherUser.getNickname(),chatRoom.getStatus(), chatList);

        return ResponseEntity.ok()
                .body(Api.OK(ChattingRoomResponseCode.GET_CHATS_SUCCESS, chatListWithOtherUserInfo));
    }
}
