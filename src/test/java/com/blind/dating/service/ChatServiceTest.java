package com.blind.dating.service;

import com.blind.dating.domain.Chat;
import com.blind.dating.domain.ChatRoom;
import com.blind.dating.domain.ReadChat;
import com.blind.dating.dto.chat.ChatRequestDto;
import com.blind.dating.repository.ChatRepository;
import com.blind.dating.repository.ChattingRoomRepository;
import com.blind.dating.repository.ReadChatRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@DisplayName("ChatService - 테스트")
@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock private ChatRepository chatRepository;
    @Mock private ChattingRoomRepository chattingRoomRepository;
    @Mock private ReadChatRepository readChatRepository;
    @InjectMocks private ChatService chatService;

    @DisplayName("챗 리스트 조회시 chatId = 0일때 - 테스트")
    @Test
    void givenRoomIdAndChatId0_whenSelectChatList_thenReturnChatList(){
        Chat chat = new Chat();
        List<Chat> list = List.of(chat);
        ChatRoom chatRoom = new ChatRoom();

        given(chatRepository.findAllByChatRoomOrderByIdDesc(chatRoom)).willReturn(List.of());

        List<Chat> result = chatService.selectChatList(chatRoom, "0");

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @DisplayName("챗 리스트 조회시 chatId != 0일때 - 테스트")
    @Test
    void givenRoomIdAndChatId_whenSelectChatList_thenReturnChatList(){
        Chat chat = new Chat();
        List<Chat> list = List.of(chat);
        ChatRoom chatRoom = new ChatRoom();

        given(chatRepository.findByChatRoomAndIdLessThanEqualOrderByIdDesc(chatRoom,1L)).willReturn(list);

        List<Chat> result = chatService.selectChatList(chatRoom,"1");

        assertThat(result).isNotNull();
    }


    @DisplayName("챗 저장 - 테스트")
    @Test
    void givenChatRequestDto_whenSaveChat_thenReturnChat(){
        ChatRequestDto dto = new ChatRequestDto("1","1","hi");
        ChatRoom chatRoom = new ChatRoom();
        Chat chat = new Chat(chatRoom, 1L,"hi");
        Optional<ChatRoom> optional = Optional.of(chatRoom);
        given(chattingRoomRepository.findById(1L)).willReturn(optional);
        given(chatRepository.save(any(Chat.class))).willReturn(chat);

        Chat result = chatService.saveChat(dto);

        assertThat(result).isNotNull();
    }



    @DisplayName("ReadChat의 chatId가 0일 때 안읽은 채팅 개수 조회하기")
    @Test
    void givenUserIdAndRoomId_whenUpdateUnReadChatAndReadChatChatId0_thenReturnSize(){
        //Given
        Chat chat = new Chat();
        List<Chat> list = List.of(chat);
        Long listSize = (long) list.size();
        ChatRoom chatRoom = new ChatRoom();
        ReadChat readChat = ReadChat.of(chatRoom,1L,0L);
        Optional<ReadChat> optional = Optional.of(readChat);

        given(readChatRepository.findByChatRoomAndUserId(any(ChatRoom.class),anyLong())).willReturn(optional);

        //When
        Long result = chatService.unreadChat(1L,chatRoom);

        //Then
        assertThat(result).isNotNull();
    }

    @DisplayName("ReadChat의 chatId가 0이상일 때 안읽은 채팅 개수 조회하기")
    @Test
    void givenUserIdAndRoomId_whenUpdateUnReadChatReadChatChatIdNot0_thenReturnSize2(){
        //Given
        Set<Chat> chats = Set.of(new Chat(1L, new ChatRoom(), 1L, "message"));
        List<Chat> list = List.of(new Chat(2L, null, 1L, null), new Chat(), new Chat(), new Chat());
        ChatRoom chatRoom = new ChatRoom(1L, null, null,false, "message",chats);
        ReadChat readChat = ReadChat.of(chatRoom,1L,1L);
        Optional<ReadChat> optional = Optional.of(readChat);

        given(chatRepository.findAllByChatRoomOrderByIdDesc(chatRoom)).willReturn(list);
        given(readChatRepository.findByChatRoomAndUserId(any(ChatRoom.class),anyLong())).willReturn(optional);
        given(chatRepository.countByIdBetween(1L, 2L)).willReturn(2L);

        //When
        Long result = chatService.unreadChat(1L,chatRoom);

        //Then
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(1L);
    }

    @DisplayName("안읽은 채팅 조회시 예외 발생 - 테스트")
    @Test
    void givenUserIdAndRoomId_whenUpdateUnReadChat_thenThrowException(){
        //Given
        Set<Chat> chats = Set.of(new Chat(1L, new ChatRoom(), 1L, "message"));
        List<Chat> list = List.of(new Chat(2L, null, 1L, null), new Chat(), new Chat(), new Chat());
        ChatRoom chatRoom = new ChatRoom(1L, null, null,false, "message", chats);
        ReadChat readChat = ReadChat.of(chatRoom,1L,1L);
        Optional<ReadChat> optional = Optional.of(readChat);

        given(chatRepository.findAllByChatRoomOrderByIdDesc(chatRoom)).willReturn(list);
        given(readChatRepository.findByChatRoomAndUserId(chatRoom, 1L)).willReturn(Optional.empty());

        //When
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            Long result = chatService.unreadChat(1L,chatRoom);
        });

        //Then
        assertThat(exception.getMessage()).isEqualTo("안읽은 메세지 개수 반환 중에 예외 발생");
    }

}