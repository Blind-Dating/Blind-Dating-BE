package com.blind.dating.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Entity
public class ChatRoom extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ToString.Exclude
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
           name = "user_chat_room",
           joinColumns = @JoinColumn(name = "chat_room_id"),
           inverseJoinColumns = @JoinColumn(name = "user_id")
    )
   private Set<UserAccount> users = new LinkedHashSet<>();

    @Setter
    @OneToMany(mappedBy = "chatRoom")
    @ToString.Exclude
    private Set<ReadChat> readChat = new LinkedHashSet<>();

    @Setter
    private Boolean status;

    @Setter
    private String recentMessage;

    @ToString.Exclude
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private final Set<Chat> chats = new LinkedHashSet<>();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatRoom)) return false;
        ChatRoom chatRoom = (ChatRoom) o;
        return Objects.equals(id, chatRoom.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
