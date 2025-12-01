package com.gdg.slbackend.api.community.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CommunityRequest {
    @NotBlank
    private String name;

    @NotNull
    @Min(value = 1900, message = "year는 최소 1900이어야 합니다.")
    @Max(value = 3000, message = "year는 최대 3000이어야 합니다.")
    private int year;

    @NotNull
    @Min(value = 1, message = "semester는 1 혹은 2여야 합니다.")
    @Max(value = 2, message = "semester는 1 혹은 2여야 합니다.")
    private int semester;
}
