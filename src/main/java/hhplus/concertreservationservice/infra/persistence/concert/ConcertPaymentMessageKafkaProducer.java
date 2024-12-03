package hhplus.concertreservationservice.infra.persistence.concert;

import hhplus.concertreservationservice.domain.concert.event.producer.ConcertPaymentMessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConcertPaymentMessageKafkaProducer implements ConcertPaymentMessageProducer {

    @Value("${spring.kafka.producer.topic.payment-success}")
    private String paymentSuccessTopic;

    private final KafkaTemplate<String,String> kafkaTemplate;

    @Override
    public void sendSuccessMessage(String key, String outboxId) {
        // key 값을 명시하는 send메서드를 사용할 경우 라운드로빈 방식으로 동작하지 않는다.
        // 단, 결제 ID를 key값으로 사용할 경우에는 각 결제끼리의 순차를 보장한다.(지금은 의미없지만 확장성 고려)
        kafkaTemplate.send(paymentSuccessTopic,key,outboxId);

    }
}
