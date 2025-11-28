package com.gdg.slbackend.service.memo;

import com.gdg.slbackend.domain.memo.MemoBoard;
import com.gdg.slbackend.domain.memo.MemoBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemoBoardService {

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
        return memoBoardRepository.findFirstByOrderByIdAsc()
                .orElseGet(this::createDefaultBoard);
    }

    private MemoBoard createDefaultBoard() {
        MemoBoard defaultBoard = MemoBoard.builder()
                .title("기본 메모 보드")
                .description("모든 메모의 기본 보드")
                .build();
        return memoBoardRepository.save(defaultBoard);
    }
}
