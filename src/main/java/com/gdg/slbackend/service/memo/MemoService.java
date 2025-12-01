package com.gdg.slbackend.service.memo;

import com.gdg.slbackend.api.memo.dto.MemoRequest;
import com.gdg.slbackend.api.memo.dto.MemoResponse;
import com.gdg.slbackend.domain.memo.Memo;
import com.gdg.slbackend.domain.memo.MemoBoard;
import com.gdg.slbackend.domain.memo.MemoColor;
import com.gdg.slbackend.domain.memo.MemoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemoService {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    private final MemoRepository memoRepository;
    private final MemoBoardService memoBoardService;

    @Transactional
    public MemoResponse createMemo(MemoRequest request) {
        MemoBoard board = memoBoardService.getBoardOrDefault(request.boardId());
        Memo memo = Memo.builder()
                .board(board)
                .contentText(request.contentText())
                .color(MemoColor.randomColor())
                .posX(request.posX())
                .posY(request.posY())
                .drawingImageUrl(request.drawingImageUrl())
                .build();

        Memo saved = memoRepository.save(memo);
        return MemoResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<MemoResponse> findMemos(boolean all) {
        List<Memo> memos = all
                ? memoRepository.findAllByOrderByCreatedAtAsc()
                : memoRepository.findByCreatedAtGreaterThanEqualOrderByCreatedAtAsc(startOfTodayInSeoul());
        return memos.stream().map(MemoResponse::from).toList();
    }

    @Transactional
    public int deleteOldMemos() {
        return memoRepository.deleteOlderThan(startOfTodayInSeoul());
    }

    private LocalDateTime startOfTodayInSeoul() {
        LocalDate today = LocalDate.now(SEOUL_ZONE);
        return today.atStartOfDay();
    }
}