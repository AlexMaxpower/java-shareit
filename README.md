# ShareIt

Приложение для обмена вещами среди друзей на время: инструментами, гаджетами, книгами, играми. Как каршеринг, только для вещей.
Бекэнд на Spring Boot и фронтэнд в виде Telegram-бота.

## Возможности

Приложение позволяет пользователям, во-первых, возможность рассказывать, какими вещами они готовы поделиться, а во-вторых, находить нужную вещь и брать её в аренду на какое-то время.

## Используемые технологии
REST-сервисы с использованием Spring Boot, Spring Data, Spring Security, Hibernate, PostgreSQL, Keycloak, OAuth 2, Docker Compose.

## Инструкция по запуску приложения
Для корректной работы приложения необходимо наличие на компьютере Docker. Оба сервиса запускаются следующей командой:

```Bash
mvn install
docker-compose up
```

## Аутентификация и авторизация
GET-Запрос для получения code:

http://localhost:8180/realms/shareit-realm/protocol/openid-connect/auth?response_type=code&client_id=shareit-client&state=sidyuf8s67dfisdgfdhdhdth%D1%80%D1%80%D0%B2&scope=openid%20profile&redirect_uri=https://localhost:8080/redirect

POST-Запрос для получения access token:

http://localhost:8180/realms/shareit-realm/protocol/openid-connect/token

Параметры:
```Bash
grant_type: authorization_code
client_id: shareit-client
client_secret: 3H82ksqN62uYregszWS1nsRpBfKkWs9a
code: подставляем результат выполнения первого запроса
```

Демо-пользователь для получения access token:

Логин: demo<br />
Пароль: demo

## Администрирование Keycloak
http://localhost:8180<br />
Логин: admin<br />
Пароль: admin<br />