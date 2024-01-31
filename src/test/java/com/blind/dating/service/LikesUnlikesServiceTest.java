package com.blind.dating.service;

import com.blind.dating.domain.ChatRoom;
import com.blind.dating.domain.LikesUnlikes;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.repository.LikesUnlikesRepository;
import com.blind.dating.repository.ReadChatRepository;
import com.blind.dating.repository.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@DisplayName("LikesUnlikesService - 테스트")
@ExtendWith(MockitoExtension.class)
class LikesUnlikesServiceTest {

    @Mock private LikesUnlikesRepository likesUnlikesRepository;
    @Mock private UserAccountRepository userAccountRepository;
    @Mock private ChattingRoomService chatRoomService;
    @Mock private ReadChatRepository readChatRepository;
    @InjectMocks private LikesUnlikesService likesUnlikesService;

    Authentication authentication;
    String userId;
    String receiverId;
    UserAccount senderAccount;
    UserAccount receiverAccount;
    @BeforeEach
    void setting() {
        authentication = new UsernamePasswordAuthenticationToken("1",null,null);
        userId = "1";
        receiverId = "2";
        senderAccount = UserAccount.of("user01","pass01", "nickname1","서울","INFP","M","하이요");
        receiverAccount = UserAccount.of("user01","pass02", "nickname2","부산","ESTP","W","안녕");
    }

    @DisplayName("좋아요 - 테스트")
    @Test
    void givenUserIdAndReceiverId_whenLikeUser_thenSaveLikes(){
        //Given
        given(userAccountRepository.findById(Long.parseLong(receiverId))).willReturn(Optional.of(receiverAccount));
        given(likesUnlikesRepository.save(any(LikesUnlikes.class))).willReturn(new LikesUnlikes());
        given(userAccountRepository.findById(Long.parseLong(userId))).willReturn(Optional.of(senderAccount));

        given(likesUnlikesRepository.findLikes(receiverAccount.getId(), senderAccount)).willReturn(new ArrayList<>());

        // When
        Boolean result = likesUnlikesService.likeUser(authentication, receiverId);

        // Then
        assertThat(result).isFalse(); // 좋아요 눌렀으므로 true 여부 확인
    }

    @DisplayName("좋아요 후 채팅방 생성 - 테스트")
    @Test
    void givenUserIdAndReceiverId_whenLikeUser_thenSaveLikesAndChatRoom() {
        // Given
        given(userAccountRepository.findById(2L)).willReturn(Optional.of(receiverAccount));
        given(likesUnlikesRepository.save(any(LikesUnlikes.class))).willReturn(new LikesUnlikes());
        given(userAccountRepository.findById(1L)).willReturn(Optional.of(senderAccount));
        given(likesUnlikesRepository.findLikes(receiverAccount.getId(), senderAccount)).willReturn(List.of(new LikesUnlikes()));

        ChatRoom chatRoom = new ChatRoom();
        given(chatRoomService.create(senderAccount, receiverAccount)).willReturn(chatRoom);

        // When
        Boolean flag = likesUnlikesService.likeUser(authentication,receiverId);

        // Then
        assertThat(flag).isTrue();
    }

    @DisplayName("좋아요 예외 발생1 - 테스트")
    @Test
    void givenUserIdAndReceiverId_whenLikeUser_thenThrowException() {
        // Given
        Long receiverId = 2L;
        given(userAccountRepository.findById(receiverId)).willReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            likesUnlikesService.likeUser(authentication, "2");
        });

        // Then
        assertThat(exception.getMessage()).isEqualTo("존재하지 않는 계정입니다.");
        verify(likesUnlikesRepository, never()).save(any(LikesUnlikes.class));
    }

    @DisplayName("좋아요 예외발생2 - 테스트")
    @Test
    void givenUserIdAndReceiverId_whenLikeUser_thenThrowException2(){
        //Given
        given(userAccountRepository.findById(Long.parseLong(receiverId))).willReturn(Optional.of(receiverAccount));
        given(likesUnlikesRepository.save(any(LikesUnlikes.class))).willReturn(new LikesUnlikes());
        given(userAccountRepository.findById(Long.parseLong(userId))).willReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            likesUnlikesService.likeUser(authentication, "2");
        });

        // Then
        assertThat(exception.getMessage()).isEqualTo("존재하지 않는 계정입니다.");
    }

    @DisplayName("싫어요 - 테스트")
    @Test
    void givenReceiverId_whenUnlikeUser_thenReturnVoid() {
        // Given
        Long receiverId = 2L;
        given(userAccountRepository.findById(anyLong())).willReturn(Optional.of(receiverAccount));

        // When
        likesUnlikesService.unlikeUser(authentication, "1");

        // Then
        verify(userAccountRepository, times(1)).findById(anyLong());
        verify(likesUnlikesRepository, times(1)).save(LikesUnlikes.of(receiverId, receiverAccount, false));
    }

    @DisplayName("싫어요 예외 발생 - 테스트")
    @Test
    void givenReceiverId_whenUnlikeUser_thenReturnException() {
        // Given
        given(userAccountRepository.findById(anyLong())).willReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            likesUnlikesService.unlikeUser(authentication, "2");
        });

        // Then
        assertThat(exception.getMessage()).isEqualTo("잘못된 요청입니다.");
        verify(likesUnlikesRepository, never()).save(any(LikesUnlikes.class));
    }

}