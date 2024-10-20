package hhplus.concertreservationservice.domain.concert.service;

import hhplus.concertreservationservice.domain.concert.dto.ConcertCommand;
import hhplus.concertreservationservice.domain.concert.dto.ConcertCommand.GetAvailableSeats;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo.AvailableSchedules;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo.AvailableSeats;
import hhplus.concertreservationservice.domain.concert.entity.ConcertSeat;
import hhplus.concertreservationservice.domain.concert.repository.ConcertScheduleRepository;
import hhplus.concertreservationservice.domain.concert.repository.ConcertSeatRepository;
import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import hhplus.concertreservationservice.global.exception.ErrorCode;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertScheduleRepository concertScheduleRepository;
    private final ConcertSeatRepository concertSeatRepository;

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

    public BigDecimal changeSeatStatusAndReturnPrice(Long concertSeatId){
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




}
