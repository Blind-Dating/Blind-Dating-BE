package com.blind.dating.dto.chat;

import com.blind.dating.domain.ChatRoom;
import com.blind.dating.domain.UserAccount;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ChatRoomDto {

    private Long roomId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    private Long otherUserId;
    private String otherUserNickname;
    private String recentMessage;
    private Long unReadCount;

    public ChatRoomDto(Long roomId, LocalDateTime updatedAt, Long otherUserid, String otherUserNickname, String recentMessage, Long unReadCount) {
        this.roomId = roomId;
        this.updatedAt = updatedAt;
        this.otherUserId = otherUserid;
        this.otherUserNickname = otherUserNickname;
        this.recentMessage = recentMessage;
        this.unReadCount = unReadCount;
    }

    public static ChatRoomDto From(UserAccount other, ChatRoom room, Long unReadCount) {
        return new ChatRoomDto(room.getId(), room.getUpdatedAt(), other.getId(), other.getNickname(), room.getRecentMessage(),unReadCount);

    }

}
