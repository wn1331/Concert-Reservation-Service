package hhplus.concertreservationservice.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import hhplus.concertreservationservice.application.concert.dto.ConcertCriteria;
import hhplus.concertreservationservice.application.concert.dto.ConcertResult;
import hhplus.concertreservationservice.application.concert.facade.ConcertFacade;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("[통합 테스트] ConcertFacade 테스트")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class ConcertFacadeIntegrationTest {

    @Autowired
    private ConcertFacade facade;

    @Test
    @Order(1)
    @DisplayName("[동시성 테스트] 100명의 사용자가 하나의 좌석을 동시에 예약했을 때, 1명만 성공하는지 테스트")
    void reserveSeat_concurrency_test() {

        Long concertSeatId = 1L; // 동일한 좌석에 대해 예약 시도
        String queueToken = "3b93aaaf-0ea8-49e4-be70-574a1813167b";  // 동일한 queueToken 사용
        Long userId = 1L;  // 동일한 사용자 ID 사용

        // 100명의 사용자가 동시에 하나의 좌석을 예약 시도 (IntStream 사용)
        List<CompletableFuture<ConcertResult.ReserveSeat>> tasks = IntStream.rangeClosed(1, 100)
            .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
                try {
                    ConcertCriteria.ReserveSeat reserveSeatCriteria = ConcertCriteria.ReserveSeat.builder()
                        .userId(userId)  // 동일한 userId 사용
                        .concertSeatId(concertSeatId)
                        .queueToken(queueToken)  // 동일한 queueToken 사용
                        .build();
                    return facade.reserveSeat(reserveSeatCriteria);
                } catch (Exception e) {
                    return null;  // 예외 발생 시 null 반환
                }
            }))
            .toList();  // 스트림을 리스트로 변환

        // 모든 비동기 작업 완료 대기
        CompletableFuture<Void> allTasks = CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]));
        allTasks.join();  // 모든 예약 요청이 완료될 때까지 기다림

        // 성공적으로 예약된 좌석의 개수 확인
        long successCount = tasks.stream()
            .map(task -> {
                try {
                    return task.join();  // 비동기 작업의 결과를 기다림
                } catch (Exception e) {
                    return null;  // 예외 발생 시 null 반환
                }
            })
            .filter(Objects::nonNull)  // 성공한 경우만 필터링
            .count();

        // 예약 성공한 사용자는 1명이어야 함
        assertEquals(1, successCount);
    }
}
