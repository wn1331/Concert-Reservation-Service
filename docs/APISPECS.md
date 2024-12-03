# 콘서트 예약 서비스 API 명세서

## 1. 콘서트 스케줄 조회 API

### **GET** `/concert/{concertId}/schedules`

#### **Request**
- **Headers**
    - `token`: `String` - 대기열 인증 토큰
- **Path Variables**
    - `concertId`: `Long` - 콘서트의 ID

#### **Response**
- **Status Code**: `200 OK`
- **Body**:
  ```json
  {
    "schedules": [
      {
        "scheduleId": 1,
        "date": "2024-10-01",
        "status": "매진됨"
      },
      {
        "scheduleId": 2,
        "date": "2024-10-02",
        "status": "예약가능"
      },
      {
        "scheduleId": 3,
        "date": "2024-10-03",
        "status": "예약가능"
      }
    ]
  }
  ```

#### **Possible Exceptions**
- **401 Unauthorized**: 대기열 토큰이 유효하지 않을 때
- **500 Internal Server Error**: 해당하는 콘서트 ID를 찾지 못했을 때

---

## 2. 콘서트 좌석 조회 API

### **GET** `/concert/schedule/{concertScheduleId}/seats`

#### **Request**
- **Headers**
    - `token`: `String` - 대기열 인증 토큰
- **Path Variables**
    - `concertScheduleId`: `Long` - 콘서트 스케줄의 ID

#### **Response**
- **Status Code**: `200 OK`
- **Body**:
  ```json
  {
    "concertSeats": [
      {
        "seatId": 1,
        "seatNo": 101,
        "status": "예약가능"
      },
      {
        "seatId": 2,
        "seatNo": 102,
        "status": "예약가능"
      },
      {
        "seatId": 3,
        "seatNo": 103,
        "status": "예약가능"
      },
      {
        "seatId": 4,
        "seatNo": 104,
        "status": "예약불가"
      }
    ]
  }
  ```

#### **Possible Exceptions**
- **401 Unauthorized**: 대기열 토큰이 유효하지 않을 때
- **500 Internal Server Error**: 해당하는 콘서트 스케줄 ID를 찾지 못했을 때

---

## 3. 콘서트 예약 API

### **POST** `/concert/reservation`

#### **Request**
- **Headers**
    - `token`: `String` - 대기열 인증 토큰
- **Body**:
  ```json
  {
    "userId": 1,
    "concertScheduleId": 2,
    "concertSeatId": 101
  }
  ```

#### **Response**
- **Status Code**: `200 OK`
- **Body**:
  ```json
  {
    "reservationId": 1
  }
  ```

#### **Possible Exceptions**
- **400 Bad Request**: 요청 본문이 유효하지 않을 때
- **401 Unauthorized**: 대기열 토큰이 유효하지 않을 때
- **500 Internal Server Error**: 해당하는 `concertScheduleId` 또는 `concertSeatId`이 없을 때

---

## 4. 콘서트 좌석 결제 API

### **POST** `/concert/reservation/{reservationId}/pay`

#### **Request**
- **Headers**
    - `token`: `String` - 대기열 인증 토큰
- **Path Variables**
    - `reservationId`: `Long` - 예약 ID
- **Body**:
  ```json
  {
    "userId": 1
  }
  ```

#### **Response**
- **Status Code**: `200 OK`
- **Body**:
  ```json
  {
    "reservationId": 1,
    "paymentStatus": "결제성공"
  }
  ```

#### **Possible Exceptions**
- **400 Bad Request**: 요청 본문이 유효하지 않을 때
- **401 Unauthorized**: 대기열 토큰이 유효하지 않을 때
- **500 Internal Server Error**: `reservationId`에 해당하는 예약 정보를 찾지 못했을 때

---

## 5. 대기열 생성 API

### **POST** `/queue/enqueue/{userId}`

#### **Request**
- **Path Variables**
    - `userId`: `Long` - 유저 ID

#### **Response**
- **Status Code**: `200 OK`
- **Body**:
  ```json
  {
    "token": "b05ac89f-3d47-4f09-96b3-9b54b7a7a6e7"
  }
  ```

#### **Possible Exceptions**
- **500 Internal Server Error**: `userId`에 해당하는 유저가 없을 때

---

## 6. 대기열 순번 확인 API

### **GET** `/queue/poll`

#### **Request**
- **Headers**
    - `token`: `String` - 대기열에서 발급받은 토큰

#### **Response**
- **Status Code**: `200 OK`
- **Body**:
  ```json
  {
    "userId": 1,
    "token": "b05ac89f-3d47-4f09-96b3-9b54b7a7a6e7",
    "order": 1
  }
  ```

#### **Possible Exceptions**
- **401 Unauthorized**: 토큰이 유효하지 않을 때

---

## 7. 유저 잔액 조회 API

### **GET** `/user/{userId}/balance`

#### **Request**
- **Headers**
    - `token`: `String` - 대기열 인증 토큰
- **Path Variables**
    - `userId`: `Long` - 유저 ID

#### **Response**
- **Status Code**: `200 OK`
- **Body**:
  ```json
  {
    "userId": 1,
    "amount": 100000
  }
  ```

#### **Possible Exceptions**
- **400 Bad Request**: 요청 본문이 유효하지 않을 때
- **401 Unauthorized**: 대기열 토큰이 유효하지 않을 때
- **404 Not Found**: `userId`에 해당하는 유저를 찾지 못했을 때

---

## 8. 유저 잔액 충전 API

### **POST** `/user/{userId}/balance`

#### **Request**
- **Headers**
    - `token`: `String` - 대기열 인증 토큰
- **Path Variables**
    - `userId`: `Long` - 유저 ID
- **Body**:
  ```json
  {
    "amount": 50000
  }
  ```

#### **Response**
- **Status Code**: `200 OK`
- **Body**:
  ```json
  {
    "userId": 1,
    "amount": 150000
  }
  ```

#### **Possible Exceptions**
- **400 Bad Request**: 요청 본문이 유효하지 않을 때
- **401 Unauthorized**: 대기열 토큰이 유효하지 않을 때
- **404 Not Found**: `userId`에 해당하는 유저를 찾지 못했을 때
