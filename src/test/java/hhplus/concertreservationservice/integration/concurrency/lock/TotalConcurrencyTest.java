package hhplus.concertreservationservice.integration.concurrency.lock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import hhplus.concertreservationservice.application.concert.dto.ConcertCriteria;
import hhplus.concertreservationservice.application.concert.dto.ConcertResult;
import hhplus.concertreservationservice.application.concert.dto.ConcertResult.Pay;
import hhplus.concertreservationservice.application.concert.dto.ConcertResult.ReserveSeat;
import hhplus.concertreservationservice.application.concert.facade.ConcertPaymentFacade;
import hhplus.concertreservationservice.application.concert.facade.ConcertReservationFacade;
import hhplus.concertreservationservice.application.user.dto.UserCriteria.ChargeBalance;
import hhplus.concertreservationservice.application.user.dto.UserResult;
import hhplus.concertreservationservice.application.user.facade.UserFacade;
import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import hhplus.concertreservationservice.global.exception.ErrorCode;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
@DisplayName("[통합 테스트] 동시성 테스트 모음")
class TotalConcurrencyTest {

    private static final Logger log = LoggerFactory.getLogger(TotalConcurrencyTest.class);

    @Autowired
    private ConcertReservationFacade reserveFacade;

    @Autowired
    private ConcertPaymentFacade paymentFacade;

    @Autowired
    private UserFacade userFacade;

    private final Long REQUEST_AMOUNT = 1000L;


