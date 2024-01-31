package com.blind.dating.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "userId"),
        @Index(columnList = "nickname"),
        @Index(columnList = "gender")
})
@Entity
@AllArgsConstructor
public class UserAccount extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false, length = 50, unique = true)
    private String userId;

    @Setter
    private String userPassword;

    @Setter
    @Column(nullable = false, unique = true)
    private String nickname;

    @Setter
    @Column(nullable = false, length = 100)
    private String region;


    @Setter
    @Column(length = 20)
    private String mbti;

    @Setter
    @Column(nullable = false, length = 20)
    private String gender;

    @Setter
    private Boolean deleted;

    @Setter
    private String selfIntroduction;

    @Setter
    private LocalDateTime recentLogin;

    @Setter
    private String role;

    @Setter
    private String social;

    @OneToMany(mappedBy = "userAccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude @Setter
    private List<Interest> interests;

    @OneToMany(mappedBy = "userAccount",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude @Setter
    private List<Question> questions;

    @OneToMany(mappedBy = "receiver",fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<LikesUnlikes> likesUnlikes;

    @ManyToMany(mappedBy = "users", fetch = FetchType.LAZY)
    private List<ChatRoom> chatRooms;


    protected UserAccount(){}

    private UserAccount(String userId, String userPassword, String nickname, String region, String mbti, String gender,Boolean deleted, String selfIntroduction, LocalDateTime recentLogin, String role) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.nickname = nickname;
        this.region = region;
        this.mbti = mbti;
        this.gender = gender;
        this.deleted = deleted;
        this.selfIntroduction = selfIntroduction;
        this.recentLogin = recentLogin;
        this.role = role;
    }

    public static UserAccount of(String userId, String userPassword, String nickname, String region, String mbti, String gender, Boolean deleted, String selfIntroduction, LocalDateTime recentLogin, String role) {
        return new UserAccount(userId, userPassword, nickname, region, mbti, gender,deleted, selfIntroduction, recentLogin, role);
    }

    private UserAccount(String userId, String userPassword, String nickname, String region, String mbti, String gender, String selfIntroduction) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.nickname = nickname;
        this.region = region;
        this.mbti = mbti;
        this.gender = gender;
        this.selfIntroduction = selfIntroduction;
    }
    public static UserAccount of(String userId, String userPassword, String nickname, String region, String mbti, String gender, String selfIntroduction) {
        return new UserAccount(userId, userPassword, nickname, region, mbti, gender, selfIntroduction);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAccount that = (UserAccount) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
