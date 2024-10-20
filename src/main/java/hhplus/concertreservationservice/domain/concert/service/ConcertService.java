package hhplus.concertreservationservice.domain.concert.service;

import hhplus.concertreservationservice.domain.concert.dto.ConcertCommand;
import hhplus.concertreservationservice.domain.concert.dto.ConcertCommand.GetAvailableSeats;
import hhplus.concertreservationservice.domain.concert.dto.ConcertCommand.Pay;
import hhplus.concertreservationservice.domain.concert.dto.ConcertCommand.ReserveSeat;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo.AvailableSchedules;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo.AvailableSeats;
import hhplus.concertreservationservice.domain.concert.entity.ConcertPayment;
import hhplus.concertreservationservice.domain.concert.entity.ConcertReservation;
import hhplus.concertreservationservice.domain.concert.entity.ConcertSeat;
import hhplus.concertreservationservice.domain.concert.entity.PaymentStatusType;
import hhplus.concertreservationservice.domain.concert.entity.ReservationStatusType;
import hhplus.concertreservationservice.domain.concert.entity.SeatStatusType;
import hhplus.concertreservationservice.domain.concert.repository.ConcertPaymentRepository;
import hhplus.concertreservationservice.domain.concert.repository.ConcertReservationRepository;
import hhplus.concertreservationservice.domain.concert.repository.ConcertScheduleRepository;
import hhplus.concertreservationservice.domain.concert.repository.ConcertSeatRepository;
import hhplus.concertreservationservice.domain.queue.entity.Queue;
import hhplus.concertreservationservice.domain.queue.repository.QueueRepository;
import hhplus.concertreservationservice.domain.user.entity.User;
import hhplus.concertreservationservice.domain.user.entity.UserPointHistory;
import hhplus.concertreservationservice.domain.user.entity.UserPointHistoryType;
import hhplus.concertreservationservice.domain.user.repository.UserPointHistoryRepository;
import hhplus.concertreservationservice.domain.user.repository.UserRepository;
import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import hhplus.concertreservationservice.global.exception.ErrorCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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



}
