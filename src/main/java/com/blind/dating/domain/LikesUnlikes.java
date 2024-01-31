package com.blind.dating.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
@NoArgsConstructor
@Entity
public class LikesUnlikes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private Long userId;

    @Setter
    @ManyToOne
    private UserAccount receiver;

    @Setter
    private Boolean isLike;

    private LikesUnlikes(Long userId, UserAccount receiver, Boolean isLike) {
        this.userId = userId;
        this.receiver = receiver;
        this.isLike = isLike;
    }

    public static LikesUnlikes of(Long userId, UserAccount receiver, Boolean isLike) {
        return new LikesUnlikes(userId, receiver, isLike);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LikesUnlikes)) return false;
        LikesUnlikes that = (LikesUnlikes) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
