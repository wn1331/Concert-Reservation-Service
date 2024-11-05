package hhplus.concertreservationservice.domain.concert.service;

import hhplus.concertreservationservice.domain.concert.dto.ConcertCommand;
import hhplus.concertreservationservice.domain.concert.dto.ConcertCommand.GetAvailableSeats;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo.AvailableSchedules;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo.AvailableSeats;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo.Create;
import hhplus.concertreservationservice.domain.concert.entity.Concert;
import hhplus.concertreservationservice.domain.concert.entity.ConcertSchedule;
import hhplus.concertreservationservice.domain.concert.entity.ConcertSeat;
import hhplus.concertreservationservice.domain.concert.entity.SeatStatusType;
import hhplus.concertreservationservice.domain.concert.repository.ConcertRepository;
import hhplus.concertreservationservice.domain.concert.repository.ConcertScheduleRepository;
import hhplus.concertreservationservice.domain.concert.repository.ConcertSeatRepository;
import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import hhplus.concertreservationservice.global.exception.ErrorCode;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final ConcertScheduleRepository concertScheduleRepository;
    private final ConcertSeatRepository concertSeatRepository;

    @Transactional
    public Create create(ConcertCommand.Create command) {
        // 콘서트 생성
        Concert concert = concertRepository.save(Concert.builder().title(command.title()).build());

        // 콘서트 스케줄 생성
        command.dates().forEach(schedule -> {
            ConcertSchedule concertSchedule = concertScheduleRepository.save(
                ConcertSchedule.builder()
                    .concertId(concert.getId())
                    .concertDateTime(schedule)
                    .build());

            // 콘서트 좌석 생성
            IntStream.rangeClosed(1, command.seatAmount()).forEach(it ->
                concertSeatRepository.save(ConcertSeat.builder()
                    .concertScheduleId(concertSchedule.getConcertId())
                    .seatNum("B" + it)
                    .status(SeatStatusType.EMPTY)
                    .price(command.price())
                    .build())
            );
        });

        return new ConcertInfo.Create(concert.getId());
    }

    @Transactional(readOnly = true)
    public ConcertInfo.AvailableSchedules getAvailableSchedules(
        ConcertCommand.GetAvailableSchedules command) {
        return AvailableSchedules.fromEntityList(
            concertScheduleRepository.findByConcertIdAndConcertDateAfter(
                command.concertId()));

    }

    @Transactional(readOnly = true)
    public ConcertInfo.AvailableSeats getAvailableSeats(GetAvailableSeats command) {
        return AvailableSeats.fromEntityList(
            concertSeatRepository.findByConcertScheduleIdAndStatus(command.concertScheduleId())
        );
    }

    public BigDecimal changeSeatStatusAndReturnPrice(Long concertSeatId) {
        // 좌석 조회( 낙관락 적용 )
        ConcertSeat concertSeat = concertSeatRepository.findById(concertSeatId)
            .orElseThrow(() -> new CustomGlobalException(ErrorCode.CONCERT_SEAT_NOT_FOUND));

        // 좌석 상태 변경
        switch (concertSeat.getStatus()) {
            case EMPTY -> concertSeat.reserveSeat();
            case RESERVED -> throw new CustomGlobalException(ErrorCode.ALREADY_RESERVED_SEAT);
            case SOLD -> throw new CustomGlobalException(ErrorCode.ALREADY_SOLD_SEAT);
        }

        return concertSeat.getPrice();
    }

    public void changeSeatStatusPaid(Long concertSeatId) {
        ConcertSeat concertSeat = concertSeatRepository.findById(concertSeatId)
            .orElseThrow(() -> new CustomGlobalException(ErrorCode.CONCERT_SEAT_NOT_FOUND));

        concertSeat.confirmSeatByPayment();
    }


    @Transactional(readOnly = true)
    public List<ConcertInfo.Concert> getConcertList() {
        return concertRepository.findAll().stream().map(i->new ConcertInfo.Concert(i.getId(),i.getTitle())).toList();
    }
}
