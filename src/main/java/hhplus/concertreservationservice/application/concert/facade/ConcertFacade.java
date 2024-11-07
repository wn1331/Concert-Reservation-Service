package hhplus.concertreservationservice.application.concert.facade;

import hhplus.concertreservationservice.application.concert.dto.ConcertCriteria;
import hhplus.concertreservationservice.application.concert.dto.ConcertResult;
import hhplus.concertreservationservice.application.concert.dto.ConcertResult.AvailableSchedules;
import hhplus.concertreservationservice.application.concert.dto.ConcertResult.AvailableSeats;
import hhplus.concertreservationservice.application.concert.dto.ConcertResult.GetConcertList;
import hhplus.concertreservationservice.domain.concert.service.ConcertService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConcertFacade {

    private final ConcertService concertService;

    public ConcertResult.GetConcertList getConcertList(){
        return new GetConcertList(concertService.getConcertList().concertInfoList().stream()
            .map(i->new ConcertResult.Concerts(i.id(),i.title()))
            .toList());
    }

    public ConcertResult.Create create(ConcertCriteria.Create criteria) {
        return ConcertResult.Create.fromInfo(concertService.create(criteria.toCommand()));
    }

    @Transactional(readOnly = true)
    public ConcertResult.AvailableSchedules getAvailableSchedules(ConcertCriteria.GetAvailableSchedules criteria) {
        // 콘서트 스케줄 조회 (날짜가 지나지 않은 것들만)
        return AvailableSchedules.fromInfo(concertService.getAvailableSchedules(criteria.toCommand()));
    }


    @Transactional(readOnly = true)
    public ConcertResult.AvailableSeats getAvailableSeats(ConcertCriteria.GetAvailableSeats criteria) {
        // 콘서트 좌석 조회
        return AvailableSeats.fromInfo(concertService.getAvailableSeats(criteria.toCommand()));
    }

}
