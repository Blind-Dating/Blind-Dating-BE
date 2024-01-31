package com.blind.dating.controller;

import com.blind.dating.common.Api;
import com.blind.dating.common.code.LikesUnlikesResponseCode;
import com.blind.dating.dto.user.UserReceiverId;
import com.blind.dating.service.LikesUnlikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class LikesUnlikesController {

    private final LikesUnlikesService likesUnlikesService;

    @PostMapping("/like")
    public ResponseEntity<?> likeUser(
            @RequestBody UserReceiverId userReceiverId,
            Authentication authentication
    ){

        Boolean result = likesUnlikesService.likeUser(authentication,userReceiverId.getReceiverId());
        
        return ResponseEntity.ok()
                .body(Api.OK(LikesUnlikesResponseCode.LIKE_SUCCESS, result));
    }

    @PostMapping("/unlike")
    public ResponseEntity<?> unlikeUser(
            @RequestBody UserReceiverId userReceiverId,
            Authentication authentication
    ){
       likesUnlikesService.unlikeUser(authentication,userReceiverId.getReceiverId());

        return ResponseEntity.ok()
                .body(Api.OK(LikesUnlikesResponseCode.UNLIKE_SUCCESS, true));
    }

}
