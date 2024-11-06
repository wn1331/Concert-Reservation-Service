package hhplus.concertreservationservice.domain.concert.service;

import hhplus.concertreservationservice.domain.concert.dto.ConcertCommand.ReserveSeat;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo.ReservationStatus;
import hhplus.concertreservationservice.domain.concert.entity.ConcertReservation;
import hhplus.concertreservationservice.domain.concert.entity.ConcertSeat;
import hhplus.concertreservationservice.domain.concert.entity.ReservationStatusType;
import hhplus.concertreservationservice.domain.concert.entity.SeatStatusType;
import hhplus.concertreservationservice.domain.concert.repository.ConcertReservationRepository;
import hhplus.concertreservationservice.domain.concert.repository.ConcertSeatRepository;
import hhplus.concertreservationservice.domain.queue.entity.Queue;
import hhplus.concertreservationservice.domain.queue.repository.QueueRepository;
import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import hhplus.concertreservationservice.global.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConcertReservationService {

    private final QueueRepository queueRepository;
    private final ConcertSeatRepository concertSeatRepository;
    private final ConcertReservationRepository concertReservationRepository;




    @Transactional
    public ConcertInfo.ReserveSeat reserveSeat(ReserveSeat command) {
        // 좌석 조회( 낙관락 적용 )
        ConcertSeat concertSeat = concertSeatRepository.findById(command.concertSeatId())
            .orElseThrow(() -> new CustomGlobalException(ErrorCode.CONCERT_SEAT_NOT_FOUND));

        // 좌석 상태 변경
        concertSeat.reserveSeat();

        // 예약 생성
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


    public ReservationStatus changeReservationStatusPaid(Long reservationId) {
        ConcertReservation reservation = concertReservationRepository.findById(
                reservationId)
            .orElseThrow(() -> new CustomGlobalException(ErrorCode.CONCERT_RESERVATION_NOT_FOUND));

        // 예약 상태 검증 및 변경
        reservation.confirmPayment();

        // dto(info) 반환
        return ReservationStatus.builder()
            .reservationId(reservation.getId())
            .concertSeatId(reservation.getConcertSeatId())
            .price(reservation.getPrice())
            .build();
    }


    @Transactional
    public void expireReservationProcess() {

        // 만료된 예약 조회
        List<ConcertReservation> expiredReservations = concertReservationRepository.findExpiredReservations(
            ReservationStatusType.RESERVED,
            LocalDateTime.now().minusMinutes(5));
        log.info("Expired reservation & seat size : {}, time : {}", expiredReservations.size(),
            LocalDateTime.now());

        // 만료된 예약 for루프 돌기
        expiredReservations.forEach(reservation -> {
            // 유저의 대기열 조회
            Optional<Queue> optionalQueue = queueRepository.findByUserId(reservation.getUserId());

            if (optionalQueue.isPresent()) {
                // 대기열 제거
                queueRepository.delete(optionalQueue.get());
            } else {
                log.warn("User not found for reservation id: {}. Skipping queue deletion.", reservation.getId());
            }

            //예약 취소 (RESERVED -> CANCELED)
            reservation.cancelReservation();

            //좌석 점유 해제
            ConcertSeat reservedSeat = concertSeatRepository.findByIdAndStatus(
                    reservation.getConcertSeatId(), SeatStatusType.RESERVED)
                .orElseThrow(() -> new CustomGlobalException(ErrorCode.CONCERT_SEAT_NOT_FOUND));

            // 좌석 상태 변경 (RESERVED -> EMPTY)
            reservedSeat.cancelSeatByReservation();

            log.info("Expired reservation & seat id : {}, {}, time : {}", reservation.getId(),
                reservedSeat.getId(), LocalDateTime.now());

        });

        // 스케줄링 완료 로깅
        log.info("Complete scheduled expire reservations. time : {}", LocalDateTime.now());
    }

}
