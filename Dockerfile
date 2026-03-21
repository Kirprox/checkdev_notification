FROM maven:3.9.6-eclipse-temurin-17

RUN mkdir checkdev_notification

WORKDIR checkdev_notification

COPY . .

RUN mvn package -Dmaven.test.skip=true

CMD ["sh", "-c", "sleep ${START_DELAY:-0} && java -jar target/notification-1.0.0.jar"]