    @Test
    @DisplayName("[Pub/Sub] 좌석 예약 동시성 테스트 - 1000번 비동기 요청 시 1번만 성공한다.")
    void concurrencyReserveSeatTest() {
        ConcertCriteria.ReserveSeat reserveSeatCriteria = ConcertCriteria.ReserveSeat.builder()
            .userId(1L)
            .concertSeatId(1L)
            .build();

        List<CompletableFuture<ConcertResult.ReserveSeat>> tasks = new ArrayList<>();

        // 동시에 1000번의 예약 요청을 수행
        for (long i = 1; i <= 1000; i++) {
            tasks.add(CompletableFuture.supplyAsync(() -> {
                try {
                    ReserveSeat reserveSeat = reserveFacade.reserveSeat(reserveSeatCriteria);
                    log.info("좌석 예약 성공");
                    return reserveSeat;
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
        log.info("좌석 예약 {}개의 비동기 요청 총 수행 시간 : {}ms", REQUEST_AMOUNT, endTime - startTime);

        // 성공한 예약 횟수 계산
        long successCount = getSuccessCount(tasks);


        // 성공한 횟수 확인
        assertThat(successCount).isEqualTo(1);
    }

    @Test
    @DisplayName("[비관락] 결제(포인트사용) 동시성 테스트 - 1000번 비동기 요청 시 1번만 성공한다.")
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
                }catch (OptimisticLockingFailureException e){
                    throw new CustomGlobalException(ErrorCode.OPTIMISTIC_EXCEPTION);
                }
                catch (Exception e) {
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
        log.info("좌석 결제 {}개의 비동기 요청 총 수행 시간 : {}ms", REQUEST_AMOUNT, endTime - startTime);

        // 성공한 결제 횟수 계산
        long successCount = getSuccessCount(tasks);

        // 성공한 횟수 확인
        assertThat(successCount).isEqualTo(1);
    }

    @Test
    @DisplayName("[낙관락(재시도x)] 포인트충전 동시성 테스트 - 1000번 동시에 비동기 요청 시 1000번 모두 성공해서는 안 된다.")
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
        log.info("포인트 충전 {}개의 비동기 요청 총 수행 시간 : {}ms", REQUEST_AMOUNT, endTime - startTime);

        // 성공한 충전 횟수 계산
        long successCount = getSuccessCount(tasks);

        // 전부 성공할 수 없음
        assertNotEquals(successCount,1000);


    }

    @Test
    @DisplayName("[Pub/Sub - ExecutorService] 좌석 예약 동시성 테스트 - 1000번 비동기 요청 시 1번만 성공한다.")
    void concurrencyReserveSeatTestWithExecutorService() throws InterruptedException{
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        List<Callable<ConcertResult.ReserveSeat>> tasks = new ArrayList<>();
        ConcertCriteria.ReserveSeat reserveSeatCriteria = ConcertCriteria.ReserveSeat.builder()
            .userId(1L)
            .concertSeatId(1L)
            .build();

        for (int i = 0; i < REQUEST_AMOUNT; i++) {
            tasks.add(() -> {
                try {
                    return reserveFacade.reserveSeat(reserveSeatCriteria);
                } catch (Exception e) {
                    log.error("예외 : {}", e.getMessage());
                    return null;
                }
            });
        }

        long startTime = System.currentTimeMillis();
        List<Future<ConcertResult.ReserveSeat>> futures = executorService.invokeAll(tasks);
        long endTime = System.currentTimeMillis();
        log.info("좌석 예약 {}개의 비동기 요청 총 수행 시간 : {}ms", REQUEST_AMOUNT, endTime - startTime);

        long successCount = futures.stream().filter(f -> {
            try {
                return f.get() != null;
            } catch (Exception e) {
                return false;
            }
        }).count();

        assertThat(successCount).isEqualTo(1);
        executorService.shutdown();
    }

    @Test
    @DisplayName("[비관락 - ExecutorService] 결제 동시성 테스트 - 1000번 비동기 요청 시 1번만 성공한다.")
    void concurrencyPayTestWithExecutorService() throws InterruptedException{
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        List<Callable<Pay>> tasks = new ArrayList<>();
        ConcertCriteria.Pay payCriteria = ConcertCriteria.Pay.builder()
            .userId(2L)
            .reservationId(1L)
            .build();

        for (int i = 0; i < REQUEST_AMOUNT; i++) {
            tasks.add(() -> {
                try {
                    return paymentFacade.pay(payCriteria);
                } catch (OptimisticLockingFailureException e) {
                    throw new CustomGlobalException(ErrorCode.OPTIMISTIC_EXCEPTION);
                } catch (Exception e) {
                    log.error("예외 : {}", e.getMessage());
                    return null;
                }
            });
        }

        long startTime = System.currentTimeMillis();
        List<Future<Pay>> futures = executorService.invokeAll(tasks);
        long endTime = System.currentTimeMillis();
        log.info("결제 {}개의 비동기 요청 총 수행 시간 : {}ms", REQUEST_AMOUNT, endTime - startTime);

        long successCount = futures.stream().filter(f -> {
            try {
                return f.get() != null;
            } catch (Exception e) {
                return false;
            }
        }).count();

        assertThat(successCount).isEqualTo(1);
        executorService.shutdown();
    }

    @Test
    @DisplayName("[낙관락(재시도x) - ExecutorService] 포인트 충전 동시성 테스트 - 1000번 요청 중 일부는 실패한다.")
    void chargeBalanceConcurrencyTestWithExecutorService() throws InterruptedException{
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        List<Callable<UserResult.ChargeBalance>> tasks = new ArrayList<>();
        ChargeBalance dto = ChargeBalance.builder().userId(1L).amount(BigDecimal.valueOf(10000)).build();

        for (int i = 0; i < REQUEST_AMOUNT; i++) {
            tasks.add(() -> {
                try {
                    return userFacade.chargeBalance(dto);
                } catch (Exception e) {
                    log.error("예외 : {}", e.getMessage());
                    return null;
                }
            });
        }

        long startTime = System.currentTimeMillis();
        List<Future<UserResult.ChargeBalance>> futures = executorService.invokeAll(tasks);
        long endTime = System.currentTimeMillis();
        log.info("포인트 충전 {}개의 비동기 요청 총 수행 시간 : {}ms", REQUEST_AMOUNT, endTime - startTime);

        long successCount = futures.stream().filter(f -> {
            try {
                return f.get() != null;
            } catch (Exception e) {
                return false;
            }
        }).count();

        assertNotEquals(successCount, 1000);
        executorService.shutdown();
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
