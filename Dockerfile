# local Build
#FROM openjdk:17-alpine AS builder
#
#WORKDIR /app
#COPY gradlew build.gradle settings.gradle ./
#COPY gradle ./gradle
#COPY src/main ./src/main
#
#RUN chmod +x gradlew
## gradle 이 로컬에 설치되지 않아도 gradle을 사용할 수 있게 해줌
#RUN ./gradlew bootJar
#
#COPY ./build/libs/yongjun-store-*-SNAPSHOT.jar /app.jar
#
#ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/app.jar"]


# gitHub Action Build
# builder stage
FROM openjdk:17-alpine AS builder

COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle
COPY src/main ./src/main

RUN chmod +x gradlew
RUN ./gradlew bootJar

# 빌드된 JAR 파일을 애플리케이션 디렉토리로 복사
COPY /yongjun-store-0.0.1-SNAPSHOT.jar /app.jar

FROM openjdk:17-alpine

#COPY --from=builder /app.jar /app.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "/app.jar"]