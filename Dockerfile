FROM openjdk:17-alpine AS builder

WORKDIR /app
COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle
COPY src/main ./src/main
ARG JAR_FILE=/build/libs/yongjun-store-*.jar
COPY ${JAR_FILE} /app.jar
RUN chmod +x gradlew
# gradle 이 로컬에 설치되지 않아도 gradle을 사용할 수 있게 해줌
RUN ./gradlew bootJar
# "-Dspring.profiles.active=prd",
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prd", "/app.jar"]