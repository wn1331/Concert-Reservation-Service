package hhplus.concertreservationservice.producer;

import hhplus.concertreservationservice.domain.concert.event.producer.ConcertPaymentMessageProducer;
import hhplus.concertreservationservice.domain.queue.service.QueueService;
import hhplus.concertreservationservice.infra.persistence.concert.ConcertPaymentMessageKafkaProducer;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("[단위테스트] PaymentMessageProducer 단위테스트")
class ConcertPaymentMessageKafkaProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private ConcertPaymentMessageKafkaProducer producer;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        Field topic = ConcertPaymentMessageKafkaProducer.class.getDeclaredField("paymentSuccessTopic");
        topic.setAccessible(true);  // private 필드 접근 가능하도록 설정
        topic.set(producer, "payment-success-topic");

    }

    @Test
    @DisplayName("[성공] 메시지 producer 동작 성공")
    void sendSuccessMessage_shouldSendCorrectMessage() {
        // Given
        String key = "paymentKey";
        String outboxId = "outboxTest";
        String paymentSuccessTopic = "payment-success-topic";
        // When
        producer.sendSuccessMessage(key, outboxId);

        // Then
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

        verify(kafkaTemplate, times(1)).send(topicCaptor.capture(), keyCaptor.capture(), valueCaptor.capture());

        assertEquals(paymentSuccessTopic, topicCaptor.getValue());
        assertEquals(key, keyCaptor.getValue());
        assertEquals(outboxId, valueCaptor.getValue());
    }
}
