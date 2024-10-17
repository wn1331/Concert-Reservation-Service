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
    user ||--|{ user_point_history: "1:N"
    user ||--|{ reservation: "1:N"
    user ||--|| Queue: "1:1"
    reservation ||--|| payment: "1:1"

    concert {
        long id PK "콘서트 PK"
        VARCHAR title "콘서트 이름"
    }
    concert_schedule {
        long id PK "콘서트 스케줄 PK"
        long concert_id "콘서트 PK"
        datetime concert_date "날짜"
    }
    concert_seat {
        long id PK "콘서트 좌석 PK"
        long concert_schedule_id "콘서트 스케줄 PK"
        VARCHAR seat_num "좌석번호"
        decimal price "좌석 가격"
        VARCHAR status "좌석 상태(비어있음, 예약됨, 사용불가)"
        datetime created_at "생성일자"
        datetime modified_at "수정일자"
    }

    user {
        long id PK "사용자 PK"
        VARCHAR name "사용자 명"
        Decimal point "잔액"
    }

    user_point_history {
        long id PK "포인트 사용내역 PK"
        long user_id "사용자 PK"
        VARCHAR type "포인트 사용 타입"
        Decimal request_point "요청된 포인트"
        datetime created_at "생성일자"
    }

    payment {
        long id PK "결제 PK"
        long reservation_id "예약 PK"
        decimal price "결제 금액"
        datetime created_at "생성일자"
        datetime modified_at "수정일자"
        VARCHAR status "결제 상태(성공, 실패)"
    }

    reservation {
        long id PK "예약 PK"
        long user_id "사용자 PK"
        long concert_seat_id "콘서트 좌석 PK"
        VARCHAR status "예약 상태 (예약, 결제완료)"
        datetime created_at "생성일자"
        datetime modified_at "수정일자"
    }

    Queue {
        long id PK "대기열 PK"
        long user_id "유저 PK"
        VARCHAR queueToken UK "대기열 토큰(UUID)"
        VARCHAR status "대기열 상태"
        datetime created_at "생성일자"
        datetime modified_at "수정일자"
    }

```

</details>

<details><summary>API 명세서</summary>

[콘서트 예약 서비스 API 명세서](docs%2FAPISPECS.md)

</details>

<details><summary>패키지 구조 및 기술 스택 선정</summary>

### 패키지 구조
```bash
├── /interfaces
│    └── /api
│        ├── /concert
│        │    ├── /controller
│        │    └── /dto
│        ├── /queue
│        │    ├── /controller
│        │    └── /dto
│        └── /user
│             ├── /controller
│             └── /dto
├── /application
│    ├── /concert
│    │    ├── /service 
│    ├── /queue
│    │    ├── /service
│    └── /user
│         └── /service 
├── /domain
│    ├── /concert
│    │    ├── /repository
│    │    └── /entity
│    ├── /queue
│    │    ├── /repository
│    │    └── /entity
│    └── /user
│         ├── /repository
│         └── /entity
└── /infra
     ├── /persistence
     │    ├── /concert
     │    ├── /queue
     │    └── /user
     └── /external


Service 클래스 내부에 inner class로 Command/Info를 구현 예정입니다.


```

### 기술 스택 선정

- **자바 버전**: 17
- **스프링 부트 버전**: 3.3.4
- **Gradle 버전**: 8.10.2
- **외부 라이브러리**: JPA, Redis
- **사용할 RDBMS**: H2
- **Test RDBMS**: H2
- **테스트 도구**: JUnit, AssertJ

</details>



