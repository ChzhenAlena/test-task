# Тестовое задание

## Установка и настройка

### Шаг 1: Клонировать репозиторий

```bash
git clone https://github.com/ChzhenAlena/test-task.git
cd test-task
```

### Шаг 2: Создать файл .env

Создайте файл .env в корне проекта на основе шаблона (.env.origin):

```ini
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your_strong_password_here  # Рекомендуется использовать сложный пароль
POSTGRES_DB=test_task_db
REDIS_HOST=redis
REDIS_PORT=6379
JWT_SECRET=your-256-bit-secret-key-must-be-at-least-32-chars  # Минимум 32 символа
```
### Шаг 3: Сборка и запуск приложения с Docker Compose
Собрать и запустить сервисы с помощью Docker Compose:

```bash
docker-compose up --build
```
Дополнительные команды:

Запуск в фоновом режиме: docker-compose up -d --build

Остановка: docker-compose down

Остановка с удалением томов: docker-compose down -v

### Шаг 4: Доступ к приложению
После запуска приложение будет доступно:

Swagger UI: http://localhost:8080/swagger-ui/index.html

Основное API: http://localhost:8080/api/v1/

В качестве тестового пользователя используйте предзаполненные данные в /auth/login  
{  
"login": "ivanov@example.com",  
"password": "password123"  
}

После запуска контейнера логи будут выводиться в консоль и в папку /logs в корне проекта

## Используемые технологии

- **Java 21**
- **Spring Framework (Boot, Data Jpa, Security)**
- **PostgreSQL 15**
- **Redis** (для кеширования)
- **JWT** (для аутентификации)
- **Docker** (для контейнеризации)
- **Flyway** (для миграций)

## Требования

- **Docker версии 20.10.0+**
- **Docker Compose версии 2.0.0+**
- **Для локальной разработки (опционально):** JDK 21, PostgreSQL15, Redis 7+

## Сcылка на тестовое задание
https://docs.google.com/document/d/1C2gBBxBgP_bv_zMfojzhxOf0WeY7Y_mA/edit?usp=sharing&ouid=103621636254374394518&rtpof=true&sd=true
