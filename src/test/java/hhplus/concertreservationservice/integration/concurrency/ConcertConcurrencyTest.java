package hhplus.concertreservationservice.integration.concurrency;

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
class ConcertConcurrencyTest {

    @Autowired
    private ConcertFacade facade;

    @Test
    @Order(1)
    @DisplayName("[동시성 테스트] 사용자가 하나의 좌석을 100번 동시에 예약했을 때, 1번만 성공하는지 테스트(유니크키 걸려있지 않아서 100명의 유저가 동시에 신청하는것과 같음)")
    void reserveSeat_concurrency_test() {

        Long concertSeatId = 1L;
        String queueToken = "3b93aaaf-0ea8-49e4-be70-574a1813167b";
        Long userId = 1L;

        // 사용자가 동시에 하나의 좌석을 100번 예약 시도 (IntStream 사용)
        List<CompletableFuture<ConcertResult.ReserveSeat>> tasks = IntStream.rangeClosed(1, 100)
            .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
                try {
                    ConcertCriteria.ReserveSeat reserveSeatCriteria = ConcertCriteria.ReserveSeat.builder()
                        .userId(userId)
                        .concertSeatId(concertSeatId)
                        .queueToken(queueToken)
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

        // 예약 성공한 것은 1번이어야 함
        assertEquals(1, successCount);
    }

//    @Test
//    @Order(2)
//    @DisplayName("[동시성 테스트] 100만원을 가진 사용자가 동시에 15만원짜리 결제요청을 6번 했을 시 10만원이 남는지 테스트")
//    void pay_concurrency_test() {
//        Long concertSeatId = 1L;
//        String queueToken = "3b93aaaf-0ea8-49e4-be70-574a1813167c";  // 동일한 queueToken 사용
//        Long userId = 2L;
//        Long reservationId = 1L;
//
//
//
//    }

}
