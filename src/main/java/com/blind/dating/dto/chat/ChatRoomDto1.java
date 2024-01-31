package com.blind.dating.dto.chat;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ChatRoomDto1 {

    private Long roomId;
    private Long otherUserid;
    private String otherUserNickname;
    private String recentMessage;
    private Long unReadCount;

    public ChatRoomDto1(Long roomId, Long otherUserid, String otherUserNickname, String recentMessage) {
        this.roomId = roomId;
        this.otherUserid = otherUserid;
        this.otherUserNickname = otherUserNickname;
        this.recentMessage = recentMessage;
    }

    public static ChatRoomDto1 from(ChatRoomDto dto){
        return new ChatRoomDto1(dto.getRoomId(), dto.getOtherUserId(),dto.getOtherUserNickname(), dto.getRecentMessage());
    }
}