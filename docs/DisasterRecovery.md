# 장애 대응(+가상 상황) 보고서

---

## 성능 테스트(STEP19) 요약

### 개요
- **목적**: 콘서트 예약 서비스의 성능을 평가하고 주요 병목 지점을 파악하여 개선 방안을 도출하는것.
- **테스트 환경**:
    - **Docker 리소스 제한**: CPU 2Core, RAM 4GB
    - **테스트 데이터**: 콘서트 100개, 스케줄 1만 개, 좌석 150만 개
    - **최대 Vuser**: 150
- **부하 테스트 시나리오**:
    1. 대기열 생성
    2. 대기열 순번 조회
    3. 콘서트 정보 조회
    4. 콘서트 스케줄 조회
    5. 콘서트 좌석 조회
    6. 좌석 예약
    7. 결제

---

### 부하테스트 시나리오 성능 결과 요약

| 지표                 | 결과                 | 설명                                    |
|--------------------|--------------------|---------------------------------------|
| **총 요청 수**       | 2071              | 7단계 API 요청을 포함한 총 호출 수         |
| **총 시나리오 실행** | 172               | 모든 단계의 시나리오 성공 횟수             |
| **성공률**          | 100%              | 모든 요청이 성공적으로 처리됨              |
| **p99 응답 시간**    | 2.77초            | 상위 1% 요청의 응답 시간                  |
| **p95 응답 시간**    | 1.93초            | 상위 5% 요청의 응답 시간                  |
| **평균 응답 시간**   | 388ms             | 전체 요청의 평균 응답 시간                 |

---

### 성능 결과 및 Grafana 모니터링을 토대로 병목 지점 분석 및 개선 방안

#### 1. Redis 병목 
- **문제**: 대기열 생성 및 분산락 처리에서 Redis 경합이 발생하여 성능 저하
- **분석**:
    - Redis는 싱글 스레드로 작동하며, 대량의 데이터 읽기/쓰기 요청이 몰릴 경우 병목 현상이 발생한다.
- **개선 방안**:
    1. **Redis Sentinel**: 고가용성 아키텍처를 구성하여 장애 발생 시 자동 페일오버를 수행하고 서비스 중단을 최소화한다.
    2. **Redis 클러스터링**: 데이터를 샤딩하여 각 노드로 분산하고 읽기/쓰기 부하를 분산시킨ㄷ다.
    3. **Lua 스크립트**: Redis에서 복잡한 논리를 처리할 때 네트워크 왕복을 줄이고 원자성을 보장하여 성능을 개선할 수 있다.

#### 2. Kafka 병목 
- **문제**: 결제 단계에서 Kafka 메시지 처리 지연 및 브로커 과부하
- **분석**:
    - Kafka 브로커와 프로듀서 간의 메시지 처리 속도 차이가 발생하여 큐에 과부하가 생겼다.
    - 파티션 개수가 충분하지 않아(3개였다) 병렬 처리 성능이 제한되었다.
- **개선 방안**:
    1. **파티션 추가**: 파티션 수를 늘려 메시지 처리 병렬성을 높이고 처리량을 증가시킨다.
    3. **Dead Letter Queue (DLQ)**: 현재 아웃박스 패턴으로 설계되어 있는데, 실패한 메시지를 추적하여 재처리하거나 분석할 수 있도록 DLQ로 바꾼다. 아웃박스보다 DLQ가 성능 상으로는 이점이 더 많다고 한다. (DB부하 적음, 지연시간 적음. 하지만 일관성 문제가 있음)
    4. **Kafka 모니터링**: 브로커 및 클러스터 상태를 지속적으로 모니터링하고 장애 발생 시 자동 복구 기능이 있다고 한다.

#### 3. 서버 리소스 부족
- **문제**: 제한된 CPU와 RAM으로 인해 응답 시간이 증가하고 병목이 발생
- **분석**:
    - CPU가 과도하게 사용되면서 중요한 작업의 우선순위가 낮아지고 처리 속도가 매우 저하됨.
    - 메모리 관리가 최적화되지 않아 GC(Garbage Collection)로 인한 지연이 발생할 수 있다.
- **개선 방안**:
    1. **스케일아웃**: 로드밸런서를 통해 여러 서버로 부하를 분산하고 처리 능력을 확장힌디.
    2. **스케일업**: CPU 코어와 RAM 용량을 확장하여 리소스를 늘린다.
    3. **GC 튜닝**: JVM 설정을 최적화하여 GC 지연 시간을 줄이고 메모리 사용 효율성을 높인다.. (고난이도 예상)

