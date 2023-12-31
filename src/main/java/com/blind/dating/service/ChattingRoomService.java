package com.blind.dating.service;

import com.blind.dating.domain.ChatRoom;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.chat.ChatRoomDto;
import com.blind.dating.repository.ChattingRoomRepository;
import com.blind.dating.repository.UserAccountRepository;
import com.blind.dating.repository.querydsl.ChattingRoomRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChattingRoomService {

    private final ChattingRoomRepository chatRoomRepository;
    private final ChattingRoomRepositoryImpl chattingRoomRepositoryImpl;
    private final ChatService chatService;
    private final UserAccountRepository userAccountRepository;

    public ChatRoom create(UserAccount user1, UserAccount user2){
       ChatRoom room = new ChatRoom();
       room.getUsers().add(user1);
       room.getUsers().add(user2);

        return chatRoomRepository.save(room);

    }

    /**
     * 현재 내가 참여하고 있는 채팅방 조회 서비스
     * @param userId
     * @return List<ChatRoomDto></ChatRoomDto>
     */
    @Transactional
    public List<ChatRoomDto> getRooms(Long userId){

        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("채팅방 조회시 내 정보가 제대로 조회되지 않았습니다. 다시 요청해 주세요"));

        List<ChatRoom> chatRooms = chatRoomRepository.findAllByUsersAndStatusOrderByUpdatedAtDesc(user, true);


        List<ChatRoomDto> rooms = chatRooms.stream().map(room -> {
            ChatRoomDto dto = new ChatRoomDto();

            Long otherId = 0L;
            // 조회를 위해 상대방 유저 정보 찾은 후 user2에 저장하기
            Set<UserAccount> users = room.getUsers();



            for(UserAccount userAccount: users){
                if(userAccount.getId() != userId){
                    otherId = userAccount.getId();
                    break;
                }
            }

            // 채팅방에서 읽지 않은 채팅 개수 조회
            Long unReadCount = chatService.unreadChat(userId, room);
            // 상대방 정보 조회하기

            Optional<UserAccount> other = userAccountRepository.findById(otherId);
            if(other.isPresent()){
                dto.setOtherUserid(other.get().getId());
                dto.setOtherUserNickname(other.get().getNickname());
            }

            // 응답 데이터를 보여주기 위해 ChatRoom -> ChatRoomDto 로 변환
            dto.setRoomId(room.getId());
            dto.setUpdatedAt(room.getUpdatedAt());
            dto.setRecentMessage(room.getRecentMessage());
            dto.setUnReadCount(unReadCount);

            return dto;
        }).collect(Collectors.toList());

        return rooms;
    }

    public Optional<ChatRoom> getRoom(String roomId){

        return chatRoomRepository.findById(Long.valueOf(roomId));

    }


    @Transactional
    public Boolean leaveChatRoom(String roomId, String userId){

        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(Long.valueOf(roomId));

        if(chatRoom.isPresent()){
            ChatRoom room = chatRoom.get();
            if(!room.getStatus()){
                chatRoomRepository.delete(chatRoom.get());
                return true;

            }else {
                room.setStatus(false);
                return false;
            }
        }else {
            throw new RuntimeException(roomId +"에 해당하는 채팅방이 존재하지 않습니다.");
        }
    }
}
