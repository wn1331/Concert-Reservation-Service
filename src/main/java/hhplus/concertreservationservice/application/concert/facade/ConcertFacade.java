package hhplus.concertreservationservice.application.concert.facade;

import hhplus.concertreservationservice.domain.concert.service.ConcertService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConcertFacade {

    private final ConcertService concertService;





}
