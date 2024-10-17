package hhplus.concertreservationservice.domain;

import hhplus.concertreservationservice.domain.concert.entity.Concert;
import hhplus.concertreservationservice.domain.concert.entity.ConcertSchedule;
import hhplus.concertreservationservice.domain.concert.entity.ConcertSeat;
import hhplus.concertreservationservice.domain.concert.entity.SeatStatusType;
import hhplus.concertreservationservice.domain.concert.repository.ConcertRepository;
import hhplus.concertreservationservice.domain.concert.repository.ConcertScheduleRepository;
import hhplus.concertreservationservice.domain.concert.repository.ConcertSeatRepository;
import hhplus.concertreservationservice.domain.user.entity.User;
import hhplus.concertreservationservice.domain.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class DataInitializer {

    private final ConcertRepository concertRepository;
    private final ConcertScheduleRepository concertScheduleRepository;
    private final ConcertSeatRepository concertSeatRepository;
    private final UserRepository userRepository;

    @PostConstruct
    public void initializeData() {
        concertRepository.save(new Concert("아이유 콘서트"));
        concertRepository.save(new Concert("싸이 콘서트"));
        List<ConcertSchedule> schedules = Arrays.asList(
            new ConcertSchedule(1L, LocalDate.of(2024, 12, 1)),
            new ConcertSchedule(1L, LocalDate.of(2024, 12, 2)),
            new ConcertSchedule(1L, LocalDate.of(2023, 12, 3)),
            new ConcertSchedule(2L, LocalDate.of(2024, 12, 10)),
            new ConcertSchedule(2L, LocalDate.of(2024, 12, 11)),
            new ConcertSchedule(2L, LocalDate.of(2024, 12, 12))
        );
        concertScheduleRepository.saveAll(schedules);

        LongStream.rangeClosed(1, 6)
            .forEach(scheduleId -> {
                List<ConcertSeat> seats = IntStream.rangeClosed(1, 50)
                    .mapToObj(seatNumber -> new ConcertSeat(
                        scheduleId, "A" + seatNumber, BigDecimal.valueOf(150000), SeatStatusType.EMPTY))
                    .toList();

                concertSeatRepository.saveAll(seats);  // 한번에 저장
            });




    }
}
