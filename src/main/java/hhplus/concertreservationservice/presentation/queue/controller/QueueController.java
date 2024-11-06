package hhplus.concertreservationservice.presentation.queue.controller;

import static org.springframework.http.ResponseEntity.ok;

import hhplus.concertreservationservice.application.queue.dto.QueueCriteria;
import hhplus.concertreservationservice.application.queue.dto.QueueCriteria.Order;
import hhplus.concertreservationservice.application.queue.facade.QueueFacade;
import hhplus.concertreservationservice.presentation.queue.dto.QueueRequest;
import hhplus.concertreservationservice.presentation.queue.dto.QueueResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/queues")
public class QueueController implements IQueueController{

    private final QueueFacade queueFacade;


    // 대기열 생성 API
    @PostMapping("/enqueue")
    public ResponseEntity<QueueResponse.Enqueue> enqueue(
        @RequestBody @Valid QueueRequest.Enqueue request
    ) {
        return ok(QueueResponse.Enqueue.fromResult(queueFacade.enqueue(request.toCriteria())));
    }


    // 폴링용 대기열 순번조회 API
    @GetMapping("/order")
    public ResponseEntity<QueueResponse.Order> getQueueStatus (@RequestHeader(value = "X-Access-Token", required = true) String token) {
        return ok().body(QueueResponse.Order.fromResult(queueFacade.getQueueOrder(new Order(token))));
    }


}
