package com.blind.dating.repository;

import com.blind.dating.domain.LikesUnlikes;
import com.blind.dating.domain.UserAccount;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LikesUnlikesRepository extends JpaRepository<LikesUnlikes, Long> {

    // 상대방이 이미 나를 좋아요눌렀는지 조회하고 가져오는 쿼리
    @Query("SELECT lk FROM LikesUnlikes lk WHERE lk.userId = :recieverId AND lk.receiver = :user")
    List<LikesUnlikes> findLikes(Long recieverId, UserAccount user);
}
