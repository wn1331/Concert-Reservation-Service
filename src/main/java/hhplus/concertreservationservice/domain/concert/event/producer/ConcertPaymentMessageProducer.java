package hhplus.concertreservationservice.domain.concert.event.producer;

public interface ConcertPaymentMessageProducer {
    void sendSuccessMessage(String key, String outboxId);

}
