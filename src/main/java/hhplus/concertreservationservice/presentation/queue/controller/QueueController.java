package hhplus.concertreservationservice.presentation.queue.controller;

import static org.springframework.http.ResponseEntity.ok;

import hhplus.concertreservationservice.application.queue.facade.QueueFacade;
import hhplus.concertreservationservice.presentation.queue.dto.QueueRequest;
import hhplus.concertreservationservice.presentation.queue.dto.QueueResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
        @RequestBody @Valid QueueRequest.Enqueue request
    ) {
        return ok(QueueResponse.Enqueue.fromResult(queueFacade.enqueue(request.toCriteria())));
    }


}
