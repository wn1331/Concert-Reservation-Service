# 콘서트 예약 서비스


## 마일스톤

[Concert Reservation Service Milestone](https://github.com/users/wn1331/projects/2)

## 시퀀스 다이어그램

#### 유저 대기열 토큰 발급 API

```mermaid
sequenceDiagram
  actor CLIENT as CLIENT(USER)
  participant API as 대기열API(토큰요청)
  participant Queue as Queue(대기열)

  CLIENT ->> API: API 요청
  activate CLIENT
  activate API
  API ->>+ Queue: 대기열토큰 발급 요청
  deactivate API
  activate Queue
  Queue ->> Queue: 대기열 토큰 생성 및 대기열에 등록
  Queue ->> API: 대기열토큰 반환
  deactivate Queue
  activate API
  API->>CLIENT: API 응답(토큰 응답)
  deactivate API
  deactivate CLIENT
```

#### 예약 가능 날짜 / 좌석 조회 API

```mermaid
 sequenceDiagram
  actor CLIENT as CLIENT(USER)
  participant API as 대기열API(토큰요청)
  participant Queue as Queue(대기열)
  participant Concert as Concert(콘서트)

  CLIENT ->> API: API 요청
  activate CLIENT
  activate API
  API ->>+ Queue: 토큰 검증 요청
  deactivate API
  activate Queue
  Queue ->> Queue: 토큰 검증

  alt [토큰 검증 실패]
    Queue ->> API: 토큰 검증 실패 반환
    deactivate Queue
    activate API
    API ->> CLIENT: API 응답(토큰 검증 실패)
    deactivate API
  else [토큰 검증 성공]
    Queue ->>+ Concert: 예약 가능 날짜 / 좌석 조회
    deactivate Queue
    activate Concert
    Concert ->> Concert: 예약 가능 날짜 / 좌석 조회

      Concert ->> API: 날짜 / 좌석 정보 반환
      deactivate Concert
      activate API
      API ->> CLIENT: API 응답(계층화된 리스트 응답)
      deactivate API
  end
  deactivate CLIENT

```

#### 좌석 예약 요청 API

```mermaid
sequenceDiagram
  actor CLIENT as CLIENT(USER)
  participant API as 좌석예약API
  participant Queue as Queue(대기열)
  participant Reservation as Reservation(예약)

  CLIENT ->> API: API 요청
  activate CLIENT
  activate API
  API ->>+ Queue: 토큰 검증
  deactivate API
  activate Queue
  Queue ->> Queue: 토큰 검증

  alt [토큰 검증 실패]
    Queue ->> CLIENT: 토큰 검증 실패 반환
    deactivate Queue
  else [토큰 검증 성공]

    Queue ->> API: 토큰 검증 성공 반환
    deactivate Queue
    activate API
    API ->>+ Reservation: 좌석 예약 요청
    deactivate API
    activate Reservation
    Reservation ->> Reservation: 좌석 예약

    alt [좌석 예약 실패]
    Reservation ->> CLIENT: 좌석 선점 실패(이미 선점됨)
    deactivate Reservation

    else [좌석 선점 성공]
    Reservation ->> API: 좌석 선점 성공 반환
        deactivate Reservation
        activate API
    API ->> CLIENT: API 응답(좌석 선점 성공)
    deactivate API
    end
  end
  deactivate CLIENT
```

#### 잔액 충전 / 조회 API

```mermaid
sequenceDiagram
  actor CLIENT as CLIENT(USER)
  participant API as 잔액 충전/조회API
  participant Queue as Queue(대기열)
  participant Balance as 잔액
  CLIENT ->> API: API 요청
  activate CLIENT
  activate API
  API ->>+ Queue: 토큰 검증
  deactivate API
  activate Queue
  Queue ->> Queue: 토큰 검증

  alt [토큰 검증 실패]
    Queue ->> CLIENT: 토큰 검증 실패 반환
    deactivate Queue
  else [토큰 검증 성공]

    Queue ->> API: 토큰 검증 성공 반환
    deactivate Queue
    activate API
    API ->>+ Balance: 잔액 충전/조회 요청
    deactivate API
    activate Balance
    Balance ->> Balance: 잔액 충전/조회

    Balance ->> API: 잔액 충전/조회 응답
    deactivate Balance
    activate API
    API ->> CLIENT: API 응답
    deactivate API
  end
  deactivate CLIENT
```

#### 결제 API

```mermaid
sequenceDiagram
  actor CLIENT as CLIENT(USER)
  participant API as 잔액 충전/조회API
  participant Queue as Queue(대기열)
  participant Payment as Payment(결제)
  participant Balance as Balance(잔액)
  CLIENT ->> API: API 요청
  activate CLIENT
  activate API
  API ->>+ Queue: 토큰 검증
  deactivate API
  activate Queue
  Queue ->> Queue: 토큰 검증

  alt [토큰 검증 실패]
    Queue ->> CLIENT: 토큰 검증 실패 반환
    deactivate Queue
  else [토큰 검증 성공]

    Queue ->> API: 토큰 검증 성공 반환
    deactivate Queue
    activate API
    API ->> Payment: 결제 요청
    deactivate API
    activate Payment
    Payment ->> Balance: 잔액 조회 요청
    deactivate Payment
    activate Balance
    Balance ->> Balance: 잔액 검증
    alt [잔액 부족]

        Balance ->> Payment: 잔액 부족 응답
        activate Payment
        Payment ->> CLIENT: 결제 처리 실패 반환
        deactivate Payment
    else [정상 처리]
        Balance ->> Balance: 잔액 차감
        
        Balance ->> Payment: 정상 처리 반환
        deactivate Balance
        activate Payment
        
        Payment ->> API: 결제 처리 응답
        deactivate Payment
    activate API
    end

    API ->> CLIENT: API 응답
    deactivate API
  end
  deactivate CLIENT
```


## FlowChart
#### 콘서트 예약 서비스 메인 흐름도
![flowchart.png](images/flowchart.png)


