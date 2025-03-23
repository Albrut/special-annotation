# API Requests Guide

Это руководство объясняет, как правильно отправлять запросы к API.

## Эндпоинты

### 1. Инициализация сессии

Для инициализации сессии отправьте `POST`-запрос:

```bash
curl -v -X POST "http://localhost:8080/init-session" \
  -c cookies.txt \
  -H "Content-Type: application/json"
```  

Этот запрос:
- Отправляет `POST`-запрос на эндпоинт `/init-session`.
- Сохраняет cookies в файл `cookies.txt`.
- Устанавливает заголовок `Content-Type: application/json`.

### 2. Создание объекта

#### 2.1 Создание с параметрами в URL

```bash
curl -v -X POST "http://localhost:8080/create?quantity=5&productId=123e4567-e89b-12d3-a456-426614174000" \
  -H "X-Custom-Header: CustomHeaderValue" \
  -b cookies.txt \
  -F "username=TestProduct" \
  -F "file=@./da.txt"
```  

Этот запрос:
- Отправляет `POST`-запрос на эндпоинт `/create`.
- Передаёт параметры `quantity=5` и `productId=123e4567-e89b-12d3-a456-426614174000` в URL.
- Добавляет заголовок `X-Custom-Header: CustomHeaderValue`.
- Использует cookies из `cookies.txt`.
- Передаёт данные формы:
    - `username=TestProduct`.
    - Файл `da.txt`.

#### 2.2 Создание с параметром в пути

```bash
curl -v -X POST "http://localhost:8080/create/10" \
  -H "X-Custom-Header: CustomHeaderValue" \
  -b cookies.txt \
  -F "productId=123e4567-e89b-12d3-a456-426614174000" \
  -F "username=TestProductV2" \
  -F "file=@./da.txt"
```  

Этот запрос:
- Отправляет `POST`-запрос на эндпоинт `/create/10`, где `10` передаётся как параметр в пути.
- Добавляет заголовок `X-Custom-Header: CustomHeaderValue`.
- Использует cookies из `cookies.txt`.
- Передаёт данные формы:
    - `productId=123e4567-e89b-12d3-a456-426614174000`.
    - `username=TestProductV2`.
    - Файл `da.txt`.

## Cookies

Во время работы с API сохраняются cookies, но **необходимо вручную добавить `userId`** в файл `cookies.txt`:

```text
#HttpOnly_localhost    FALSE   /       FALSE   0       JSESSIONID      A39162C73860C625D25665333ED27442
localhost              FALSE   /       FALSE   0       userId          123e4567-e89b-12d3-a456-426614174000
```  

- `JSESSIONID` — идентификатор сессии.
- `userId` — **не добавляется автоматически, его нужно указать вручную**.

Чтобы добавить `userId`, откройте `cookies.txt` и вставьте строку вручную. Без этого некоторые запросы могут не работать.

---
