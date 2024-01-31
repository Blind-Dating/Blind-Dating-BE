package com.blind.dating.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Chat extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ChatRoom chatRoom;

    @Setter
    private Long writerId;

    @Setter
    private String message;

    public Chat(ChatRoom chatRoom, Long writerId, String message) {
        this.chatRoom = chatRoom;
        this.writerId = writerId;
        this.message = message;
    }

    public static Chat of(ChatRoom chatRoom, Long writerId, String message) {
        return new Chat(chatRoom, writerId, message);
    }


}
