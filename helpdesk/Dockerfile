# -------- build step -------------
FROM maven:3.9.7-amazoncorretto-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package       # cria target/helpdesk-*.jar

# -------- runtime ---------------
FROM amazoncorretto:17-alpine
WORKDIR /app

ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080

COPY --from=builder /app/target/*-SNAPSHOT.jar app.jar

CMD ["java","-jar","app.jar"]
