FROM khipu/openjdk17-alpine:latest AS builder

WORKDIR /usr/src/app

FROM khipu/openjdk17-alpine:latest
COPY ../build/libs/ConcertReservationService-0.0.1-SNAPSHOT.jar ./concert-api.jar
EXPOSE 8088
RUN ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime

# Jenkins 서버에 있는 jar 파일 경로 -> C:\ProgramData\Jenkins\.jenkins\workspace\obt-backend\obt-api\build\libs
# Dockerfile의 경로 -> C:\ProgramData\Jenkins\.jenkins\workspace\obt-backend\obt-api
CMD ["java","-jar","concert-api.jar"]

# hi!
#docker build -t obt-api .
#docker save obt-api > obt-api.tar