# checkdev_notification

## Описание проекта

**checkdev_notification** — это микросервис уведомлений, отвечающий за рассылку сообщений пользователям через различные каналы:

- Telegram-бот
- внутренняя система сообщений
- подписки на темы и категории

Сервис обеспечивает централизованную доставку уведомлений о событиях (интервью, отклики, утверждения, отказы, обновления) другим участникам платформы CheckDev. Он тесно интегрируется с другими микросервисами экосистемы — такими как checkdev_auth, checkdev_desc, checkdev_mock, checkdev_generator — и взаимодействует с ними через REST и Eureka.

---

## Основные возможности

- Отправка уведомлений пользователям через Telegram и внутренние сообщения
- Управление подписками на категории и темы (subscribe / unsubscribe)
- Уведомления о событиях интервью, откликов и изменении статусов
- Генерация текстов сообщений на основе шаблонов (`MessagesGenerator`)
- Поддержка авторизации и регистрации через Telegram-бота
- Работа с внутренней системой сообщений (Inner Messages)
- Миграции базы данных с помощью Liquibase
- REST API для интеграции с другими микросервисами

---

## Архитектура проекта
```text
ru.checkdev.notification/
├── NtfSrv.java                           # Точка входа (Spring Boot)
├── config/
│   └── SecurityConfig.java               # Конфигурация безопасности
├── filter/
│   └── CorsFilter.java                   # CORS-фильтр
├── domain/                               # JPA-сущности
│   ├── Base.java
│   ├── Profile.java
│   ├── InnerMessage.java
│   ├── UserTelegram.java
│   ├── SubscribeCategory.java
│   └── SubscribeTopic.java
├── dto/                                  # Data Transfer Objects
│   ├── FeedbackNotificationDTO.java
│   ├── WisherNotifyDTO.java
│   ├── InterviewNotifyDTO.java
│   ├── CancelInterviewNotificationDTO.java
│   ├── WisherApprovedDTO.java
│   ├── WisherDismissedDTO.java
│   ├── InnerMessageDTO.java
│   ├── ProfileTgDTO.java
│   └── CategoryWithTopicDTO.java
├── repository/                           # Spring Data JPA репозитории
│   ├── InnerMessageRepository.java
│   ├── UserTelegramRepository.java
│   ├── SubscribeCategoryRepository.java
│   └── SubscribeTopicRepository.java
├── service/                              # Бизнес-логика
│   ├── NotificationMessagesService.java
│   ├── NotificationMessage.java
│   ├── NotificationMessageTg.java
│   ├── MessagesGenerator.java
│   ├── SubscribeTopicService.java
│   ├── SubscribeCategoryService.java
│   ├── UserTelegramService.java
│   ├── InnerMessageService.java
│   └── EurekaUriProvider.java
├── telegram/                             # Telegram-интеграция
│   ├── Bot.java
│   ├── TgBot.java
│   ├── TgBootFake.java
│   ├── SessionTg.java
│   ├── TgConfig.java
│   ├── config/
│   │   └── TgConfig.java
│   ├── service/
│   │   ├── TgCall.java
│   │   ├── TgAuthCallWebClint.java
│   │   └── FakeTgCallConsole.java
│   └── action/                           # Telegram-диалоги и команды
│       ├── Action.java
│       ├── SaveInnerMessageAction.java
│       ├── notify/
│       │   ├── NotifyAction.java
│       │   └── UnNotifyAction.java
│       ├── reg/                          # Регистрация пользователей
│       │   ├── RegAskNameAction.java
│       │   ├── RegPutNameAction.java
│       │   ├── RegAskEmailAction.java
│       │   ├── RegPutEmailAction.java
│       │   ├── RegCheckEmailAction.java
│       │   └── RegSaveUserAction.java
│       ├── bind/                         # Привязка Telegram к аккаунту
│       │   ├── BindAccountAction.java
│       │   ├── BindAskEmailAction.java
│       │   ├── BindAskPasswordAction.java
│       │   ├── BindPutPasswordAction.java
│       │   ├── BindPutEmailAction.java
│       │   └── UnbindAccountAction.java
│       ├── check/
│       │   └── CheckAction.java
│       ├── info/
│       │   ├── InfoAction.java
│       │   └── UnKnownRequestAction.java
│       └── forget/
│           └── ForgetAction.java
└── web/                                  # REST-контроллеры
    ├── FeedbackNotificationController.java
    ├── NotificationWisherController.java
    ├── NotificationInterviewController.java
    ├── InnerMessageController.java
    ├── SubscribeTopicController.java
    └── SubscribeCategoriesController.java
```

