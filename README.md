# 콘서트 예약 서비스

<details><summary>마일스톤</summary>

[Concert Reservation Service Milestone](https://github.com/users/wn1331/projects/2)

</details>

<details><summary>Sequence Diagram</summary>

[Sequence Diagram](docs%2FSequenceDiagram.md)

</details>


<details><summary>FlowChart</summary>

#### 콘서트 예약 서비스 메인 흐름도
![flowchart.png](images/flowchart.png)

</details>

<details><summary>비즈니스 로직 예상 및 분석</summary>

[대기열을 DB로 구현하는 경우에서 콘서트 예약 서비스의 비즈니스 로직 예상 및 분석](docs%2FBusinessLogic.md)

</details>

<details><summary>ERD</summary>

```mermaid
erDiagram
    concert ||--|{ concert_schedule: "1:N"
    concert_schedule ||--|{ concert_seat: "1:N"
    concert_seat ||--|{ reservation: "1:N"
    user ||--|| user_detail: "1:1"
    user ||--|{ reservation: "1:N"
    user ||--|| Queue: "1:1"
    reservation ||--|| payment: "1:1"

    concert {
        long id PK "콘서트 PK"
        String name "콘서트 이름"
    }
    concert_schedule {
        long id PK "콘서트 스케줄 PK"
        long concert_id "콘서트 PK"
        datetime concert_datetime "날짜"
    }
    concert_seat {
        long id PK "콘서트 좌석 PK"
        long concert_schedule_id "콘서트 스케줄 PK"
        String seatNum "좌석번호"
        decimal price "좌석 가격"
        String status "좌석 상태"
        datetime created_at "생성일시"
        datetime modified_at "수정일시"
    }

    user {
        long id PK "사용자 PK"
        String name "사용자 명"
    }
    user_detail {
        long id PK "유저상세 PK"
        long userId "사용자 PK"
        decimal amount "잔액"
        datetime create_at "생성일시"
        datetime modified_at "수정일시"
    }

    payment {
        long id PK "결제 PK"
        long reservation_id "예약 PK"
        String seat_num "좌석 번호"
        String concert_name "콘서트 이름"
        datetime concert_datetime "콘서트 일시"
        decimal price "결제 금액"
        datetime created_at "생성일자"
    }

    reservation {
        long id PK "예약 PK"
        long user_id "사용자 PK"
        long id PK "예약 좌석 PK"
        long concert_seat_id "콘서트 좌석 PK"
        String status "예약 상태 (예약, 결제완료)"
        datetime created_at "생성일자"
        datetime modified_at "수정일자"
    }

    Queue {
        long id PK "대기열 PK"
        long user_id "유저 PK"
        String token UK "대기열 토큰(UUID)"
        String status "대기열 상태"
        datetime created_at "생성일자"
        datetime modified_at "수정일자"
    }

```

</details>

<details><summary>API 명세서</summary>

[콘서트 예약 서비스 API 명세서](docs%2FAPISPECS.md)

</details>

<details><summary>패키지 구조 및 기술 스택 선정</summary>

</details>



