
package hhplus.concertreservationservice.integration.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9093", "port=9093"})
class KafkaConnectionTest {

    private static final Logger log = LoggerFactory.getLogger(KafkaConnectionTest.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
//    private KafkaTemplate<String, ConcertPayment> kafkaTemplate;

    private String receivedMessage;
//    private ConcertPayment receivedMessage;


    @Test
    @DisplayName("[성공] 카프카 연동 테스트, 브로커에 test-topic 토픽으로 test-message를 전송하고 최대 10초 대기하면서 consumer가 받는 메시지와 일치한지 확인한다.")
    void produceTestMessage() {
        String topic = "test-topic";
        String message = "test-message";
//        ConcertPayment message = ConcertPayment.builder().price(BigDecimal.valueOf(123)).build();

        kafkaTemplate.send(topic, message);
        log.info("토픽 : {} , 보낸 메시지 : {}",topic,message);
        await()
            .atMost(Duration.ofSeconds(10))
            .untilAsserted(() -> {
                log.info("받은 메시지 : {}", receivedMessage);
                assertThat(receivedMessage).isEqualTo(message);
            });
    }

    @KafkaListener(topics = "test-topic", groupId = "test-group")
    public void consumeTestMessage(String message) {
        this.receivedMessage = message;
    }

}

