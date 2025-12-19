package com.gdg.slbackend.api.memo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MemoRequest(
        Long boardId,
        @NotBlank(message = "메모 내용을 입력해주세요.")
        @Size(max = 2000, message = "메모 내용은 2000자를 넘을 수 없습니다.")
        String contentText,
        @NotNull(message = "X 좌표가 필요합니다.")
        Double posX,
        @NotNull(message = "Y 좌표가 필요합니다.")
        Double posY,
        @Size(max = 500, message = "이미지 URL은 500자를 넘을 수 없습니다.")
        String drawingImageUrl
) {
}