package hhplus.concertreservationservice.domain.concert.service;

import hhplus.concertreservationservice.domain.concert.dto.ConcertCommand.Pay;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo.ReservationStatus;
import hhplus.concertreservationservice.domain.concert.entity.ConcertPayment;
import hhplus.concertreservationservice.domain.concert.entity.ConcertReservation;
import hhplus.concertreservationservice.domain.concert.entity.ConcertSeat;
import hhplus.concertreservationservice.domain.concert.entity.PaymentStatusType;
import hhplus.concertreservationservice.domain.concert.repository.ConcertPaymentRepository;
import hhplus.concertreservationservice.domain.concert.repository.ConcertReservationRepository;
import hhplus.concertreservationservice.domain.concert.repository.ConcertSeatRepository;
import hhplus.concertreservationservice.domain.user.entity.User;
import hhplus.concertreservationservice.domain.user.entity.UserPointHistory;
import hhplus.concertreservationservice.domain.user.entity.UserPointHistoryType;
import hhplus.concertreservationservice.domain.user.repository.UserPointHistoryRepository;
import hhplus.concertreservationservice.domain.user.repository.UserRepository;
import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import hhplus.concertreservationservice.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConcertPaymentService {

    private final UserRepository userRepository;
    private final UserPointHistoryRepository userPointHistoryRepository;
    private final ConcertReservationRepository concertReservationRepository;
    private final ConcertPaymentRepository concertPaymentRepository;
    private final ConcertSeatRepository concertSeatRepository;

    @Transactional
    public ConcertInfo.Pay payReservation(Pay command) {

        ConcertReservation reservation = concertReservationRepository.findById(
                command.reservationId())
            .orElseThrow(() -> new CustomGlobalException(ErrorCode.CONCERT_RESERVATION_NOT_FOUND));

        // 예약 상태 검증 및 변경
        reservation.confirmPayment();

        User user = userRepository.findByIdForUsePoint(reservation.getUserId())
            .orElseThrow(() -> new CustomGlobalException(ErrorCode.USER_NOT_FOUND));

        if (user.getPoint().compareTo(reservation.getPrice()) > 0) {
            // 변경감지. 포인트 사용
            user.pointUse(reservation.getPrice());

        } else {
            throw new CustomGlobalException(ErrorCode.NOT_ENOUGH_BALANCE);
        }
        // 포인트 사용내역 저장
        userPointHistoryRepository.save(UserPointHistory.builder()
            .userId(user.getId())
            .requestPoint(command.price())
            .type(UserPointHistoryType.PAY)
            .build()
        );

        ConcertSeat concertSeat = concertSeatRepository.findById(reservation.getConcertSeatId())
            .orElseThrow(() -> new CustomGlobalException(ErrorCode.CONCERT_SEAT_NOT_FOUND));

        concertSeat.confirmSeatByPayment();

        // 결제 생성 후 반환
        return ConcertInfo.Pay.builder()
            .paymentId(concertPaymentRepository.save(ConcertPayment.builder()
                        .reservationId(command.reservationId())
                        .price(command.price())
                        .status(PaymentStatusType.SUCCEED)
                        .build()
                    )
                    .getId()
            )
            .build();
    }




}
