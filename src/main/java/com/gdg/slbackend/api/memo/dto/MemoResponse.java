package com.gdg.slbackend.api.memo.dto;

import com.gdg.slbackend.domain.memo.Memo;
import com.gdg.slbackend.domain.memo.MemoColor;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MemoResponse(
        Long id,
        Long boardId,
        String contentText,
        MemoColor color,
        Double posX,
        Double posY,
        String drawingImageUrl,
        LocalDateTime createdAt
) {
    public static MemoResponse from(Memo memo) {
        return MemoResponse.builder()
                .id(memo.getId())
                .boardId(memo.getBoard().getId())
                .contentText(memo.getContentText())
                .color(memo.getColor())
                .posX(memo.getPosX())
                .posY(memo.getPosY())
                .drawingImageUrl(memo.getDrawingImageUrl())
                .createdAt(memo.getCreatedAt())
                .build();
    }
}