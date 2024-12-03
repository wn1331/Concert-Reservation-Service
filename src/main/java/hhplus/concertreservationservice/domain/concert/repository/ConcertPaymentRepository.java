package hhplus.concertreservationservice.domain.concert.repository;


import hhplus.concertreservationservice.domain.concert.entity.ConcertPayment;

public interface ConcertPaymentRepository {

    ConcertPayment save(ConcertPayment build);
}
