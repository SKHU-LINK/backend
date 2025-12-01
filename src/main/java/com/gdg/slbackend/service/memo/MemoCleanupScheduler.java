package com.gdg.slbackend.service.memo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemoCleanupScheduler {

    private final MemoService memoService;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void clearOldMemos() {
        int deletedCount = memoService.deleteOldMemos();
        log.info("메모 정리 완료: {}건 삭제", deletedCount);
    }
}