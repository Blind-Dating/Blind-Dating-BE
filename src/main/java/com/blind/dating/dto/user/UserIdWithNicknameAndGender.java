package com.blind.dating.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserIdWithNicknameAndGender {
    private Long id;
    private String nickname;
    private String gender;
}
