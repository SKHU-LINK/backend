package com.gdg.slbackend.global.enums;

import lombok.Getter;

@Getter
public enum MileageType {

    RESOURCE_UPLOAD_REWARD(+10, "자료 업로드 보상"),
    RESOURCE_DOWNLOAD_UPLOADER_REWARD(+100, "자료 다운로드 보상"),
    RESOURCE_DOWNLOAD(-100, "자료 다운로드 차감");

    private final int amount;
    private final String description;

    MileageType(int amount, String description) {
        this.amount = amount;
        this.description = description;
    }

}
