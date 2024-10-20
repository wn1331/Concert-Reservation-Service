package hhplus.concertreservationservice.domain.concert.service;

import hhplus.concertreservationservice.domain.concert.dto.ConcertCommand.ReserveSeat;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo;
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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConcertReservationService {
    private static final Logger log = LoggerFactory.getLogger(ConcertReservationService.class);

    private final QueueRepository queueRepository;
    private final ConcertSeatRepository concertSeatRepository;
    private final ConcertReservationRepository concertReservationRepository;




    public ConcertInfo.ReserveSeat reserveSeat(ReserveSeat command) {
        // 예약 생성
        return ConcertInfo.ReserveSeat.builder()
            .reservationId(concertReservationRepository.save(
                    ConcertReservation.builder()
                        .userId(command.userId())
                        .concertSeatId(command.concertSeatId())
                        .status(ReservationStatusType.RESERVED)
                        .price(command.price())
                        .build())
                .getId())
            .build();
    }



    @Transactional
    public void expireReservationProcess() {

        // 만료된 예약 조회
        List<ConcertReservation> expiredReservations = concertReservationRepository.findExpiredReservations(
            ReservationStatusType.RESERVED,
            LocalDateTime.now().minusMinutes(5));
        log.info("expired reservation & seat size : {}, time : {}", expiredReservations.size(),
            LocalDateTime.now());

        // 만료된 예약 for루프 돌기
        expiredReservations.forEach(reservation -> {
            Queue queue = queueRepository.findByUserId(reservation.getUserId())
                .orElseThrow(() -> new CustomGlobalException(ErrorCode.USER_NOT_FOUND));

            // 대기열 제거
            queueRepository.delete(queue);

            //예약 취소 (RESERVED -> CANCELED)
            reservation.cancelReservation();

            //좌석 점유 해제
            ConcertSeat reservedSeat = concertSeatRepository.findByIdAndStatus(
                    reservation.getConcertSeatId(), SeatStatusType.RESERVED)
                .orElseThrow(() -> new CustomGlobalException(ErrorCode.CONCERT_SEAT_NOT_FOUND));

            // 좌석 상태 변경 (RESERVED -> EMPTY)
            reservedSeat.cancelSeatByReservation();

            log.info("expired reservation & seat id : {}, {}, time : {}", reservation.getId(),
                reservedSeat.getId(), LocalDateTime.now());

        });
    }

}
