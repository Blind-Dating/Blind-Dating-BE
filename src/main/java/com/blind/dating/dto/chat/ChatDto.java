package com.blind.dating.dto.chat;

import com.blind.dating.domain.Chat;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto{

    private Long id;
    private Long writerId;
    private String message;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    private Long chatRoomId;
    private Boolean status;

    public ChatDto(Long id,Long chatRoomId, Long writerId, String message, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.chatRoomId = chatRoomId;
        this.writerId = writerId;
        this.message = message;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ChatDto from(Chat chat){
        return new ChatDto(
                chat.getId(),
                chat.getChatRoom().getId(),
                chat.getWriterId(),
                chat.getMessage(),
                chat.getCreatedAt(),
                chat.getUpdatedAt()
        );
    }
}
