package com.blind.dating.controller;

import com.blind.dating.domain.Chat;
import com.blind.dating.domain.ChatRoom;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.chat.*;
import com.blind.dating.handler.SessionHandler;
import com.blind.dating.service.ChatService;
import com.blind.dating.service.ChattingRoomService;
import com.blind.dating.service.ReadChatService;
import com.blind.dating.service.RedisPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final SimpMessagingTemplate template; //특정 Broker로 메세지를 전달
    private final ChatService chatService;
    private final ReadChatService readChatService;
    private final ChattingRoomService chattingRoomService;
    private final SessionHandler sessionHandler;
    private final RedisPublisher redisPublisher;
    private final ChannelTopic channelTopic1;
    private final ChannelTopic channelTopic2;


    //Client가 SEND할 수 있는 경로
    //stompConfig에서 설정한 applicationDestinationPrefixes와 @MessageMapping 경로가 병합됨
    //"/pub/chat/enter"
    // roomId, userId, message,
    @MessageMapping(value = "/chat/message")
    public void message(ChatRequestDto message){

        Chat chat = chatService.saveChat(message);

        readChatService.updateReadChat(message.getChatRoomId(), chat.getId());
        // 내 채팅방 가져와서 현제 writer외의 다른 사람의 rooms를 가져와서 convert해주기
        ChatRoom chatRoom = chattingRoomService.getRoom(message.getChatRoomId()).get();

        Long userId = 0L;
        for(UserAccount userAccount: chatRoom.getUsers()){
            if(userAccount.getId() != Long.valueOf(message.getWriterId())){
                userId = userAccount.getId();
                break;
            }
        }

        List<ChatRoomDto> rooms = chattingRoomService.getRooms(userId);
        ChatListWithUserId chatListWithUserId = new ChatListWithUserId(userId, rooms);
        redisPublisher.publicRooms(channelTopic2, chatListWithUserId);
        //template.convertAndSend("/sub/chatroom/"+userId, rooms);

        //redis 구독
        ChatDto dto = ChatDto.from(chat);
        redisPublisher.publish(channelTopic1, dto);
        //template.convertAndSend("/sub/chat/room/" + message.getChatRoomId(), message);
    }

    @MessageMapping(value = "/chat/disconnect")
    public void leave(ChatRequestDto message){
        message.setMessage("상대방이 채팅방을 나가셨습니다.");
        Chat chat = chatService.saveChat(message);

        Boolean check = chattingRoomService.leaveChatRoom(message.getChatRoomId(), message.getWriterId());


        if(!check){
            ChatDto dto = ChatDto.from(chat);
            dto.setStatus(false);
            ChatRoom chatRoom = chattingRoomService.getRoom(message.getChatRoomId()).get();

            Long userId = 0L;
            for(UserAccount userAccount: chatRoom.getUsers()){
                if(userAccount.getId() != Long.valueOf(message.getWriterId())){
                    userId = userAccount.getId();
                    break;
                }
            }

            List<ChatRoomDto> rooms = chattingRoomService.getRooms(userId);
            ChatListWithUserId chatListWithUserId = new ChatListWithUserId(userId, rooms);
            redisPublisher.publicRooms(channelTopic2, chatListWithUserId);


            redisPublisher.publish(channelTopic1, dto);
        }else{
            ChatDto dto = ChatDto.from(chat);
            dto.setStatus(false);
            redisPublisher.publish(channelTopic1, dto);
        }


    }
}