#### 4. 캐싱 미적용
- **문제**: 스케줄 및 좌석 조회 API에서 캐싱이 적용되지 않아 응답 속도가 느림
- **분석**:
    - 좌석 조회는 동시 요청이 많아 캐싱 없이 처리하면 부하가 집중된다.  
    - 하지만 자주 변경되는 데이터라 성능상의 트레이드오프를 고려해야 한다.
- **개선 방안**:
    1. **스케줄 조회 캐싱**: 자정까지의 시간을 TTL을 설정하여 유효 기간 내 데이터를 캐싱하여 처리한다.
    2. **좌석 조회 캐싱**: 좌석예약시마다 캐시를 삭제하도록 설정한다.

---

## 장애 대응 계획

### 장애란?
서비스가 정상 작동하지 못하는 상태를 의미하며, 장애 대응을 통해 서비스의 가용성을 확보하는 것이 중요하다.

---

### 내부 서비스 장애

#### 애플리케이션 장애
1. **메모리 누수**
    - **대응 방안**:
        - 긴급 재시작 스크립트를 통해 즉각적인 조치
        - 메모리 누수를 조기에 감지할 수 있도록 모니터링 시스템 강화
        - 애플리케이션 메모리 관리 방식을 재설계

2. **CPU 과부하**
    - **대응 방안**:
        - 주요 작업에 우선순위를 부여하고 비효율적인 작업을 최적화
        - CPU 사용량이 높은 로직을 비동기 작업으로 전환
        - 멀티스레딩 및 분산 처리를 도입해 전반적인 아키텍처 개선

#### 데이터베이스 장애
1. **잠금(Lock) 충돌**
    - **대응 방안**:
        - 실시간 트랜잭션 모니터링을 통해 장시간 잠금을 강제 롤백
        - 잠금 충돌이 발생하는 쿼리를 최적화하고 인덱스를 적절히 추가
        - 샤딩 및 파티셔닝 전략을 도입하여 병렬 처리를 강화

2. **쿼리 성능 저하**
    - **대응 방안**:
        - 비효율적인 쿼리를 실행 계획 분석을 통해 최적화 (인덱스 등)
        - 데이터베이스 아키텍처를 개선하여 성능을 개선(읽기-쓰기 분리, 마스터 슬레이브 등)

#### 네트워크 장애
- **대응 방안**:
    - 실시간 트래픽 분석 및 우선순위 관리
    - 네트워크 병목 지점을 식별하고 경로를 최적화
    - 트래픽 부하 분산을 위한 자동화된 관리 시스템 도입

---
# 가상의 상황 및 대응 방안

---

## 가상 상황 A: 대규모 콘서트 예약 대란

### **상황**
9년만에 복귀한 지드래곤 대규모 콘서트 예약이 막 시작되었다.  
순식간에 10만 명의 사용자가 예약 시작 시간에 동시 접속하여 좌석 조회 및 예약을 시도한다.  
서비스는 Redis와 데이터베이스를 사용해 대기열과 예약 상태를 관리하며, Kafka를 통해 예약 완료 메시지를 처리힌다.  

**이러한 상황에서, 다음과 같은 문제가 발생했다...!**
1. 트래픽 폭주로 인해 서버 응답 지연, 일부 요청 실패 발생
2. 사용자 불만 급증, 유저 대거 이탈 가능성 높음

---

### **분석**
- Redis는 싱글 스레드 특성으로 인해 대량의 읽기/쓰기 요청이 몰리며 병목이 발생했다.
- Kafka의 브로커와 파티션 수가 과소설정되어 처리량이 한계에 도달했다.
- Redis Pub/Sub Lock 방식은 요청 폭증 상황에서 락 경합이 증가해 성능 저하 및 지연 시간이 발생해버렸다.
---

### **대응 방안**

#### Redis
1. **단기 대응**
    - Redis Sentinel로 장애 노드가 발생하면 자동 페일오버하여 서비스 중단을 방지한다.
    - 자주 갱신되는 데이터를 데이터베이스로 읽도록 전환하는 백업 계획 실행.
    - 분산락 사용 중 요청 폭증 시 적절한 만료 시간을 설정해 충돌을 줄이고 재시도 간격을 조정한다.  
    - 분산락 사용 중 동일 키에 대한 동시 요청 시 충돌 최소화를 위해 백오프 전략을 도입한다.

   
2. **중기 대응**
    - Redis 클러스터링 도입으로 데이터 샤딩을 통해 읽기/쓰기 부하를 분산한다.
    - Lua 스크립트를 사용해 분산락과 대기열 작업을 원자적으로 처리하여 성능 최적화.
    - 분산락 사용 시 좌석별로 세분화된 락 키를 사용하여 충돌 가능성을 낮춘다.
    - 락 히스토리 저장. 충돌이 잦은 락 키를 식별하고, 캐싱 및 분산 처리 방안을 도입.  
   

