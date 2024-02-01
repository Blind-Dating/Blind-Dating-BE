package com.blind.dating.repository;

import com.blind.dating.domain.Interest;
import com.blind.dating.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterestRepository extends JpaRepository<Interest, Long> {

    void deleteAllByUserAccount(UserAccount user);
}
