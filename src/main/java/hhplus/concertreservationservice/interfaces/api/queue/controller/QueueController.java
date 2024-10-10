package hhplus.concertreservationservice.interfaces.api.queue.controller;

import static org.springframework.http.ResponseEntity.ok;

import hhplus.concertreservationservice.interfaces.api.queue.dto.Enqueue;
import hhplus.concertreservationservice.interfaces.api.queue.dto.QueuePoll;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/queue")
public class QueueController {


    // 대기열 생성 API
    @PostMapping("/enqueue/{userId}")
    public ResponseEntity<Enqueue.Response> enqueue(
        @PathVariable(name = "userId") Long userId
    ){
        return ok(new Enqueue.Response(UUID.randomUUID().toString()));
    }

    // 대기열 순번 API
    @GetMapping("/poll")
    public ResponseEntity<QueuePoll.Response> poll(
        @RequestHeader(name = "token") String token
    ){
        return ok(new QueuePoll.Response(1L,token,1L));
    }

}
