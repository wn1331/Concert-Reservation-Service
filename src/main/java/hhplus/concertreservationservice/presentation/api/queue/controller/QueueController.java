package hhplus.concertreservationservice.presentation.api.queue.controller;

import static org.springframework.http.ResponseEntity.ok;

import hhplus.concertreservationservice.application.queue.facade.QueueFacade;
import hhplus.concertreservationservice.presentation.api.queue.dto.QueueRequest;
import hhplus.concertreservationservice.presentation.api.queue.dto.QueueResponse;
import hhplus.concertreservationservice.presentation.api.queue.dto.QueueResponse.Poll;
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
public class QueueController {

    private final QueueFacade queueFacade;


    // 대기열 생성 API
    @PostMapping("/enqueue")
    public ResponseEntity<QueueResponse.Enqueue> enqueue(
        @RequestBody QueueRequest.Enqueue request
    ) throws Exception {
        return ok(QueueResponse.Enqueue.fromResult(queueFacade.enqueue(request.toCriteria())));
    }

    // 대기열 순번 API
    @GetMapping("/poll")
    public ResponseEntity<QueueResponse.Poll> poll(
        @RequestHeader(name = "token") String token
    ){
        return ok(new Poll(1L,token,1L));
    }

}
