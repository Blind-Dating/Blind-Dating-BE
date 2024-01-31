package com.blind.dating.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table
@Entity
public class Interest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private UserAccount userAccount;

    @Setter
    @Column(nullable = false, length = 50)
    private String interestName;


    private Interest(UserAccount userAccount, String interestName) {
        this.userAccount = userAccount;
        this.interestName = interestName;
    }

    public static Interest of(UserAccount userAccount, String interestName){
        return new Interest(userAccount, interestName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Interest)) return false;
        Interest interest = (Interest) o;
        return Objects.equals(id, interest.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
