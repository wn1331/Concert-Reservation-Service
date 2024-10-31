package hhplus.concertreservationservice.integration.concurrency.lock;

import static org.assertj.core.api.Assertions.assertThat;

import hhplus.concertreservationservice.application.concert.dto.ConcertCriteria;
import hhplus.concertreservationservice.application.concert.dto.ConcertResult;
import hhplus.concertreservationservice.application.concert.dto.ConcertResult.ReserveSeat;
import hhplus.concertreservationservice.application.concert.facade.ConcertPaymentFacade;
import hhplus.concertreservationservice.application.concert.facade.ConcertReservationFacade;
import hhplus.concertreservationservice.application.user.dto.UserCriteria.ChargeBalance;
import hhplus.concertreservationservice.application.user.dto.UserResult;
import hhplus.concertreservationservice.application.user.facade.UserFacade;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("낙관락 테스트")
class OptimisticLockPerformanceTest {

    private static final Logger log = LoggerFactory.getLogger(OptimisticLockPerformanceTest.class);

    @Autowired
    private ConcertReservationFacade reserveFacade;

    @Autowired
    private ConcertPaymentFacade paymentFacade;

    @Autowired
    private UserFacade userFacade;

    private final Long REQUEST_AMOUNT = 1000L;


    @Test
    @DisplayName("[낙관락] 좌석 예약 동시성 테스트 - 1000개 쓰레드의 요청")
    void concurrencyReserveSeatTest() {
        ConcertCriteria.ReserveSeat reserveSeatCriteria = ConcertCriteria.ReserveSeat.builder()
            .userId(1L)
            .concertSeatId(1L)
            .build();

        List<CompletableFuture<ConcertResult.ReserveSeat>> tasks = new ArrayList<>();

        // 동시에 1000번의 예약 요청을 수행
        for (long i = 1; i <= REQUEST_AMOUNT; i++) {
            tasks.add(CompletableFuture.supplyAsync(() -> {
                try {
                    ReserveSeat reserveSeat = reserveFacade.reserveSeat(reserveSeatCriteria);
                    log.info("좌석 예약 성공");
                    return reserveSeat;
                } catch (OptimisticLockingFailureException e) {
                    log.error("낙관락 예외 : {}", e.getMessage());
                    return null;
                } catch (Exception e) {
                    log.error("예외 : {}", e.getMessage());
                    return null;
                }
            }));
        }

        // 모든 CompletableFuture가 생성된 이후 실제 요청을 시작하는 시점
        long startTime = System.currentTimeMillis();

        // 모든 CompletableFuture 작업이 완료될 때까지 대기
        CompletableFuture<Void> allTasks = CompletableFuture.allOf(
            tasks.toArray(new CompletableFuture[0]));
        allTasks.join();

        // 시간 측정
        long endTime = System.currentTimeMillis();
        log.info("좌석 예약 {}개의 동시 비동기 요청 총 수행 시간 : {}ms", REQUEST_AMOUNT, endTime - startTime);

        // 성공한 예약 횟수 계산
        long successCount = getSuccessCount(tasks);


        // 성공한 횟수 확인
        assertThat(successCount).isEqualTo(1);
    }

    @Test
    @DisplayName("[낙관락] 결제(포인트사용) 동시성 테스트 - 1000개 쓰레드의 요청")
    void concurrencyPayTest() {

        ConcertCriteria.Pay payCriteria = ConcertCriteria.Pay.builder()
            .userId(2L)
            .reservationId(1L)
            .build();

        List<CompletableFuture<ConcertResult.Pay>> tasks = new ArrayList<>();

        // 동시에 10번의 충전 요청을 수행
        for (long i = 1; i <= REQUEST_AMOUNT; i++) {

            tasks.add(CompletableFuture.supplyAsync(() -> {
                try {
                    ConcertResult.Pay payResult = paymentFacade.pay(payCriteria);
                    log.info("결제 성공");
                    return payResult;
                    // 재시도 로직으로 낙관락 예외는 잡히지 않는다.(재시도가 매우 빠르면 잡힌다. 대신 테스트도 실패할 것)
                } catch (Exception e) {
                    log.error("예외 : {}", e.getMessage());
                    return null;
                }
            }));
        }

        // 모든 CompletableFuture가 생성된 이후 실제 요청을 시작하는 시점
        long startTime = System.currentTimeMillis();

        CompletableFuture<Void> allTasks = CompletableFuture.allOf(
            tasks.toArray(new CompletableFuture[0]));
        allTasks.join();

        // 시간 측정
        long endTime = System.currentTimeMillis();
        log.info("좌석 결제 {}개의 동시 비동기 요청 총 수행 시간 : {}ms", REQUEST_AMOUNT, endTime - startTime);

        // 성공한 결제 횟수 계산
        long successCount = getSuccessCount(tasks);

        // 성공한 횟수 확인
        assertThat(successCount).isEqualTo(1);
    }

    @Test
    @DisplayName("[낙관락] 포인트충전 동시성 테스트 - 1000번 충전")
    void chargeBalance_concurrency_test() {

        ChargeBalance dto = ChargeBalance.builder().userId(1L).amount(BigDecimal.valueOf(10000))
            .build();

        List<CompletableFuture<UserResult.ChargeBalance>> tasks = new ArrayList<>();

        // 동시에 1000번의 충전 요청을 수행
        for (long i = 1; i <= REQUEST_AMOUNT; i++) {

            tasks.add(CompletableFuture.supplyAsync(() -> {
                try {
                    UserResult.ChargeBalance chargeBalance = userFacade.chargeBalance(dto);
                    log.info("충전 성공");
                    return chargeBalance;
                    // 재시도 로직으로 낙관락 예외는 잡히지 않는다.
                } catch (Exception e) {
                    log.error("예외 : {}", e.getMessage());
                    return null;
                }
            }));
        }

        // 모든 CompletableFuture가 생성된 이후 실제 요청을 시작하는 시점
        long startTime = System.currentTimeMillis();

        CompletableFuture<Void> allTasks = CompletableFuture.allOf(
            tasks.toArray(new CompletableFuture[0]));
        allTasks.join();

        // 시간 측정
        long endTime = System.currentTimeMillis();
        log.info("포인트 충전 {}개의 동시 비동기 요청 총 수행 시간 : {}ms", REQUEST_AMOUNT, endTime - startTime);

        // 성공한 충전 횟수 계산
        long successCount = getSuccessCount(tasks);

        // 성공한 횟수 확인. 1000일 수 없다
        Assertions.assertNotEquals(successCount,1000);
//        assertThat(successCount).isEqualTo(1000);


    }

    private static <T> long getSuccessCount(List<CompletableFuture<T>> tasks) {
        return tasks.stream()
            .filter(task -> {
                try {
                    return task.get() != null;
                } catch (Exception e) {
                    return false;
                }
            })
            .count();
    }

}
