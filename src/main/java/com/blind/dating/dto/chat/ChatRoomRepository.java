package com.blind.dating.dto.chat;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ChatRoomRepository {
    private Map<String, ChatRoomDtos> chatRoomDTOMap;

    @PostConstruct
    private void init(){
        chatRoomDTOMap = new LinkedHashMap<>();
    }

    public List<ChatRoomDtos> findAllRooms(){
        //채팅방 생성 순서 최근 순으로 반환
        List<ChatRoomDtos> result = new ArrayList<>(chatRoomDTOMap.values());
        Collections.reverse(result);

        return result;
    }

    public ChatRoomDtos findRoomById(String id){
        return chatRoomDTOMap.get(id);
    }

    public ChatRoomDtos createChatRoomDTO(String name){
        ChatRoomDtos room = ChatRoomDtos.create(name);
        chatRoomDTOMap.put(room.getRoomId(), room);

        return room;
    }
}
