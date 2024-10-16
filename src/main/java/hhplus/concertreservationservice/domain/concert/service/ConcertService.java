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
import hhplus.concertreservationservice.domain.user.entity.User;
import hhplus.concertreservationservice.domain.user.entity.UserPointHistory;
import hhplus.concertreservationservice.domain.user.entity.UserPointHistoryType;
import hhplus.concertreservationservice.domain.user.repository.UserPointHistoryRepository;
import hhplus.concertreservationservice.domain.user.repository.UserRepository;
import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import hhplus.concertreservationservice.global.exception.ErrorCode;
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

    private static final Logger log = LoggerFactory.getLogger(ConcertService.class);

    private final ConcertScheduleRepository concertScheduleRepository;
    private final ConcertSeatRepository concertSeatRepository;
    private final ConcertReservationRepository concertReservationRepository;
    private final ConcertPaymentRepository concertPaymentRepository;
    private final UserRepository userRepository;
    private final UserPointHistoryRepository userPointHistoryRepository;

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


    public ConcertInfo.ReserveSeat reserveSeat(ReserveSeat command) {
        ConcertSeat concertSeat = concertSeatRepository.findById(command.concertSeatId())
            .orElseThrow(() -> new CustomGlobalException(ErrorCode.CONCERT_SEAT_NOT_FOUND));

        switch (concertSeat.getStatus()) {
            case EMPTY -> concertSeat.reserveSeat();
            case RESERVED -> throw new CustomGlobalException(ErrorCode.ALREADY_RESERVED_SEAT);
            case SOLD -> throw new CustomGlobalException(ErrorCode.ALREADY_SOLD_SEAT);
        }

        return ConcertInfo.ReserveSeat.builder()
            .reservationId(concertReservationRepository.save(
                    ConcertReservation.builder()
                        .userId(command.userId())
                        .concertSeatId(command.concertSeatId())
                        .status(ReservationStatusType.RESERVED)
                        .price(concertSeat.getPrice())
                        .build())
                .getId())
            .build();
    }


    @Transactional
    public ConcertInfo.Pay payReservation(Pay command) {
        // 예약 조회
        ConcertReservation reservation = concertReservationRepository.findById(
                command.reservationId())
            .orElseThrow(() -> new CustomGlobalException(ErrorCode.CONCERT_RESERVATION_NOT_FOUND));

        // 예약 상태 검증 및 변경
        reservation.confirmPayment();

        // 유저 조회, 유저 잔액 검증-차감 (비관락 적용)
        User user = userRepository.findById(command.userId())
            .orElseThrow(() -> new CustomGlobalException(ErrorCode.USER_NOT_FOUND));

        if (user.getPoint().compareTo(reservation.getPrice()) > 0) {
            // 변경감지. 포인트 사용
            user.pointUse(reservation.getPrice());

        } else {
            throw new CustomGlobalException(ErrorCode.NOT_ENOUGH_BALANCE);
        }

        // 좌석 조회, 좌석 상태 변경
        ConcertSeat concertSeat = concertSeatRepository.findById(reservation.getConcertSeatId())
            .orElseThrow(() -> new CustomGlobalException(ErrorCode.CONCERT_SEAT_NOT_FOUND));

        concertSeat.confirmSeatByPayment();

        // 포인트 사용내역 저장
        userPointHistoryRepository.save(UserPointHistory.builder()
            .userId(user.getId())
            .requestPoint(reservation.getPrice())
            .type(UserPointHistoryType.PAY)
            .build()
        );

        // 결제 생성 후 반환
        return ConcertInfo.Pay.builder()
            .paymentId(concertPaymentRepository.save(ConcertPayment.builder()
                        .reservationId(reservation.getId())
                        .price(reservation.getPrice())
                        .status(PaymentStatusType.SUCCEED)
                        .build()
                    )
                    .getId()
            )
            .build();
    }

    public void expireReservationProcess() {
        List<ConcertReservation> expiredReservations = concertReservationRepository.findExpiredReservations(
            ReservationStatusType.RESERVED,
            LocalDateTime.now().minusMinutes(5));
        log.info("expired reservation & seat size : {}, time : {}", expiredReservations.size(),
            LocalDateTime.now());

        expiredReservations.forEach(reservation -> {

            // 변경감지!! 예약 상태 변경 (RESERVED -> CANCELED)
            reservation.cancelReservation();

            ConcertSeat reservedSeat = concertSeatRepository.findByIdAndStatus(
                    reservation.getConcertSeatId(), SeatStatusType.RESERVED)
                .orElseThrow(() -> new CustomGlobalException(ErrorCode.CONCERT_SEAT_NOT_FOUND));

            // 변경감지!! 좌석 상태 변경 (RESERVED -> EMPTY)
            reservedSeat.cancelSeatByReservation();

            log.info("expired reservation & seat id : {}, {}, time : {}", reservation.getId(),
                reservedSeat.getId(), LocalDateTime.now());

        });
    }
}
