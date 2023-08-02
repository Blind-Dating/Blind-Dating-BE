package com.blind.dating.dto.response;

import com.blind.dating.domain.Interest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterestResponse {
    private Long id;
    private String interestName;

    public static InterestResponse from(Interest interest){
        return new InterestResponse(interest.getId(),interest.getInterestName());
    }

}
