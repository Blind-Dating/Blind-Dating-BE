package com.blind.dating.controller;

import com.blind.dating.common.code.UserResponseCode;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.common.Api;
import com.blind.dating.dto.user.UserInfoWithPageInfo;
import com.blind.dating.dto.user.UserUpdateRequestDto;
import com.blind.dating.dto.user.UserWithInterestAndQuestionDto;
import com.blind.dating.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    // 유저10명 불러오기
    @GetMapping("/user-list")
    public ResponseEntity<?> getUserList(
            Authentication authentication,
            @PageableDefault(size = 10, sort = "recentLogin", direction = Sort.Direction.DESC) Pageable pageable
            ){

        // 이성 추천 유저리스트 가져오기.
        Page<UserWithInterestAndQuestionDto> pages = userService.getUserList(authentication,pageable);
        UserInfoWithPageInfo content = new UserInfoWithPageInfo(pages.getNumber(), pages.getTotalPages(), pages.getContent());

        return ResponseEntity.ok()
                .body(Api.OK(UserResponseCode.GET_USER_LIST_SUCCESS, content));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getMyInfo(
            Authentication authentication
    ){
        // 내정보 조회하기
        UserWithInterestAndQuestionDto userInfo = userService.getMyInfo(authentication);

        return ResponseEntity.ok()
                .body(Api.OK(UserResponseCode.GET_USER_INFO_SUCCESS, userInfo));
    }

    @PutMapping("/user")
    public ResponseEntity<Api<UserWithInterestAndQuestionDto>> updateMyInfo(
            Authentication authentication
            , @RequestBody UserUpdateRequestDto dto
    ){
        //내정보 업데이트
        UserAccount user = userService.updateMyInfo(authentication, dto);
        // 업데이트 된 정보 가져오기
        UserWithInterestAndQuestionDto userInfo = UserWithInterestAndQuestionDto.from(user);

        return ResponseEntity.ok()
                .body(Api.OK(UserResponseCode.UPDATE_USER_INFO_SUCCESS, userInfo));
    }

}
