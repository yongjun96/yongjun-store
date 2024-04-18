# local Build
FROM openjdk:17-alpine AS builder

WORKDIR /app
COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle
COPY src/main ./src/main

RUN chmod +x gradlew
# gradle 이 로컬에 설치되지 않아도 gradle을 사용할 수 있게 해줌
RUN ./gradlew bootJar

WORKDIR /app

#COPY ./build/libs/yongjun-store-*-SNAPSHOT.jar /app.jar
COPY --from=builder /app/build/libs/yongjun-store-*.jar /app.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/app.jar"]


# gitHub Action Build
# builder stage
#FROM openjdk:17-alpine AS builder
#
#WORKDIR /app
#
#COPY gradlew build.gradle settings.gradle ./
#COPY gradle ./gradle
#COPY src/main ./src/main
#
#RUN chmod +x gradlew
#RUN ./gradlew bootJar
#
#FROM openjdk:17-alpine
#
#WORKDIR /app
#
## 빌더 스테이지에서 빌드된 JAR 파일 복사
#COPY --from=builder /app/build/libs/yongjun-store-*.jar /app.jar
#
## 애플리케이션 실행
#ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/app.jar"]