3. **장기 대응**
    - 락 경합 감소 전략. 요청 분산을 위해 락의 범위를 축소하거나, 특정 키 기반 샤딩을 도입한다.   
    - 자주 갱신되지 않는 데이터를 캐싱하고, 캐싱 데이터를 활용하여 API 요청을 분산.
    - 레디스 성능 최적화. 락 요청과 다른 데이터 작업을 별도 클러스터로 분리.  

<br>

#### Kafka
1. **단기 대응**
    - 메시지 처리 속도가 느린 브로커를 격리하고 정상 브로커로 트래픽 재분배.
    - DLQ(Dead Letter Queue)에 메시지를 저장하여 손실 방지.

2. **중기 대응**
    - 파티션 수를 늘려 메시지 병렬 처리량을 증가.
    - Kafka Consumer를 병렬로 확장하여 메시지 처리 속도를 개선.

3. **장기 대응**
    - Kafka 모니터링 시스템(예: Prometheus, Grafana)으로 브로커 상태를 실시간으로 추적하고 장애 자동 복구 설정.

#### Database
1. **단기 대응**
    - 읽기 전용 복제본을 설정하여 읽기 요청을 분산 처리.
    - 어플리케이션에서 읽기/쓰기 트래픽을 구분하도록 설정.
    - 예약 관련 주요 쿼리에 인덱스 추가하여 쿼리 성능 향상.
 
2. **중기 대응**
    - 테이블 정규화 및 비정규화 조정으로 쿼리 성능 향상.
    - 좌석 정보를 샤딩하여 이벤트 또는 좌석 섹션 단위로 데이터를 분리.
    - 각 샤드에서 병렬 처리를 통해 동시 요청을 분산.

3. **장기 대응**
    - CQRS 패턴을 적용해 읽기/쓰기 워크로드를 물리적으로 분리.
    - 예약 요청은 비동기로 처리하고 읽기는 최적회된 데이터베이스에서 제공하는 방식 고려.
    - RDB라면 NoSQL로 변경 또는 추가할 수 있을 지 검토.
    - DB 오토 스케일링 설정.
    - 모니터링 도구(exporter+prometheus+grafana)를 사용하여 장애 조기 감지 및 자동 대응 시스템 구축.

---

## 가상 상황 B: 외부 API 장애

### **상황**
다날이나 페이코같은 외부 결제 게이트웨이를 사용한다고 가정해보자.  
과부하로 인해 응답 시간이 10초 이상 지연되거나 일부 호출이 실패하는 상황이 발생했다..!

**이렇게 되면 결과적으로, 다음과 같은 문제가 발생한다**
1. 결제 단계에서 사용자 대기 시간 증가로 예약 취소율 급증
2. 결제 실패로 인해 좌석 상태 불일치 발생 가능성 증가
3. 서비스 평판 저하 및 고객 신뢰도 하락, 유저 대거 이탈

---

### **대응 방안**

#### 단기 대응
1. 서킷 브레이커 적용: 결제 게이트웨이 지연 시 일정 시간 동안 호출을 중단하여 시스템의 과부하를 방지한다.
2. 사용자 알림: 결제가 지연됨을 사용자에게 알리고 다시 시도할 수 있는 옵션을 제공해주어야 한다.

#### 중기 대응
1. 재시도 로직 개선: 백오프 전략을 활용해 점진적으로 재시도하며 실패 횟수 제한을 설정한다.
2. 대체 API 도입: 결제 게이트웨이가 복구되지 않으면 대체 결제 서비스를 제공.

#### 장기 대응
1. 결제 프로세스 재설계: 결제가 지연되더라도 좌석 예약 상태를 유지하는 방식을 도입한다.

---

## 결론 및 향후 계획

수많은 방법들이 있지만, 현실적인 대응 방안을 고려해서 아래와 같은 향후 계획을 설계해 볼 수 있을 것 같다.  

- **어플리케이션 서버에서의 캐싱 적용**: 성능상의 트레이드오프를 고려하여 캐싱 전략을 적절히 배치한다. 
- **Redis 및 Kafka 최적화**: 클러스터링 및 고가용성 설정을 통해 병목을 최소화한다.
- **자동화된 장애 대응 시스템 구축**: 장애 탐지와 복구를 자동화하여 서비스의 안정성을 극대화한다.
- **성능 모니터링 강화**: Prometheus와 Grafana를 사용해 시스템 상태를 실시간으로 모니터링하고, 병목 지점을 조기에 탐지한다.
