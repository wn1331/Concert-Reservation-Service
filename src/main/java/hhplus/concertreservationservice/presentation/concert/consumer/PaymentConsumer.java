package hhplus.concertreservationservice.presentation.concert.consumer;

import static hhplus.concertreservationservice.global.utils.JsonUtil.fromJson;
import static hhplus.concertreservationservice.global.utils.JsonUtil.toJson;

import hhplus.concertreservationservice.domain.common.notification.NotificationClient;
import hhplus.concertreservationservice.domain.concert.event.ConcertPaymentSuccessEvent;
import hhplus.concertreservationservice.domain.outbox.entity.Outbox;
import hhplus.concertreservationservice.domain.outbox.service.OutboxService;
import hhplus.concertreservationservice.domain.queue.service.QueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentConsumer {

    private final NotificationClient notificationClient;
    private final OutboxService outboxService;
    private final QueueService queueService;


    @KafkaListener(topics = "${spring.kafka.producer.topic.payment-success}", groupId = "${spring.kafka.consumer.group-ids.payment-success}")
    public void sentPaymentNotification(ConsumerRecord<String,String> record){// 레코드로 아웃박스 식별값이 들어온다.
        // 아웃박스 식별값으로 아웃박스 조회.(init만 찾아옴. 없으면 Exception. 멱등)
        Outbox outbox = outboxService.findOutbox(record.value());

        ConcertPaymentSuccessEvent deserializedRecord = fromJson(outbox.getPayload(),
            ConcertPaymentSuccessEvent.class);
        try{
            // 알림전송. 실패 시 예외발생
            notificationClient.sendPaymentNotification(deserializedRecord);
            log.info("알림 전송에 성공했습니다.");

            // 대기열 만료는 성공/실패해도 에러나 로그가 나타나지 않도록 설계
            queueService.expireToken(deserializedRecord.token());

        }catch (Exception e){
            log.error("알림 전송에 실패했습니다.");
            throw e;
        }

        // 아웃박스 상태 변경
        outboxService.updateOutbox(outbox);
    }

}
