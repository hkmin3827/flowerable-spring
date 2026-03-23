package com.flowerable.spring.global.scheduler;

import com.flowerable.spring.domain.user.service.TestUserCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestUserCleanupScheduler {

    private final TestUserCleanupService testUserCleanupService;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void run() {
        log.info("[TestUserCleanup] ===== 자정 테스트 계정 cleanup 시작 =====");
        try {
            testUserCleanupService.cleanupTestUser();
        } catch (Exception e) {
            log.error("[TestUserCleanup] 오류 발생 : ", e);
        }
        log.info("===== TestUsercleanup 스케쥴러 종료 =====");
    }
}