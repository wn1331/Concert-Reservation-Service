package hhplus.concertreservationservice.domain.concert.service;

import hhplus.concertreservationservice.domain.concert.dto.ConcertCommand.Pay;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo;
import hhplus.concertreservationservice.domain.concert.entity.ConcertPayment;
import hhplus.concertreservationservice.domain.concert.entity.PaymentStatusType;
import hhplus.concertreservationservice.domain.concert.repository.ConcertPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConcertPaymentService {

    private final ConcertPaymentRepository concertPaymentRepository;

    @Transactional
    public ConcertInfo.Pay payReservation(Pay command) {

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
