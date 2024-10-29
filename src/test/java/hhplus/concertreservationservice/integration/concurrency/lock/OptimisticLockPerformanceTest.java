package hhplus.concertreservationservice.integration.concurrency.lock;

import hhplus.concertreservationservice.application.concert.dto.ConcertCriteria;
import hhplus.concertreservationservice.application.concert.dto.ConcertResult;
import hhplus.concertreservationservice.application.concert.dto.ConcertResult.ReserveSeat;
import hhplus.concertreservationservice.application.concert.facade.ConcertReservationFacade;
import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@SpringBootTest
class OptimisticLockPerformanceTest {

    private static final Logger log = LoggerFactory.getLogger(OptimisticLockPerformanceTest.class);

    @Autowired
    private ConcertReservationFacade facade;

    @Test
    @DisplayName("낙관락 동시성 테스트 - 1000개 쓰레드의 요청")
    void concurrencyTest() {

        ConcertCriteria.ReserveSeat reserveSeatCriteria = ConcertCriteria.ReserveSeat.builder()
            .userId(1L)
            .concertSeatId(1L)
            .build();

        List<CompletableFuture<ConcertResult.ReserveSeat>> tasks = new ArrayList<>();

        // 동시에 10번의 충전 요청을 수행
        for (long i = 1; i <= 1000; i++) {

            tasks.add(CompletableFuture.supplyAsync(() -> {
                try {
                    ReserveSeat reserveSeat = facade.reserveSeat(reserveSeatCriteria);
                    log.info("좌석 예약 성공");
                    return reserveSeat;
                }catch (ObjectOptimisticLockingFailureException e) {
                    log.error("낙관락 : {}",e.getMessage());
                    return null;
                }catch (CustomGlobalException ee){
                    log.error("커스텀 : {}",ee.getMessage());
                    return null;
                }

            }));

        }

        CompletableFuture<Void> allTasks = CompletableFuture.allOf(
            tasks.toArray(new CompletableFuture[0]));
        allTasks.join();


    }

}
