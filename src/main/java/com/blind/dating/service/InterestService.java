package com.blind.dating.service;

import com.blind.dating.domain.Interest;
import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.interest.InterestDto;
import com.blind.dating.repository.InterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InterestService {

    private final InterestRepository interestRepository;

    /**
     * 내 관심사 저장하기.
     * @param userAccount
     * @param interestName
     * @return List<Interest>
     */
    @Transactional
    public List<Interest> saveInterest(UserAccount userAccount, List<String> interestName){

        List<Interest> list = new ArrayList<>();

        for(String s: interestName){
            list.add(Interest.of(userAccount,s));
        }

        return list;
    }
}
