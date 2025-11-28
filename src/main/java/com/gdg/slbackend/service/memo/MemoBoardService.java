package com.gdg.slbackend.service.memo;

import com.gdg.slbackend.domain.memo.MemoBoard;
import com.gdg.slbackend.domain.memo.MemoBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemoBoardService {

    public static final long DEFAULT_BOARD_ID = 1L;

    private final MemoBoardRepository memoBoardRepository;

    @Transactional(readOnly = true)
    public MemoBoard getBoardOrDefault(Long boardId) {
        if (boardId != null) {
            return memoBoardRepository.findById(boardId)
                    .orElseGet(this::getOrCreateDefaultBoard);
        }
        return getOrCreateDefaultBoard();
    }

    @Transactional
    public MemoBoard getOrCreateDefaultBoard() {
        return memoBoardRepository.findById(DEFAULT_BOARD_ID)
                .orElseGet(() -> createDefaultBoard(DEFAULT_BOARD_ID));
    }

    private MemoBoard createDefaultBoard(Long id) {
        MemoBoard defaultBoard = MemoBoard.builder()
                .title("기본 메모 보드")
                .description("모든 메모의 기본 보드")
                .build();
        defaultBoard.assignId(id);
        return memoBoardRepository.save(defaultBoard);
    }
}