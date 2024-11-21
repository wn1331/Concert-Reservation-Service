create table if not exists concert
(
    id    bigint auto_increment
        primary key,
    title varchar(255) null
);



CREATE TABLE IF NOT EXISTS USER (
                                    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    POINT INT,
                                    NAME VARCHAR(255),
    VERSION INT
    );

create table if not exists CONCERT_SCHEDULE
(
    concert_date date   null,
    concert_id   bigint null,
    id           bigint auto_increment
        primary key
);

create table if not exists CONCERT_SEAT
(
    price               decimal(38, 2)                     null,
    concert_schedule_id bigint                             null,
    created_at          datetime(6)                        null,
    id                  bigint auto_increment
        primary key,
    modified_at         datetime(6)                        null,
    seat_num            varchar(255)                       null,
    status              enum ('EMPTY', 'RESERVED', 'SOLD') null
);

create index idx_concert_seat_schedule
    on CONCERT_SEAT (concert_schedule_id);



create index idx_concert_id_concert_date
    on CONCERT_SCHEDULE (concert_id, concert_date);


create table if not exists RESERVATION
(
    price           decimal(38, 2)                               null,
    concert_seat_id bigint                                       null,
    created_at      datetime(6)                                  null,
    id              bigint auto_increment
        primary key,
    modified_at     datetime(6)                                  null,
    user_id         bigint                                       null,
    status          enum ('CANCELED', 'PAY_SUCCEED', 'RESERVED') null
);

create index idx_concert_reservation_status_created_at
    on RESERVATION (created_at);


create table if not exists payment
(
    price          decimal(38, 2)               null,
    created_at     datetime(6)                  null,
    id             bigint auto_increment
        primary key,
    modified_at    datetime(6)                  null,
    reservation_id bigint                       null,
    status         enum ('CANCELED', 'SUCCEED') null
);

create table if not exists user_point_history
(
    request_point decimal(38, 2)         null,
    created_at    datetime(6)            null,
    id            bigint auto_increment
        primary key,
    user_id       bigint                 null,
    type          enum ('CHARGE', 'PAY') null
);

create table if not exists outbox
(
    created_at  datetime(6)                          null,
    modified_at datetime(6)                          null,
    target_id   bigint                               null,
    id          varchar(255)                         not null
        primary key,
    `key`       varchar(255)                         null,
    payload     varchar(255)                         null,
    topic       varchar(255)                         null,
    status      enum ('INIT', 'RECEIVED', 'SUCCESS') null,
    type        enum ('PAYMENT', 'RESERVATION')      null
);







INSERT INTO PUBLIC.USER (POINT, NAME,VERSION) VALUES (0, '주종훈',0);
INSERT INTO PUBLIC.USER (POINT, NAME,VERSION) VALUES (1000000, '부자',0);

INSERT INTO PUBLIC.CONCERT_SCHEDULE (CONCERT_DATE, CONCERT_ID) VALUES ('2024-12-01', 1);
INSERT INTO PUBLIC.CONCERT_SCHEDULE (CONCERT_DATE, CONCERT_ID) VALUES ('2024-12-02', 1);
INSERT INTO PUBLIC.CONCERT_SCHEDULE (CONCERT_DATE, CONCERT_ID) VALUES ('2023-12-03', 1);



INSERT INTO PUBLIC.CONCERT_SEAT (PRICE, CONCERT_SCHEDULE_ID, CREATED_AT, MODIFIED_AT, SEAT_NUM, STATUS) VALUES (150000.00, 1, '2024-10-17 21:01:57.217771', null, 'A1', 'EMPTY');
INSERT INTO PUBLIC.CONCERT_SEAT (PRICE, CONCERT_SCHEDULE_ID, CREATED_AT, MODIFIED_AT, SEAT_NUM, STATUS) VALUES (150000.00, 1, '2024-10-17 21:01:57.217771', null, 'A2', 'RESERVED');
INSERT INTO RESERVATION (CONCERT_SEAT_ID, CREATED_AT, MODIFIED_AT, USER_ID, STATUS,PRICE) VALUES (2, NOW(), NULL, 2, 'RESERVED',150000);


INSERT INTO PAYMENT (price, created_at, id, modified_at, reservation_id, status) VALUES (150000.00, '2024-11-21 17:58:09.498742', 1, null, 1, 'SUCCEED');

INSERT INTO OUTBOX (created_at, modified_at, id, `key`, payload, topic, status, type) VALUES ('2024-11-21 17:58:09.527198', '2023-11-21 17:58:09.601629', '71b3feb7-d139-45ad-a080-fd9f15773e66', '1', '{"userId":1,"price":150000.00,"reservationId":1,"paymentId":1,"token":"68cf2c00-088e-4e81-ab15-98c9ccd2bfad"}', 'PAYMENT-SUCCESS', 'INIT', 'PAYMENT');