## Технологический стек

| Компонент | Назначение |
|---------|-----------|
| Java 17+ | Язык реализации |
| Spring Boot | Основной фреймворк |
| Spring Security | Защита REST API |
| Spring Data JPA | Работа с БД |
| Liquibase | Миграции |
| PostgreSQL | Основная БД |
| Telegram Bot API | Канал уведомлений |
| Maven | Сборка |
| Jenkins | CI/CD |
| Eureka Client | Service Discovery |

## Конфигурация приложения

Пример `src/main/resources/application.properties`:

```properties
spring.application.name=notification
server.port=9014

spring.datasource.url=jdbc:postgresql://localhost:5432/checkdev_notification
spring.datasource.username=postgres
spring.datasource.password=postgres

spring.jpa.hibernate.ddl-auto=validate
spring.liquibase.change-log=classpath:db/db.changelog-master.xml

telegram.bot.token=${BOT_TOKEN}
telegram.bot.username=@CheckDevBot

eureka.client.service-url.defaultZone=http://localhost:9009/eureka
```
## Основные REST API
| Метод  | Путь                       | Назначение              |
| ------ | -------------------------- | ----------------------- |
| POST   | /notification/feedback     | Уведомление о фидбэке   |
| POST   | /notification/interview    | Уведомление об интервью |
| POST   | /notification/wisher       | Изменение статуса       |
| GET    | /inner-messages            | Внутренние сообщения    |
| POST   | /subscribe/topic           | Подписка на тему        |
| POST   | /subscribe/category        | Подписка на категорию   |
| DELETE | /unsubscribe/topic/{id}    | Отписка                 |
| DELETE | /unsubscribe/category/{id} | Отписка                 |

## Telegram-интеграция
Бот реализован в пакете telegram и поддерживает следующие команды:

| Команда      | Назначение        |
| ------------ | ----------------- |
| /start       | Начало работы     |
| /register    | Регистрация       |
| /bind        | Привязка аккаунта |
| /check       | Проверка статуса  |
| /unsubscribe | Отписка           |
| /info        | Информация        |
| /forget      | Очистка данных    |

## Как запустить локально

### 1.Сборка проекта
```xml
mvn clean package
```
### 2.Запуск
```xml
java -jar target/checkdev_notification-0.0.1-SNAPSHOT.jar
```    
или
```xml
mvn spring-boot:run
```
### 3.Доступ
Приложение будет доступно по адресу: http://localhost:9014
## Интеграция с Eureka
Для регистрации в ```cd_eureka```:
```properties
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
eureka.client.service-url.defaultZone=http://localhost:9009/eureka
```
## Dockerfile (пример)
```properties
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/checkdev_notification-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9014
ENTRYPOINT ["java", "-jar", "app.jar"]
```
Сборка:
```xml
docker build -t checkdev-notification 
```
Запуск:
```xml
docker run -p 9014:9014 checkdev-notification
```
## Jenkins(CI/CD)
```Jenkinsfile``` содержит типичный pipeline:
1.Build — сборка Maven-проекта
2.Test — выполнение unit и integration тестов
3.Deploy — публикация Docker-образа и деплой