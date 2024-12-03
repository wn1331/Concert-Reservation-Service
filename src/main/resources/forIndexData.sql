
-- Concert 데이터 100개 삽입
CREATE PROCEDURE InsertConcerts()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= 100 DO
        INSERT INTO Concert (title) VALUES (CONCAT('콘서트 ', i));
        SET i = i + 1;
END WHILE;
END;

-- ConcertSchedule 데이터 삽입 (각 콘서트에 대해 10000개의 스케줄)
CREATE PROCEDURE InsertConcertSchedules2()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE j INT DEFAULT 1;
    DECLARE concert_date DATE;
    SET concert_date = '2024-12-01';

    WHILE i <= 100 DO
        SET j = 1;
        WHILE j <= 100 DO
            INSERT INTO concert_schedule (concert_id, concert_date)
            VALUES (i, DATE_ADD(concert_date, INTERVAL j DAY));
            SET j = j + 1;
END WHILE;
        SET i = i + 1;
END WHILE;
END;

-- ConcertSeat 데이터 삽입 (각 스케줄에 대해 1500개의 좌석)
CREATE PROCEDURE InsertConcertSeats3()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE seat_number INT DEFAULT 1;
    DECLARE price DECIMAL(10, 2) DEFAULT 150000;
    DECLARE seat_status ENUM('EMPTY', 'RESERVED', 'SOLD');

    WHILE i <= 10000 DO  -- 총 10000개의 스케줄 * 150좌석 = 1500000 스케줄
        SET seat_number = 1;
        WHILE seat_number <= 150 DO
            -- 랜덤하게 상태 설정 (0: EMPTY, 1: RESERVED, 2: SOLD)
            SET seat_status = 'EMPTY';
INSERT INTO concert_seat (concert_schedule_id, seat_num, price, status)
VALUES (i, CONCAT('A', seat_number), price, seat_status);
SET seat_number = seat_number + 1;
END WHILE;
        SET i = i + 1;
END WHILE;
END;

-- User 데이터 2명 삽입
CREATE PROCEDURE InsertUsers2()
BEGIN
    DECLARE i INT DEFAULT 1; -- 반복을 위한 변수
    WHILE i <= 1000000 DO
            INSERT INTO User (name,version, point)
            VALUES (CONCAT('User_', i),0, 1000000); -- name은 User_1, User_2, ... 형태로 생성
            SET i = i + 1; -- i 값을 증가
        END WHILE;
    END;

-- ConcertReservation 데이터 100만 개 삽입
CREATE PROCEDURE InsertConcertReservations()
BEGIN
    DECLARE i BIGINT DEFAULT 1;
    DECLARE user_id BIGINT DEFAULT 1;
    DECLARE price DECIMAL(10, 2) DEFAULT 150000;
    DECLARE reservation_status ENUM('RESERVED', 'PAY_SUCCEED', 'CANCELED');
    DECLARE created_at TIMESTAMP;

    WHILE i <= 1000000 DO
            SET user_id = IF(i % 2 = 0, 1, 2);

            -- 랜덤하게 상태 설정 (0: RESERVED, 1: PAY_SUCCEED, 2: CANCELED)
            SET reservation_status = CASE FLOOR(RAND() * 3)
                                         WHEN 0 THEN 'RESERVED'
                                         WHEN 1 THEN 'PAY_SUCCEED'
                                         ELSE 'CANCELED'
                END;

            -- 현재 시간부터 1시간 전후로 랜덤한 시간 생성
            SET created_at = DATE_ADD(NOW(), INTERVAL (RAND() * 120 - 60) MINUTE);

            INSERT INTO reservation (user_id, concert_seat_id, price, status, created_at)
            VALUES (user_id, i, price, reservation_status, created_at);

            SET i = i + 1;
        END WHILE;
END;


DELIMITER ;


DELIMITER $$

CREATE PROCEDURE InsertReservations()
BEGIN
    DECLARE i INT DEFAULT 1;

    START TRANSACTION;

    WHILE i <= 1000000 DO
            INSERT INTO concert.reservation (price, concert_seat_id, created_at, modified_at, user_id, status)
            VALUES (50000, i, NOW(), NOW(), i, 'RESERVED');
            SET i = i + 1;

            -- 10,000개씩 커밋하여 메모리 사용량 관리
            IF i % 10000 = 0 THEN
                COMMIT;
                START TRANSACTION;
            END IF;
        END WHILE;

    COMMIT;
END$$

DELIMITER ;


-- 각 프로시저 호출하여 데이터 삽입 실행
CALL InsertConcerts();
CALL InsertConcertSchedules2();
CALL InsertConcertSeats3();
CALL InsertUsers2();
