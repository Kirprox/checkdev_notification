FROM maven:3.9.6-eclipse-temurin-17 as maven

WORKDIR /app

COPY . /app

RUN mvn package

FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=maven /app/target/notification-1.0.0.jar app.jar
CMD ["sh", "-c", "sleep ${START_DELAY:-15} && java -jar app.jar"]