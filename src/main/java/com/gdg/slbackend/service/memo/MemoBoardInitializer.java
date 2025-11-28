package com.gdg.slbackend.service.memo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemoBoardInitializer implements ApplicationRunner {

    private final MemoBoardService memoBoardService;

    @Override
    public void run(ApplicationArguments args) {
        memoBoardService.getOrCreateDefaultBoard();
        log.info("기본 메모 보드가 준비되었습니다.");
    }
}