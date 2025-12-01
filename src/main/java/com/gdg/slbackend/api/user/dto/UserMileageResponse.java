package com.gdg.slbackend.api.user.dto;

import lombok.Getter;

/**
 * 현재 사용자의 마일리지 값을 단순 조회할 때 사용하는 응답 DTO임.
 */
@Getter
public class UserMileageResponse {

    private final int mileage;

    public UserMileageResponse(int mileage) {
        this.mileage = mileage;
    }
}
